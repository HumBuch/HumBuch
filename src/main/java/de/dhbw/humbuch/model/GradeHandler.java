package de.dhbw.humbuch.model;

import de.dhbw.humbuch.model.entity.Grade;

public class GradeHandler {
	public static String getFullGrade(Grade grade){
		return grade.getGrade() + grade.getSuffix();
	}

}
