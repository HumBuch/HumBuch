package de.dhbw.humbuch.viewmodel;

import static de.dhbw.humbuch.test.TestUtils.borrowedMaterialReceivedInPast;
import static de.dhbw.humbuch.test.TestUtils.borrowedMaterialReceivedInPastBorrowUntil;
import static de.dhbw.humbuch.test.TestUtils.schoolYearFirstTermEnded;
import static de.dhbw.humbuch.test.TestUtils.schoolYearFirstTermEndedPlusDays;
import static de.dhbw.humbuch.test.TestUtils.schoolYearSecondTermEndedPlusDays;
import static de.dhbw.humbuch.test.TestUtils.studentInGrade;
import static de.dhbw.humbuch.test.TestUtils.teachingMaterialInFirstTermOfGrade;
import static de.dhbw.humbuch.test.TestUtils.teachingMaterialInSecondTermOfGrade;
import static de.dhbw.humbuch.test.TestUtils.todayPlusDays;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.dhbw.humbuch.guice.GuiceJUnitRunner;
import de.dhbw.humbuch.guice.GuiceJUnitRunner.GuiceModules;
import de.dhbw.humbuch.guice.TestModule;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Dunning;
import de.dhbw.humbuch.model.entity.Dunning.Status;
import de.dhbw.humbuch.model.entity.SchoolYear;
import de.dhbw.humbuch.model.entity.TestPersistenceInitialiser;


@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ TestModule.class })
/** 
 * @author Martin Wentzel
 * @author David Hermann
 */
public class DunningViewModelTest extends BaseTest {

	private final int DEADLINE_FIRST_DUNNING = 15;
	private final int DEADLINE_SECOND_DUNNING = 15;

	private DunningViewModel dunningViewModel;
	private DAO<Dunning> daoDunning;
	private DAO<SchoolYear> daoSchoolYear;
	private DAO<BorrowedMaterial> daoBorrowedMaterial;
	private Properties properties;

	@Inject
	public void setInjected(TestPersistenceInitialiser persistenceInitialiser,
			Provider<EntityManager> emProvider,
			DunningViewModel dunningViewModel, DAO<Dunning> daoDunning,
			DAO<SchoolYear> daoSchoolYear,
			DAO<BorrowedMaterial> daoBorrowedMaterial, Properties properties) {
		super.setInjected(persistenceInitialiser, emProvider);

		this.dunningViewModel = dunningViewModel;
		this.daoDunning = daoDunning;
		this.daoSchoolYear = daoSchoolYear;
		this.daoBorrowedMaterial = daoBorrowedMaterial;
		this.properties = properties;
	}

	private void persistSomeEmptyDunnings(int amount, Status status) {
		for (int i = 0; i < amount; i++) {
			Dunning dunning = new Dunning.Builder(null).status(status).build();
			daoDunning.insert(dunning);
		}
	}

	/**
	 * Persist a {@link BorrowedMaterial} which is due after the term end
	 */
	private void persistBorrowedMaterialDueAfterFirstTerm() {
		daoBorrowedMaterial.insert(borrowedMaterialReceivedInPast(
				studentInGrade(6), teachingMaterialInFirstTermOfGrade(6)));
	}

	/**
	 * Persist a {@link BorrowedMaterial} which is due after manual borrowUntil
	 * date
	 */
	private void persistBorrowedMaterialDueAfterManualEndDate() {
		daoBorrowedMaterial.insert(borrowedMaterialReceivedInPastBorrowUntil(
				studentInGrade(6), teachingMaterialInFirstTermOfGrade(6),
				todayPlusDays(-DEADLINE_FIRST_DUNNING)));
	}

	private void persistBorrowedMaterialManualEndDateInFuture() {
		daoBorrowedMaterial.insert(borrowedMaterialReceivedInPastBorrowUntil(
				studentInGrade(6), teachingMaterialInFirstTermOfGrade(6),
				todayPlusDays(1)));
	}

	private void persistBorrowedMaterialDueAfterFirstTermEndInLastSchoolYear() {
		daoBorrowedMaterial.insert(borrowedMaterialReceivedInPast(
				studentInGrade(7), teachingMaterialInFirstTermOfGrade(6)));
	}

	private void persistBorrowedMaterialDueAfterSecondTermEndInLastSchoolYear() {
		daoBorrowedMaterial.insert(borrowedMaterialReceivedInPast(
				studentInGrade(7), teachingMaterialInSecondTermOfGrade(6)));
	}

	@Before
	public void refreshDunningViewModel() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("dun_firstDunningDeadline", "" + DEADLINE_FIRST_DUNNING);
		properties.settings.set(map);
		map.put("dun_secondDunningDeadline", "" + DEADLINE_SECOND_DUNNING);
		properties.settings.set(map);
		dunningViewModel.refresh();
	}

	@Test
	public void testStateInitialisation() {
		// states should be initialised (not null)
		assertNotNull(dunningViewModel.dunnings.get());
	}

	@Test
	public void testInitialDunningAmount() {
		// amount of dunnings should be 0 with an empty database
		assertEquals(0, dunningViewModel.dunnings.get().size());
	}

	@Test
	public void testAddTwoSentDunnings() {
		// add two sent dunnings, check amount
		persistSomeEmptyDunnings(2, Status.SENT);
		dunningViewModel.refresh();
		assertEquals(2, dunningViewModel.dunnings.get().size());
	}

	public void testAddTwoSentThreeClosedDunnings() {
		// add two sent dunnings
		persistSomeEmptyDunnings(2, Status.SENT);
		// add three closed dunnings, check amount
		persistSomeEmptyDunnings(3, Status.CLOSED);
		dunningViewModel.refresh();
		assertEquals(5, dunningViewModel.dunnings.get().size());
	}

	@Test
	public void testAddFourOpenedDunnings() {
		// add four opened dunnings, check amount
		persistSomeEmptyDunnings(4, Status.OPENED);
		dunningViewModel.refresh();
		assertEquals(4, dunningViewModel.dunnings.get().size());
	}

	@Test
	public void testAutoDunningAfterFirstTerm() {
		// BorrowedMaterial due after end of term
		daoSchoolYear
				.insert(schoolYearFirstTermEndedPlusDays(DEADLINE_FIRST_DUNNING));
		persistBorrowedMaterialDueAfterFirstTerm();
		dunningViewModel.refresh();
		assertEquals(1, dunningViewModel.dunnings.get().size());
	}

	@Test
	public void testAutoDunningAfterManualLendingDate() {
		// BorrowedMaterial due after manual lending date
		daoSchoolYear.insert(schoolYearFirstTermEnded());
		persistBorrowedMaterialDueAfterManualEndDate();
		dunningViewModel.refresh();
		assertEquals(1, dunningViewModel.dunnings.get().size());
	}

	@Test
	public void testNoActionBeforeManualLendingDate() {
		// BorrowedMaterial due in future (manual lending date)
		daoSchoolYear.insert(schoolYearFirstTermEnded());
		persistBorrowedMaterialManualEndDateInFuture();
		dunningViewModel.refresh();
		assertEquals(0, dunningViewModel.dunnings.get().size());
	}

	@Test
	public void testAutoDunningAfterFirstTermEndOfLastSchoolYear() {
		// BorrowedMaterial due after term end of last school year
		daoSchoolYear.insert(schoolYearSecondTermEndedPlusDays(DEADLINE_FIRST_DUNNING));
		persistBorrowedMaterialDueAfterFirstTermEndInLastSchoolYear();
		dunningViewModel.refresh();
		assertEquals(1, dunningViewModel.dunnings.get().size());
	}

	@Test
	public void testAutoDunningAfterSecondTermInLastSchoolYear() {
		// BorrowedMaterial due after term end of last school year
		daoSchoolYear.insert(schoolYearSecondTermEndedPlusDays(DEADLINE_FIRST_DUNNING));
		persistBorrowedMaterialDueAfterSecondTermEndInLastSchoolYear();
		dunningViewModel.refresh();
		assertEquals(1, dunningViewModel.dunnings.get().size());
	}

}
