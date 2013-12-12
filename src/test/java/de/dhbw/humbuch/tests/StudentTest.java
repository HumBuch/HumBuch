package de.dhbw.humbuch.tests;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.junit.Test;

import de.dhbw.humbuch.model.GradeHandler;
import de.dhbw.humbuch.model.ProfileHandler;
import de.dhbw.humbuch.model.StudentHandler;
import de.dhbw.humbuch.model.entity.Profile;
import de.dhbw.humbuch.model.entity.Student;


public class StudentTest {
	
	@Test
	public void testCreateStudent(){
		Profile profile = ProfileHandler.createProfile("E", "", "F");
		Student student = StudentHandler.createStudentObject("Karl", "August", "12.04.1970", "m", "11au", profile);
		
		assertEquals("Karl", student.getFirstname());
		assertEquals("August", student.getLastname());
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		assertEquals("12.04.1970", dateFormat.format(student.getBirthday()));
		assertEquals("m", student.getGender());
		assertEquals("11au", GradeHandler.getFullGrade(student.getGrade()));
//		SassertEquals("E F", ProfileHandler.getLanguageProfile(student.getProfile()));
	}

}
