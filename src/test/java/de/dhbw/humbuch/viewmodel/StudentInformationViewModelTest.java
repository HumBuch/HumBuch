package de.dhbw.humbuch.viewmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

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
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.TestPersistenceInitialiser;

/**
 * Test persisting of "uploaded" students in StudentInformationViewModel.
 * Test different kinds of persisting: delta import and non-delta import. 
 * 
 * @author Benjamin Räthlein
 *
 */
@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ TestModuleWithoutSingletons.class })
public class StudentInformationViewModelTest extends BaseTest {

	private StudentInformationViewModel vm;
	private DAO<Student> daoStudent;

	@Inject
	public void setInjected(TestPersistenceInitialiser persistenceInitialiser,
			Provider<EntityManager> emProvider,
			StudentInformationViewModel studentInformationViewModel,
			DAO<Student> daoStudent) {
		this.daoStudent = daoStudent;
		super.setInjected(persistenceInitialiser, emProvider);

		this.vm = studentInformationViewModel;
	}

	private void importTwoStudents(boolean fullImport) {
		Grade grade = new Grade.Builder(5, "").build();
		List<Student> students = new ArrayList<Student>();
		Student student1 = new Student.Builder(4, "Roberto", "Rastapopoulos", null, grade).build();
		Student student2 = new Student.Builder(5, "Bianca", "Castafiore", null, grade).build();
		students.add(student1);
		students.add(student2);
		vm.persistStudents(students, fullImport);
	}
	
	private void importThreeStudents(){
		Grade grade = new Grade.Builder(7, "").build();
		List<Student> students = new ArrayList<Student>();
		Student student1 = new Student.Builder(1, "Tim", "Tintin", null, grade).build();
		Student student2 = new Student.Builder(2, "Struppi", "Milou", null, grade).build();
		Student student3 = new Student.Builder(3, "Archibald", "Haddock", null, grade).build();
		students.add(student1);
		students.add(student2);
		students.add(student3);
		vm.persistStudents(students, true);
	}
	
	private void overrideStudentWithId(int id){
		Grade grade = new Grade.Builder(7, "").build();
		List<Student> students = new ArrayList<Student>();
		Student student1 = new Student.Builder(id, "Balduin", "Bienlein", null, grade).build();
		students.add(student1);
		vm.persistStudents(students, true);
	}

	@Before
	public void refreshViewModel() {
		vm.refresh();
	}

	@Test
	public void testStateInitialisation() {
		assertNotNull(vm.students.get());
	}

	@Test
	public void testFullImport() {
		importThreeStudents();
		refreshViewModel();
		importTwoStudents(true);
		refreshViewModel();
		assertEquals(2, daoStudent.findAll().size());
		assertEquals("Bianca", daoStudent.find(5).getFirstname());
	}
	
	@Test
	public void testDeltaImport() {
		importThreeStudents();
		refreshViewModel();
		importTwoStudents(false);
		refreshViewModel();
		assertEquals(5, daoStudent.findAll().size());
		assertEquals("Milou", daoStudent.find(2).getLastname());
	}
	
	@Test
	public void updateStudentByImport(){
		importThreeStudents();
		refreshViewModel();
		int id = 3;
		assertEquals("Haddock", daoStudent.find(id).getLastname());
		overrideStudentWithId(id);
		refreshViewModel();
		assertEquals("Bienlein", daoStudent.find(id).getLastname());
	}
}
