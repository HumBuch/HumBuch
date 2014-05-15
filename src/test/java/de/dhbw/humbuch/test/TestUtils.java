package de.dhbw.humbuch.test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Calendar;
import java.util.Date;

import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Category;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.SchoolYear;
import de.dhbw.humbuch.model.entity.SettingsEntry;
import de.dhbw.humbuch.model.entity.SchoolYear.Term;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.TeachingMaterial;
import de.dhbw.humbuch.model.entity.User;
import de.dhbw.humbuch.util.PasswordHash;

public class TestUtils {

	public static final String USERNAME = "USERNAME";
	public static final String PASSWORD = "PASSWORD";
	public static final String PASSWORD_HASH = "HASH";

	public static int rInt() {
		return (int) (Math.random() * 10000);
	}

	public static String rStr() {
		return "" + rInt();
	}

	public static Date todayPlusDays(int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}

	public static User user(String username, String password) {
		try {
			return new User.Builder(username, PasswordHash.createHash(password))
					.email(rStr() + "@" + rStr() + "." + rStr()).build();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			return new User.Builder(USERNAME, PASSWORD_HASH).email(
					rStr() + "@" + rStr() + "." + rStr()).build();
		}
	}

	public static User randomUser() {
		return user(rStr(), rStr());
	}

	public static User standardUser() {
		return user(USERNAME, PASSWORD);
	}

	public static Category category() {
		return new Category.Builder(rStr()).build();
	}

	public static Grade grade(int grade) {
		Grade gradeEntity = new Grade.Builder(grade, "").build();
		gradeEntity.setId(grade);
		return gradeEntity;
	}

	public static SettingsEntry settingsEntry() {
		return new SettingsEntry.Builder(rStr(), rStr(), rStr()).build();
	}

	public static SchoolYear schoolYear(int fromDays, int endFirstTermDays,
			int beginSecondTermDays, int toDays) {
		SchoolYear schoolYear = new SchoolYear.Builder(rStr(),
				todayPlusDays(fromDays), todayPlusDays(toDays))
				.endFirstTerm(todayPlusDays(endFirstTermDays))
				.beginSecondTerm(todayPlusDays(beginSecondTermDays)).build();
		return schoolYear;
	}

	public static SchoolYear schoolYearFirstTermNotStarted() {
		return schoolYear(1, 2, 3, 4);
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

	public static SchoolYear schoolYearFirstTermEndedPlusDays(int days) {
		return schoolYear(-days - 1, -days, 1, 2);
	}

	public static SchoolYear schoolYearSecondTermStartedPlusDays(int days) {
		return schoolYear(-days - 2, -days - 1, -days, 1);
	}

	public static SchoolYear schoolYearSecondTermEndedPlusDays(int days) {
		return schoolYear(-days - 3, -days - 2, -days - 1, -days);
	}

	public static Student studentInGrade(int grade) {
		Grade gradeEntity = new Grade.Builder(grade, "").build();
		gradeEntity.setId(grade);
		Student student = new Student.Builder(rInt(), rStr(), rStr(), null,
				gradeEntity).build();
		return student;
	}

	public static TeachingMaterial teachingMaterialInBothTermsOfGrade(int grade) {
		TeachingMaterial teachingMaterial = new TeachingMaterial.Builder(
				category(), rStr(), null, todayPlusDays(-20)).fromGrade(grade)
				.fromTerm(Term.FIRST).toGrade(grade).toTerm(Term.SECOND)
				.build();
		return teachingMaterial;
	}

	public static TeachingMaterial teachingMaterialInFirstTermOfGrade(int grade) {
		TeachingMaterial teachingMaterial = new TeachingMaterial.Builder(
				category(), rStr(), null, todayPlusDays(-20)).fromGrade(grade)
				.fromTerm(Term.FIRST).toGrade(grade).toTerm(Term.FIRST).build();
		return teachingMaterial;
	}

	public static TeachingMaterial teachingMaterialInSecondTermOfGrade(int grade) {
		TeachingMaterial teachingMaterial = new TeachingMaterial.Builder(
				category(), rStr(), null, todayPlusDays(-20)).fromGrade(grade)
				.fromTerm(Term.SECOND).toGrade(grade).toTerm(Term.SECOND)
				.build();
		return teachingMaterial;
	}

	public static TeachingMaterial teachingMaterialInNoTerm(int grade) {
		TeachingMaterial teachingMaterial = new TeachingMaterial.Builder(
				category(), rStr(), null, todayPlusDays(-20)).build();
		return teachingMaterial;
	}

	public static BorrowedMaterial borrowedMaterialReceivedInPast(
			Student student, TeachingMaterial teachingMaterial) {
		BorrowedMaterial borrowedMaterial = new BorrowedMaterial.Builder(
				student, teachingMaterial, todayPlusDays(-25)).received(true)
				.build();
		return borrowedMaterial;
	}

	public static BorrowedMaterial borrowedMaterialReceivedInPastBorrowUntil(
			Student student, TeachingMaterial teachingMaterial, Date borrowUntil) {
		BorrowedMaterial borrowedMaterial = new BorrowedMaterial.Builder(
				student, teachingMaterial, todayPlusDays(-25))
				.borrowUntil(borrowUntil).received(true).build();
		return borrowedMaterial;
	}
}
