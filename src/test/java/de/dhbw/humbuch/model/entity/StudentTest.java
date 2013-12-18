package de.dhbw.humbuch.model.entity;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

public class StudentTest {
	
	@Test
	public void testStudentBuilder() {
		final int ID = 1;
		final String FIRST_NAME = "John";
		final String LAST_NAME = "Doe";
		final Date DATE = new Date();
		final Grade GRADE = null;
		
		Student student = new Student.Builder(ID, FIRST_NAME, LAST_NAME, DATE, GRADE).build();
		assertEquals(ID, student.getId());
		assertEquals(FIRST_NAME, student.getFirstname());
		assertEquals(LAST_NAME, student.getLastname());
		assertEquals(DATE, student.getBirthday());
		assertEquals(GRADE, student.getGrade());
	}
}
