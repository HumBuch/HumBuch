package de.dhbw.humbuch.model.entity;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

public class StudentTest {
	
	@Test
	public void testStudentBuilder() {
		final String FIRST_NAME = "John";
		final String LAST_NAME = "Doe";
		final Date DATE = new Date();
		final Grade GRADE = null;
		
		Student student = new Student.Builder(FIRST_NAME, LAST_NAME, DATE, GRADE).build();
		assertEquals(FIRST_NAME, student.getFirstname());
		assertEquals(LAST_NAME, student.getLastname());
		assertEquals(DATE, student.getBirthday());
		assertEquals(GRADE, student.getGrade());
	}
}
