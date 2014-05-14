package de.dhbw.humbuch.viewmodel;

import static de.dhbw.humbuch.test.TestUtils.borrowedMaterialReceivedInPast;
import static de.dhbw.humbuch.test.TestUtils.studentInGrade;
import static de.dhbw.humbuch.test.TestUtils.teachingMaterialInFirstTermOfGrade;
import static de.dhbw.humbuch.test.TestUtils.teachingMaterialInSecondTermOfGrade;
import static de.dhbw.humbuch.test.TestUtils.todayPlusDays;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.dhbw.humbuch.guice.GuiceJUnitRunner;
import de.dhbw.humbuch.guice.GuiceJUnitRunner.GuiceModules;
import de.dhbw.humbuch.guice.TestModuleWithoutSingletons;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.SchoolYear;
import de.dhbw.humbuch.model.entity.TestPersistenceInitialiser;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ TestModuleWithoutSingletons.class })
public class ReturnViewModelTest extends BaseTest {

	private ReturnViewModel vm;
	private DAO<SchoolYear> daoSchoolYear;
	private DAO<BorrowedMaterial> daoBorrowedMaterial;

	@Inject
	public void setInjected(TestPersistenceInitialiser persistenceInitialiser,
			Provider<EntityManager> emProvider,
			ReturnViewModel returnViewModel, DAO<SchoolYear> daoSchoolYear,
			DAO<BorrowedMaterial> daoBorrowedMaterial) {
		this.daoSchoolYear = daoSchoolYear;
		this.daoBorrowedMaterial = daoBorrowedMaterial;
		super.setInjected(persistenceInitialiser, emProvider);

		this.vm = returnViewModel;
	}

	private void persistSchoolYear(int fromDays, int endFirstTermDays,
			int beginSecondTermDays, int toDays) {
		SchoolYear schoolYear = new SchoolYear.Builder("now",
				todayPlusDays(fromDays), todayPlusDays(toDays))
				.endFirstTerm(todayPlusDays(endFirstTermDays))
				.beginSecondTerm(todayPlusDays(beginSecondTermDays)).build();
		daoSchoolYear.insert(schoolYear);
	}

	private void persistSchoolYearFirstTermBegun() {
		persistSchoolYear(-1, 1, 1, 2);
	}

	private void persistSchoolYearFirstTermEnded() {
		persistSchoolYear(-2, -1, 1, 2);
	}

	private void persistSchoolYearSecondTermBegun() {
		persistSchoolYear(-3, -2, -1, 1);
	}

	private void persistSchoolYearSecondTermEnded() {
		persistSchoolYear(-9, -8, -7, -6);
	}

	/**
	 * Persist a {@link BorrowedMaterial} which is due after the first term end
	 */
	private BorrowedMaterial persistBorrowedMaterialDueAfterFirstTermOfThisGrade() {
		BorrowedMaterial borrowedMaterial = borrowedMaterialReceivedInPast(
				studentInGrade(6), teachingMaterialInFirstTermOfGrade(6));
		return daoBorrowedMaterial.insert(borrowedMaterial);
	}

	/**
	 * Persist a {@link BorrowedMaterial} which is due after the second term end
	 */
	private BorrowedMaterial persistBorrowedMaterialDueAfterSecondTermOfThisGrade() {
		BorrowedMaterial borrowedMaterial = borrowedMaterialReceivedInPast(
				studentInGrade(6), teachingMaterialInSecondTermOfGrade(6));
		return daoBorrowedMaterial.insert(borrowedMaterial);
	}

	/**
	 * Persist a {@link BorrowedMaterial} which is due after the second term end
	 * of last grade
	 */
	private BorrowedMaterial persistBorrowedMaterialDueAfterSecondTermOfLastGrade() {
		BorrowedMaterial borrowedMaterial = borrowedMaterialReceivedInPast(
				studentInGrade(6), teachingMaterialInSecondTermOfGrade(5));
		return daoBorrowedMaterial.insert(borrowedMaterial);
	}
	
	/**
	 * Persist a {@link BorrowedMaterial} which is due after the first term end
	 * of last grade
	 */
	private BorrowedMaterial persistBorrowedMaterialDueAfterFirstTermOfLastGrade() {
		BorrowedMaterial borrowedMaterial = borrowedMaterialReceivedInPast(
				studentInGrade(6), teachingMaterialInFirstTermOfGrade(5));
		return daoBorrowedMaterial.insert(borrowedMaterial);
	}

	@Before
	public void refreshViewModel() {
		vm.refresh();
	}

	@Test
	public void testStateInitialisation() {
		assertNotNull(vm.returnListStudent.get());
	}

	@Test
	public void testNoBorrowedMaterials() {
		assertEquals(0, vm.returnListStudent.get().size());
	}

	@Test
	public void testZeroToReturnWhenInFirstTermBorrowedUntilSecondTerm() {
		persistSchoolYearFirstTermBegun();
		persistBorrowedMaterialDueAfterSecondTermOfThisGrade();
		vm.refresh();
		assertEquals(0, vm.returnListStudent.get().size());
	}

	@Test
	public void testOneToReturnWhenAfterFirstTermBorrowedUntilFirstTerm() {
		persistSchoolYearFirstTermEnded();
		persistBorrowedMaterialDueAfterFirstTermOfThisGrade();
		vm.refresh();
		assertEquals(1, vm.returnListStudent.get().size());
	}

	@Test
	public void testOneToReturnWhenInSecondTermBorrowedUntilFirstTerm() {
		persistSchoolYearSecondTermBegun();
		persistBorrowedMaterialDueAfterFirstTermOfThisGrade();
		vm.refresh();
		assertEquals(1, vm.returnListStudent.get().size());
	}

	@Test
	public void testOneToReturnWhenAfterSecondTermBorrowedUntilFirstTerm() {
		persistSchoolYearSecondTermEnded();
		persistBorrowedMaterialDueAfterFirstTermOfThisGrade();
		vm.refresh();
		assertEquals(1, vm.returnListStudent.get().size());
	}

	@Test
	public void testOneToReturnWhenAfterSecondTermBorrowedUntilSecondTerm() {
		persistSchoolYearSecondTermEnded();
		persistBorrowedMaterialDueAfterSecondTermOfThisGrade();
		vm.refresh();
		assertEquals(1, vm.returnListStudent.get().size());
	}

	@Test
	public void testOneToReturnWhenInFirstTermBorrowedUntilSecondTermOfLastGrade() {
		persistSchoolYearSecondTermEnded();
		persistSchoolYearFirstTermBegun();
		persistBorrowedMaterialDueAfterSecondTermOfLastGrade();
		vm.refresh();
		assertEquals(1, vm.returnListStudent.get().size());
	}
	
	@Test
	public void testOneToReturnWhenInSecondTermBorrowedUntilSecondTermOfLastGrade() {
		persistSchoolYearSecondTermEnded();
		persistSchoolYearSecondTermBegun();
		persistBorrowedMaterialDueAfterSecondTermOfLastGrade();
		vm.refresh();
		assertEquals(1, vm.returnListStudent.get().size());
	}
	
	@Test
	public void testOneToReturnWhenInSecondTermBorrowedUntilFirstTermOfLastGrade() {
		persistSchoolYearSecondTermEnded();
		persistSchoolYearSecondTermBegun();
		persistBorrowedMaterialDueAfterFirstTermOfLastGrade();
		vm.refresh();
		assertEquals(1, vm.returnListStudent.get().size());
	}
	
	@Test
	public void testOneToReturnWhenInFirstTermBorrowedUntilFirstTermOfLastGrade() {
		persistSchoolYearSecondTermEnded();
		persistSchoolYearFirstTermBegun();
		persistBorrowedMaterialDueAfterFirstTermOfLastGrade();
		vm.refresh();
		assertEquals(1, vm.returnListStudent.get().size());
	}
	
	@Test
	public void testZeroToReturnAfterReturningOneToReturn() {
		persistSchoolYearFirstTermEnded();
		BorrowedMaterial borrowedMaterial = persistBorrowedMaterialDueAfterFirstTermOfThisGrade();
		vm.refresh();
		assertEquals(1, vm.returnListStudent.get().size());
		Collection<BorrowedMaterial> borrowedMaterials = new ArrayList<>();
		borrowedMaterials.add(borrowedMaterial);
		vm.setBorrowedMaterialsReturned(borrowedMaterials);
		vm.refresh();
		assertEquals(0, vm.returnListStudent.get().size());
	}
}