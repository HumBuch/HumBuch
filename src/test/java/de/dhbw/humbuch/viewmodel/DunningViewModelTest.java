package de.dhbw.humbuch.viewmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.Date;
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
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.SchoolYear;
import de.dhbw.humbuch.model.entity.SchoolYear.Term;
import de.dhbw.humbuch.model.entity.SettingsEntry;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.TeachingMaterial;
import de.dhbw.humbuch.model.entity.TestPersistenceInitialiser;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ TestModule.class })
public class DunningViewModelTest extends BaseTest {
	private DunningViewModel dunningViewModel;
	private DAO<Dunning> daoDunning;
	private DAO<SchoolYear> daoSchoolYear;
	private DAO<BorrowedMaterial> daoBorrowedMaterial;
	private DAO<SettingsEntry> daoSettingsEntry;
	private Properties properties;

	@Inject
	public void setInjected(TestPersistenceInitialiser persistenceInitialiser,
			Provider<EntityManager> emProvider,
			DunningViewModel dunningViewModel, DAO<Dunning> daoDunning,
			DAO<SchoolYear> daoSchoolYear,
			DAO<BorrowedMaterial> daoBorrowedMaterial,
			Properties properties) {		
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

	private Date todayPlusDays(int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}

	/**
	 * Persist the current {@link SchoolYear} - end of first and beginning of
	 * second term is yesterday, beginning of school year 10 days in the past,
	 * end of school year 10 days in the future
	 */
	private void persistSchoolYear() {
		SchoolYear schoolYear = new SchoolYear.Builder("now",
				todayPlusDays(-40), todayPlusDays(10))
				.endFirstTerm(todayPlusDays(-20))
				.beginSecondTerm(todayPlusDays(-20)).build();
		daoSchoolYear.insert(schoolYear);
	}

	/**
	 * Persist a {@link BorrowedMaterial} which is due after the term end
	 */
	private void persistBorrowedMaterialDueAfterTermEndDate() {
		Grade grade = new Grade.Builder(5, "").build();
		Student student = new Student.Builder(0, "John", "Doe", null, grade)
				.build();
		TeachingMaterial teachingMaterial = new TeachingMaterial.Builder(null,
				"FooBook1", null, todayPlusDays(-20)).fromGrade(5)
				.fromTerm(Term.FIRST).toGrade(5).toTerm(Term.FIRST).build();
		BorrowedMaterial borrowedMaterial = new BorrowedMaterial.Builder(
				student, teachingMaterial, todayPlusDays(-25)).received(true)
				.build();
		daoBorrowedMaterial.insert(borrowedMaterial);
	}

	/**
	 * Persist a {@link BorrowedMaterial} which is due after manual borrowUntil
	 * date
	 */
	private void persistBorrowedMaterialDueAfterManualEndDate() {
		Grade grade = new Grade.Builder(5, "").build();
		Student student = new Student.Builder(1, "John", "Foo", null, grade)
				.build();
		TeachingMaterial teachingMaterial = new TeachingMaterial.Builder(null,
				"FooBook1", null, todayPlusDays(-20)).build();
		BorrowedMaterial borrowedMaterial = new BorrowedMaterial.Builder(
				student, teachingMaterial, todayPlusDays(-5))
				.borrowUntil(todayPlusDays(-20)).received(true).build();
		daoBorrowedMaterial.insert(borrowedMaterial);
	}

	private void persistBorrowedMaterialManualEndDateInFuture() {
		Grade grade = new Grade.Builder(5, "").build();
		Student student = new Student.Builder(2, "Peter", "Doe", null, grade)
				.build();
		TeachingMaterial teachingMaterial = new TeachingMaterial.Builder(null,
				"FooBook1", null, todayPlusDays(-20)).build();
		BorrowedMaterial borrowedMaterial = new BorrowedMaterial.Builder(
				student, teachingMaterial, todayPlusDays(-5))
				.borrowUntil(todayPlusDays(10)).received(true).build();
		daoBorrowedMaterial.insert(borrowedMaterial);
	}
	
	private void persistBorrowedMaterialDueAfterFirstTermEndInLastSchoolYear() {
		Grade grade = new Grade.Builder(5, "").build();
		Student student = new Student.Builder(2, "Peter", "Doe", null, grade)
				.build();
		TeachingMaterial teachingMaterial = new TeachingMaterial.Builder(null,
				"FooBook1", null, todayPlusDays(-20)).fromGrade(4)
				.fromTerm(Term.FIRST).toGrade(4).toTerm(Term.FIRST).build();
		BorrowedMaterial borrowedMaterial = new BorrowedMaterial.Builder(
				student, teachingMaterial, todayPlusDays(-5)).received(true)
				.build();
		daoBorrowedMaterial.insert(borrowedMaterial);
	}

	private void persistBorrowedMaterialDueAfterSecondTermEndInLastSchoolYear() {
		Grade grade = new Grade.Builder(5, "").build();
		Student student = new Student.Builder(2, "Peter", "Doe", null, grade)
				.build();
		TeachingMaterial teachingMaterial = new TeachingMaterial.Builder(null,
				"FooBook1", null, todayPlusDays(-20)).fromGrade(4)
				.fromTerm(Term.FIRST).toGrade(4).toTerm(Term.SECOND).build();
		BorrowedMaterial borrowedMaterial = new BorrowedMaterial.Builder(
				student, teachingMaterial, todayPlusDays(-5)).received(true)
				.build();
		daoBorrowedMaterial.insert(borrowedMaterial);
	}
	
	@Before
	public void refreshDunningViewModel() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("dun_firstDunningDeadline", "15");
		properties.settings.set(map);
		map.put("dun_secondDunningDeadline", "15");
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
	public void testAutoDunningAfterEndOfTerm() {
		// BorrowedMaterial due after end of term
		persistSchoolYear();
		persistBorrowedMaterialDueAfterTermEndDate();
		dunningViewModel.refresh();
		assertEquals(1, dunningViewModel.dunnings.get().size());
	}
	
	@Test
	public void testAutoDunningAfterManualLendingDate() {
		// BorrowedMaterial due after manual lending date
		persistSchoolYear();
		persistBorrowedMaterialDueAfterManualEndDate();
		dunningViewModel.refresh();
		assertEquals(1, dunningViewModel.dunnings.get().size());
	}
	
	@Test
	public void testNoActionBeforeManualLendingDate() {
		// BorrowedMaterial due in future (manual lending date)
		persistSchoolYear();
		persistBorrowedMaterialManualEndDateInFuture();
		dunningViewModel.refresh();
		assertEquals(0, dunningViewModel.dunnings.get().size());
	}
	
	@Test
	public void testAutoDunningAfterFirstTermEndOfLastSchoolYear() {
		// BorrowedMaterial due after term end of last school year
		persistSchoolYear();
		persistBorrowedMaterialDueAfterFirstTermEndInLastSchoolYear();
		dunningViewModel.refresh();
		assertEquals(1, dunningViewModel.dunnings.get().size());
	}
	
	@Test
	public void testAutoDunningAfterSecondTermInLastSchoolYear() {
		// BorrowedMaterial due after term end of last school year
		persistSchoolYear();
		persistBorrowedMaterialDueAfterSecondTermEndInLastSchoolYear();
		dunningViewModel.refresh();
		assertEquals(1, dunningViewModel.dunnings.get().size());
	}
	
}
