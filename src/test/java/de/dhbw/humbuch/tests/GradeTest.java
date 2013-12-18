package de.dhbw.humbuch.tests;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;

import de.dhbw.humbuch.model.GradeHandler;
import de.dhbw.humbuch.model.MapperAmountAndBorrowedMaterial;
import de.dhbw.humbuch.model.ProfileTypeHandler;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Category;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.ProfileType;
import de.dhbw.humbuch.model.entity.Student;
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
	
	public static Grade prepareGradeTest(){
		Grade grade = new Grade.Builder("7b").teacher("Herr Bob").build();
		List<Student> studentsList = new ArrayList<Student>();

		Set<ProfileType> profileTypeSet = ProfileTypeHandler.createProfile(new String[]{"L", "E", ""}, "ev");
		Date date = null;
		try {
			date = new SimpleDateFormat("dd.mm.yyyy", Locale.GERMAN).parse("12.04.1970");
		}
		catch (ParseException e) {
			System.err.println("Could not format date " + e.getStackTrace());
		}		
		Student student = new Student.Builder(2, "Karl", "August", date, grade).gender("m").profileTypes(profileTypeSet).build();
		
		List<BorrowedMaterial> borrowedMaterialList = new ArrayList<BorrowedMaterial>();
		Category category = new Category.Builder("Book").build();
		TeachingMaterial teachingMaterial = new TeachingMaterial.Builder(category, "Bio1 - Bugs", "1-2-3", date).price(79.75).toGrade(6).build();	
		BorrowedMaterial borrowedMaterial = new BorrowedMaterial();
		borrowedMaterial.setTeachingMaterial(teachingMaterial);
		borrowedMaterialList.add(borrowedMaterial);
		
		teachingMaterial = new TeachingMaterial.Builder(category, "German1 - Faust", "1-2-3", date).price(22.49).toGrade(11).build();	
		borrowedMaterial = new BorrowedMaterial();
		borrowedMaterial.setTeachingMaterial(teachingMaterial);
		borrowedMaterialList.add(borrowedMaterial);
		
		teachingMaterial = new TeachingMaterial.Builder(category, "Java rocks", "1-2-3", date).price(22.49).toGrade(11).build();
		borrowedMaterial = new BorrowedMaterial();
		borrowedMaterial.setTeachingMaterial(teachingMaterial);
		borrowedMaterialList.add(borrowedMaterial);
		
		student.setBorrowedList(borrowedMaterialList);
		studentsList.add(student);
		
		profileTypeSet = ProfileTypeHandler.createProfile(new String[]{"E", "", "F"}, "rk");
		try {
			date = new SimpleDateFormat("dd.mm.yyyy", Locale.GERMAN).parse("12.04.1981");
		}
		catch (ParseException e) {
			System.err.println("Could not format date " + e.getStackTrace());
		}
		student = new Student.Builder(5, "Karla", "Kolumna", date, grade).gender("w").profileTypes(profileTypeSet).build();
		
		borrowedMaterialList = new ArrayList<BorrowedMaterial>();	
		
		//subject = SubjectHandler.createSubject("Biology");
		teachingMaterial = new TeachingMaterial.Builder(category, "Bio1 - Bugs", "1-2-3", date).price(79.75).toGrade(6).build();
		borrowedMaterial = new BorrowedMaterial();
		borrowedMaterial.setTeachingMaterial(teachingMaterial);
		borrowedMaterialList.add(borrowedMaterial);
		
		//subject = SubjectHandler.createSubject("IT");
		teachingMaterial = new TeachingMaterial.Builder(category, "Java rocks", "1-2-3", date).price(22.49).toGrade(11).build();
		borrowedMaterial = new BorrowedMaterial();
		borrowedMaterial.setTeachingMaterial(teachingMaterial);
		borrowedMaterialList.add(borrowedMaterial);
		
		//subject = SubjectHandler.createSubject("Mathe");
		teachingMaterial = new TeachingMaterial.Builder(category, "Geometrie for Dummies", "1-2-3", date).price(22.49).toGrade(11).build();
		borrowedMaterial = new BorrowedMaterial();
		borrowedMaterial.setTeachingMaterial(teachingMaterial);
		borrowedMaterialList.add(borrowedMaterial);

		student.setBorrowedList(borrowedMaterialList);
		studentsList.add(student);
		
		grade.setStudents(studentsList);
		
		return grade;
	}
}
