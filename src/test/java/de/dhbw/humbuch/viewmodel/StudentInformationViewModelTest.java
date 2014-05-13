package de.dhbw.humbuch.viewmodel;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jfree.util.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.dhbw.humbuch.guice.GuiceJUnitRunner;
import de.dhbw.humbuch.guice.TestModule;
import de.dhbw.humbuch.guice.GuiceJUnitRunner.GuiceModules;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.TestPersistenceInitialiser;
import de.dhbw.humbuch.util.CSVHandler;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ TestModule.class })
public class StudentInformationViewModelTest extends BaseTest {

	private StudentInformationViewModel vm;
	private DAO<Student> daoStudent;
	
	private final static Logger LOG = LoggerFactory.getLogger(StudentInformationViewModelTest.class);

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
		Student student1 = new Student.Builder(2, "Peter", "Doe", null, grade).build();
		Student student2 = new Student.Builder(2, "Claude", "Gable", null, grade).build();
		students.add(student1);
		students.add(student2);		
		vm.persistStudents(students, fullImport);
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
	public void testFullImport(){
		LOG.info(""+daoStudent.findAll().size());
		System.out.println(""+daoStudent.findAll().size());
		importTwoStudents(true);
		refreshViewModel();
		assertEquals(2, daoStudent.findAll().size());
		assertEquals("Claude", daoStudent.find(1).getFirstname());
	}
}
