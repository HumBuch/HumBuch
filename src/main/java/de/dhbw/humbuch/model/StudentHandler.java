package de.dhbw.humbuch.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.ProfileType;
import de.dhbw.humbuch.model.entity.Student;


public class StudentHandler {
	public static String getFullNameOfStudent(Student student){
		return student.getFirstname() + " " + student.getLastname();
	}
	
	public static Student createStudentObject(String firstName, String lastName, String birthDayString, 
												String gender, String gradeString, Set<ProfileType> profileTypeSet){
		Student student = new Student();
		try {
			student.setFirstname(firstName);
			student.setLastname(lastName);		
			student.setBirthday(new SimpleDateFormat("dd.MM.yyyy").parse(birthDayString));
			student.setGender(gender);
			Grade grade = new Grade();
			String[] splittedString = splitBetweenCharsAndDigits(gradeString);
			grade.setGrade(Integer.parseInt(splittedString[0]));
			grade.setSuffix(splittedString[1]);
			student.setGrade(grade);
			student.setProfileTypes(profileTypeSet);
		}catch (ParseException e) {
			System.err.println("Could not parse String to Date " + e.getMessage());
		}
		
		return student;
	}
	
	private static String[] splitBetweenCharsAndDigits(String str){	
		return str.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
	}
}
