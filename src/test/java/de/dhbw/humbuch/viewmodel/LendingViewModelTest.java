package de.dhbw.humbuch.viewmodel;

import static de.dhbw.humbuch.test.TestUtils.grade;
import static de.dhbw.humbuch.test.TestUtils.schoolYearFirstTermStarted;
import static de.dhbw.humbuch.test.TestUtils.schoolYearSecondTermEnded;
import static de.dhbw.humbuch.test.TestUtils.schoolYearSecondTermStarted;
import static de.dhbw.humbuch.test.TestUtils.studentInGrade;
import static de.dhbw.humbuch.test.TestUtils.teachingMaterialInBothTermsOfGrade;
import static de.dhbw.humbuch.test.TestUtils.teachingMaterialInFirstTermOfGrade;
import static de.dhbw.humbuch.test.TestUtils.teachingMaterialInSecondTermOfGrade;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.dhbw.humbuch.guice.GuiceJUnitRunner;
import de.dhbw.humbuch.guice.GuiceJUnitRunner.GuiceModules;
import de.dhbw.humbuch.guice.TestModuleWithoutSingletons;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.SchoolYear;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.TeachingMaterial;
import de.dhbw.humbuch.model.entity.TestPersistenceInitialiser;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ TestModuleWithoutSingletons.class })
public class LendingViewModelTest extends BaseTest {

	private final static Logger LOG = LoggerFactory
			.getLogger(LendingViewModelTest.class);

	private LendingViewModel vm;
	private DAO<Grade> daoGrade;
	private DAO<Student> daoStudent;
	private DAO<SchoolYear> daoSchoolYear;
	private DAO<TeachingMaterial> daoTeachingMaterial;

	@Inject
	public void setInjected(TestPersistenceInitialiser persistenceInitialiser,
			Provider<EntityManager> emProvider,
			LendingViewModel lendingViewModel, DAO<Grade> daoGrade,
			DAO<Student> daoStudent, DAO<SchoolYear> daoSchoolYear,
			DAO<TeachingMaterial> daoTeachingMaterial) {
		this.daoGrade = daoGrade;
		this.daoStudent = daoStudent;
		this.daoSchoolYear = daoSchoolYear;
		this.daoTeachingMaterial = daoTeachingMaterial;
		super.setInjected(persistenceInitialiser, emProvider);

		this.vm = lendingViewModel;
	}

	private int amountOfTeachingMaterialsInMaterialListGrades() {
		LOG.info("amountOfTeachingMaterialsInMaterialListGrades");
		int amount = 0;
		Map<Grade, Map<TeachingMaterial, Integer>> gradeList = vm.materialListGrades
				.get();
		LOG.info("gradeList: " + gradeList);
		for (Grade grade : gradeList.keySet()) {
			LOG.info("grade: " + grade);
			Map<TeachingMaterial, Integer> materialList = gradeList.get(grade);
			LOG.info("materialList: " + materialList);
			LOG.info("materialListKeys: " + materialList.keySet());
			for (int amountInGrade : materialList.values()) {
				LOG.info("amountInGrade: " + amountInGrade);
				amount += amountInGrade;
			}
		}
		return amount;
	}

	@Before
	public void refreshViewModel() {
		vm.refresh();
	}

	@Test
	public void testStateInitialisation() {
		assertNotNull(vm.studentsWithUnreceivedBorrowedMaterials.get());
		assertNotNull(vm.teachingMaterials.get());
	}

	@Test
	public void testStateMaterialListGrades() {
		Set<Grade> grades = new HashSet<>();
		Grade grade = daoGrade.insert(grade(6));
		vm.refresh();
		grades.add(grade);
		vm.generateMaterialListGrades(grades);
		assertNotNull(vm.materialListGrades.get());
	}

	@Test
	public void testOneToLendWhenInFirstTermBorrowedInBothTerms() {
		daoSchoolYear.insert(schoolYearFirstTermStarted());
		daoTeachingMaterial.insert(teachingMaterialInBothTermsOfGrade(6));
		daoStudent.insert(studentInGrade(6));
		vm.refresh();
		assertEquals(1, vm.studentsWithUnreceivedBorrowedMaterials.get().size());
	}

	@Test
	public void testOneToLendWhenInSecondTermBorrowedInBothTerms() {
		daoSchoolYear.insert(schoolYearSecondTermStarted());
		daoTeachingMaterial.insert(teachingMaterialInBothTermsOfGrade(6));
		daoStudent.insert(studentInGrade(6));
		vm.refresh();
		assertEquals(1, vm.studentsWithUnreceivedBorrowedMaterials.get().size());
	}

	@Test
	public void testOneToLendWhenInFirstTermBorrowedInFirstTerm() {
		daoSchoolYear.insert(schoolYearFirstTermStarted());
		daoTeachingMaterial.insert(teachingMaterialInFirstTermOfGrade(6));
		daoStudent.insert(studentInGrade(6));
		vm.refresh();
		assertEquals(1, vm.studentsWithUnreceivedBorrowedMaterials.get().size());
	}

	@Test
	public void testOneToLendWhenInSecondTermBorrowedInSecondTerm() {
		daoSchoolYear.insert(schoolYearSecondTermStarted());
		daoTeachingMaterial.insert(teachingMaterialInSecondTermOfGrade(6));
		daoStudent.insert(studentInGrade(6));
		vm.refresh();
		assertEquals(1, vm.studentsWithUnreceivedBorrowedMaterials.get().size());
	}

	@Test
	public void testZeroToLendWhenInSecondTermBorrowedInFirstTerm() {
		daoSchoolYear.insert(schoolYearSecondTermStarted());
		daoTeachingMaterial.insert(teachingMaterialInFirstTermOfGrade(6));
		daoStudent.insert(studentInGrade(6));
		vm.refresh();
		assertEquals(0, vm.studentsWithUnreceivedBorrowedMaterials.get().size());
	}

	@Test
	public void testZeroToLendWhenInFirstTermBorrowedInSecondTerm() {
		daoSchoolYear.insert(schoolYearFirstTermStarted());
		daoTeachingMaterial.insert(teachingMaterialInSecondTermOfGrade(6));
		daoStudent.insert(studentInGrade(6));
		vm.refresh();
		assertEquals(0, vm.studentsWithUnreceivedBorrowedMaterials.get().size());
	}

	@Test
	public void testZeroToLendWhenAfterSecondTermBorrowedInSecondTerm() {
		daoSchoolYear.insert(schoolYearSecondTermEnded());
		daoTeachingMaterial.insert(teachingMaterialInSecondTermOfGrade(6));
		daoStudent.insert(studentInGrade(6));
		vm.refresh();
		assertEquals(0, vm.studentsWithUnreceivedBorrowedMaterials.get().size());
	}

	@Test
	public void testZeroToLendWhenAfterSecondTermBorrowedInFirstTerm() {
		daoSchoolYear.insert(schoolYearSecondTermEnded());
		daoTeachingMaterial.insert(teachingMaterialInFirstTermOfGrade(6));
		daoStudent.insert(studentInGrade(6));
		vm.refresh();
		assertEquals(0, vm.studentsWithUnreceivedBorrowedMaterials.get().size());
	}

	@Test
	public void testZeroToLendWhenAfterSecondTermBorrowedInBothTerms() {
		daoSchoolYear.insert(schoolYearSecondTermEnded());
		daoTeachingMaterial.insert(teachingMaterialInBothTermsOfGrade(6));
		daoStudent.insert(studentInGrade(6));
		vm.refresh();
		assertEquals(0, vm.studentsWithUnreceivedBorrowedMaterials.get().size());
	}

	@Test
	public void testMarkUnreceivedBorrowedMaterialAsReceived() {
		daoSchoolYear.insert(schoolYearFirstTermStarted());
		daoTeachingMaterial.insert(teachingMaterialInBothTermsOfGrade(6));
		daoStudent.insert(studentInGrade(6));
		vm.refresh();
		assertEquals(1, vm.studentsWithUnreceivedBorrowedMaterials.get().size());

		Map<Grade, Map<Student, List<BorrowedMaterial>>> map = vm.studentsWithUnreceivedBorrowedMaterials
				.get();
		BorrowedMaterial borrowedMaterial = map.values().iterator().next()
				.values().iterator().next().iterator().next();
		Set<BorrowedMaterial> borrowedMaterials = new HashSet<>();
		borrowedMaterials.add(borrowedMaterial);
		vm.setBorrowedMaterialsReceived(borrowedMaterials);
		vm.refresh();
		assertEquals(0, vm.studentsWithUnreceivedBorrowedMaterials.get().size());
	}

	@Test
	public void testGenerateMaterialListOneGradeOneStudentOneBook() {
		daoSchoolYear.insert(schoolYearFirstTermStarted());
		daoTeachingMaterial.insert(teachingMaterialInBothTermsOfGrade(6));
		daoStudent.insert(studentInGrade(6));
		vm.refresh();
		vm.generateMaterialListGrades(new HashSet<>(daoGrade.findAll()));
		assertEquals(1, (int) vm.materialListGrades.get().values().iterator()
				.next().values().iterator().next());
	}

	@Test
	public void testGenerateMaterialListOneGradeTwoStudentsTwoBooks() {
		daoSchoolYear.insert(schoolYearFirstTermStarted());
		daoTeachingMaterial.insert(teachingMaterialInBothTermsOfGrade(6));
		daoStudent.insert(studentInGrade(6));
		daoStudent.insert(studentInGrade(6));
		vm.refresh();
		vm.generateMaterialListGrades(new HashSet<>(daoGrade.findAll()));
		assertEquals(2, amountOfTeachingMaterialsInMaterialListGrades());
	}

	@Test
	public void testGenerateMaterialListTwoGradesThreeStudentsThreeBooks() {
		daoSchoolYear.insert(schoolYearFirstTermStarted());
		daoTeachingMaterial.insert(teachingMaterialInBothTermsOfGrade(6));
		daoStudent.insert(studentInGrade(6));
		daoStudent.insert(studentInGrade(6));
		daoTeachingMaterial.insert(teachingMaterialInBothTermsOfGrade(7));
		daoStudent.insert(studentInGrade(7));
		vm.refresh();
		vm.generateMaterialListGrades(new HashSet<>(daoGrade.findAll()));
		assertEquals(3, amountOfTeachingMaterialsInMaterialListGrades());
	}

	@Test
	public void testGenerateMaterialListThreeGradesFourStudentsThreeBooks() {
		daoSchoolYear.insert(schoolYearFirstTermStarted());
		daoTeachingMaterial.insert(teachingMaterialInBothTermsOfGrade(6));
		daoStudent.insert(studentInGrade(6));
		daoStudent.insert(studentInGrade(6));
		daoTeachingMaterial.insert(teachingMaterialInBothTermsOfGrade(7));
		daoStudent.insert(studentInGrade(7));
		daoStudent.insert(studentInGrade(8));
		vm.refresh();
		vm.generateMaterialListGrades(new HashSet<>(daoGrade.findAll()));
		assertEquals(3, amountOfTeachingMaterialsInMaterialListGrades());
	}
}
