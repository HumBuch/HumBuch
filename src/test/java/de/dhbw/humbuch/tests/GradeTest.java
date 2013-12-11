package de.dhbw.humbuch.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.dhbw.humbuch.model.GradeHandler;
import de.dhbw.humbuch.model.MapperAmountAndBorrowedMaterial;
import de.dhbw.humbuch.model.ProfileHandler;
import de.dhbw.humbuch.model.StudentHandler;
import de.dhbw.humbuch.model.SubjectHandler;
import de.dhbw.humbuch.model.TeachingMaterialHandler;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Profile;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.Subject;
import de.dhbw.humbuch.model.entity.TeachingMaterial;


public class GradeTest {
	
	@Test
	public void testGetAllRentedBooksOfGrade(){
		Grade grade = prepareGradeTest();
		List<MapperAmountAndBorrowedMaterial> maabm = GradeHandler.getAllRentedBooksOfGrade(grade);
		
		assertEquals(2, maabm.get(0).getAmount());
		assertEquals("Bio1 - Bugs", maabm.get(0).getBorrowedMaterial().getTeachingMaterial().getName());
		assertEquals(1, maabm.get(1).getAmount());
		assertEquals("German1 - Faust", maabm.get(1).getBorrowedMaterial().getTeachingMaterial().getName());
		assertEquals(2, maabm.get(2).getAmount());
		assertEquals("Java rocks", maabm.get(2).getBorrowedMaterial().getTeachingMaterial().getName());
		assertEquals(1, maabm.get(3).getAmount());
		assertEquals("Geometrie for Dummies", maabm.get(3).getBorrowedMaterial().getTeachingMaterial().getName());
	}
	
	private Grade prepareGradeTest(){
		Grade grade = GradeHandler.createGrade(7, "b", "Herr Bob");
		List<Student> studentsList = new ArrayList<Student>();
		
		Profile profile = ProfileHandler.createProfile("L", "E", "");
		Student student = StudentHandler.createStudentObject("Karl", "August", "12.04.1970", "m", "7b", profile);
		
		List<BorrowedMaterial> borrowedMaterialList = new ArrayList<BorrowedMaterial>();	
		Subject subject = SubjectHandler.createSubject("Biology");
		TeachingMaterial teachingMaterial = TeachingMaterialHandler.createTeachingMaterial(subject, 6, "Bio1 - Bugs", 79.75);
		BorrowedMaterial borrowedMaterial = new BorrowedMaterial();
		borrowedMaterial.setTeachingMaterial(teachingMaterial);
		borrowedMaterialList.add(borrowedMaterial);
		
		subject = SubjectHandler.createSubject("German");
		teachingMaterial = TeachingMaterialHandler.createTeachingMaterial(subject, 11, "German1 - Faust", 22.49);
		borrowedMaterial = new BorrowedMaterial();
		borrowedMaterial.setTeachingMaterial(teachingMaterial);
		borrowedMaterialList.add(borrowedMaterial);
		
		subject = SubjectHandler.createSubject("IT");
		teachingMaterial = TeachingMaterialHandler.createTeachingMaterial(subject, 11, "Java rocks", 22.49);
		borrowedMaterial = new BorrowedMaterial();
		borrowedMaterial.setTeachingMaterial(teachingMaterial);
		borrowedMaterialList.add(borrowedMaterial);
		
		student.setBorrowedList(borrowedMaterialList);
		studentsList.add(student);
		
		profile = ProfileHandler.createProfile("E", "", "F");
		student = StudentHandler.createStudentObject("Karla", "Kolumna", "12.04.1981", "m", "7b", profile);
		
		borrowedMaterialList = new ArrayList<BorrowedMaterial>();	
		
		subject = SubjectHandler.createSubject("Biology");
		teachingMaterial = TeachingMaterialHandler.createTeachingMaterial(subject, 6, "Bio1 - Bugs", 79.75);
		borrowedMaterial = new BorrowedMaterial();
		borrowedMaterial.setTeachingMaterial(teachingMaterial);
		borrowedMaterialList.add(borrowedMaterial);
		
		subject = SubjectHandler.createSubject("IT");
		teachingMaterial = TeachingMaterialHandler.createTeachingMaterial(subject, 11, "Java rocks", 22.49);
		borrowedMaterial = new BorrowedMaterial();
		borrowedMaterial.setTeachingMaterial(teachingMaterial);
		borrowedMaterialList.add(borrowedMaterial);
		
		subject = SubjectHandler.createSubject("Mathe");
		teachingMaterial = TeachingMaterialHandler.createTeachingMaterial(subject, 11, "Geometrie for Dummies", 22.49);
		borrowedMaterial = new BorrowedMaterial();
		borrowedMaterial.setTeachingMaterial(teachingMaterial);
		borrowedMaterialList.add(borrowedMaterial);

		student.setBorrowedList(borrowedMaterialList);
		studentsList.add(student);
		
		grade.setStudents(studentsList);
		
		return grade;
	}

}
