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
}
