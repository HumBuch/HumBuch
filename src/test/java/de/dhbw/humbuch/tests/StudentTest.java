package de.dhbw.humbuch.tests;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;

import de.dhbw.humbuch.model.GradeHandler;
import de.dhbw.humbuch.model.ProfileTypeHandler;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.ProfileType;
import de.dhbw.humbuch.model.entity.Student;


public class StudentTest {
	
	@Test
	public void testCreateStudent(){
		Set<ProfileType> profileTypeSet = ProfileTypeHandler.createProfile(new String[]{"E", "", "F"}, "ev");
		Date date = null;
		try {
			date = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).parse("12.04.1970");
		}
		catch (ParseException e) {
			System.err.println("Could not format date " + e.getStackTrace());
		}		
		Grade grade = new Grade.Builder("11au").build();
		Student student = new Student.Builder(4,"Karl","August", date, grade).gender("m").profileTypes(profileTypeSet).build();
		
		assertEquals("Karl", student.getFirstname());
		assertEquals("August", student.getLastname());
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		assertEquals("12.04.1970", dateFormat.format(student.getBirthday()));
		assertEquals("m", student.getGender());
		assertEquals("11au", GradeHandler.getFullGrade(student.getGrade()));
		assertEquals("E F", ProfileTypeHandler.getLanguageProfile(student.getProfileTypes()));
	}

}
