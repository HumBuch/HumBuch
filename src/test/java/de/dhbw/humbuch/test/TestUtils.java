package de.dhbw.humbuch.test;

import java.util.Calendar;
import java.util.Date;

import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.SchoolYear;
import de.dhbw.humbuch.model.entity.SchoolYear.Term;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.TeachingMaterial;

public class TestUtils {
	public static int random() {
		return (int) (Math.random() * 10000);
	}

	public static Date todayPlusDays(int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}
	
	public static Grade grade(int grade) {
		return new Grade.Builder(grade, "").build();
	}
	
	public static SchoolYear schoolYear(int fromDays, int endFirstTermDays,
			int beginSecondTermDays, int toDays) {
		SchoolYear schoolYear = new SchoolYear.Builder("now",
				todayPlusDays(fromDays), todayPlusDays(toDays))
				.endFirstTerm(todayPlusDays(endFirstTermDays))
				.beginSecondTerm(todayPlusDays(beginSecondTermDays)).build();
		return schoolYear;
	}

	public static SchoolYear schoolYearFirstTermStarted() {
		return schoolYear(-1, 1, 1, 2);
	}

	public static SchoolYear schoolYearFirstTermEnded() {
		return schoolYear(-2, -1, 1, 2);
	}

	public static SchoolYear schoolYearSecondTermStarted() {
		return schoolYear(-3, -2, -1, 1);
	}

	public static SchoolYear schoolYearSecondTermEnded() {
		return schoolYear(-9, -8, -7, -6);
	}

	public static Student studentInGrade(int grade) {
		Grade gradeEntity = new Grade.Builder(grade, "").build();
		gradeEntity.setId(grade);
		Student student = new Student.Builder(random(), "John", "Doe", null,
				gradeEntity).build();
		return student;
	}

	public static TeachingMaterial teachingMaterialInBothTermsOfGrade(int grade) {
		TeachingMaterial teachingMaterial = new TeachingMaterial.Builder(null,
				"FooBook1", null, todayPlusDays(-20)).fromGrade(grade)
				.fromTerm(Term.FIRST).toGrade(grade).toTerm(Term.SECOND)
				.build();
		return teachingMaterial;
	}

	public static TeachingMaterial teachingMaterialInFirstTermOfGrade(int grade) {
		TeachingMaterial teachingMaterial = new TeachingMaterial.Builder(null,
				"FooBook1", null, todayPlusDays(-20)).fromGrade(grade)
				.fromTerm(Term.FIRST).toGrade(grade).toTerm(Term.FIRST).build();
		return teachingMaterial;
	}

	public static TeachingMaterial teachingMaterialInSecondTermOfGrade(int grade) {
		TeachingMaterial teachingMaterial = new TeachingMaterial.Builder(null,
				"FooBook1", null, todayPlusDays(-20)).fromGrade(grade)
				.fromTerm(Term.SECOND).toGrade(grade).toTerm(Term.SECOND)
				.build();
		return teachingMaterial;
	}

	public static BorrowedMaterial borrowedMaterialReceivedInPast(
			Student student, TeachingMaterial teachingMaterial) {
		BorrowedMaterial borrowedMaterial = new BorrowedMaterial.Builder(
				student, teachingMaterial, todayPlusDays(-25)).received(true)
				.build();
		return borrowedMaterial;
	}
}
