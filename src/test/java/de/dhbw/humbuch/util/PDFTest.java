package de.dhbw.humbuch.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import de.dhbw.humbuch.model.SubjectHandler;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.GradeTest;
import de.dhbw.humbuch.model.entity.Parent;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.Subject;
import de.dhbw.humbuch.model.entity.TeachingMaterial;

/**
 * 
 * @author Benjamin
 *
 * This class contains test data to generate PDFs.
 * It does NOT contain any JUnit tests.
 * Use this class to create PDFs. After the creation one has to check them manually!
 */

public class PDFTest {
	public static void main(String[] args){
		testStudentPDF();
		testClassPDF();	
		testDunningPDF();
		testDunningPDFWithParent();
	}
	
	public static void testStudentPDF(){
		Set<Subject> profileTypeSet = SubjectHandler.createProfile(new String[]{"E", "", "F"}, "ev");
		List<BorrowedMaterial> borrowedMaterialList = new ArrayList<BorrowedMaterial>();
		
		TeachingMaterial teachingMaterial = new TeachingMaterial.Builder(null, "Bio1 - Bugs", "123", null).price(79.75).toGrade(6).build();
		BorrowedMaterial borrowedMaterial = new BorrowedMaterial.Builder(null, teachingMaterial, null).build();
		borrowedMaterialList.add(borrowedMaterial);
		
		teachingMaterial = new TeachingMaterial.Builder(null, "German1 - Faust", "123", null).price(22.49).toGrade(11).build();
		borrowedMaterial = new BorrowedMaterial.Builder(null, teachingMaterial, null).build();
		borrowedMaterialList.add(borrowedMaterial);
		
		Date date = null;
		try {
			date = new SimpleDateFormat("dd.mm.yyyy", Locale.GERMAN).parse("12.04.1970");
			Grade grade = new Grade.Builder("11au").build();
			Student student = new Student.Builder(4,"Karl","August", date, grade).profile(profileTypeSet).borrowedList(borrowedMaterialList).build();
			new PDFStudentList.Builder(student).build().savePDF("./testfiles/FirstPdf.pdf");
		}
		catch (ParseException e) {
			System.err.println("Could not format date " + e.getStackTrace());
		}		
	}
	
	public static void testClassPDF(){
//		Grade grade = GradeTest.prepareGradeTest();
//		new PDFClassList(grade).savePDF("./testfiles/FirstPdfClass.pdf");
		
		Map<Grade, Map<TeachingMaterial, Integer>> gradeMap = GradeTest.prepareGradeTest();
		new PDFClassList(gradeMap).savePDF("./testfiles/SecondPdfClass.pdf");
		
		Map<Grade, Map<TeachingMaterial, Integer>> multipleGradesMap = GradeTest.prepareMultipleGradeTest();
		new PDFClassList(multipleGradesMap).savePDF("./testfiles/MultiPdfClass.pdf");
	}
	
	public static void testDunningPDF(){
		Set<Subject> profileTypeSet = SubjectHandler.createProfile(new String[]{"E", "", "F"}, "ev");
		List<BorrowedMaterial> borrowedMaterialList = new ArrayList<BorrowedMaterial>();
		
		TeachingMaterial teachingMaterial = new TeachingMaterial.Builder(null, "Bio1 - Bugs", "123", null).price(79.75).toGrade(6).build();
		BorrowedMaterial borrowedMaterial = new BorrowedMaterial.Builder(null, teachingMaterial, null).build();
		borrowedMaterialList.add(borrowedMaterial);
		
		teachingMaterial = new TeachingMaterial.Builder(null, "German1 - Faust", "123", null).price(22.49).toGrade(11).build();
		borrowedMaterial = new BorrowedMaterial.Builder(null, teachingMaterial, null).build();
		borrowedMaterialList.add(borrowedMaterial);
		
		Date date = null;
		try {
			date = new SimpleDateFormat("dd.mm.yyyy", Locale.GERMAN).parse("12.04.1970");
			Grade grade = new Grade.Builder("11au").build();
			Student student = new Student.Builder(4,"Karl","August", date, grade).profile(profileTypeSet).borrowedList(borrowedMaterialList).build();
			Set<Student> students = new LinkedHashSet<Student>();
			students.add(student);
			PDFDunning.createFirstDunning(students, student.getBorrowedList()).savePDF("./testfiles/DunningPdf.pdf");
		}
		catch (ParseException e) {
			System.err.println("Could not format date " + e.getStackTrace());
		}		
	}
	
	public static void testDunningPDFWithParent(){
		Set<Subject> profileTypeSet = SubjectHandler.createProfile(new String[]{"E", "", "F"}, "ev");
		List<BorrowedMaterial> borrowedMaterialList = new ArrayList<BorrowedMaterial>();
		
		TeachingMaterial teachingMaterial = new TeachingMaterial.Builder(null, "Bio1 - Bugs", "123", null).price(79.75).toGrade(6).build();
		BorrowedMaterial borrowedMaterial = new BorrowedMaterial.Builder(null, teachingMaterial, null).build();
		borrowedMaterialList.add(borrowedMaterial);
		
		teachingMaterial = new TeachingMaterial.Builder(null, "German1 - Faust", "123", null).price(22.49).toGrade(11).build();
		borrowedMaterial = new BorrowedMaterial.Builder(null, teachingMaterial, null).build();
		borrowedMaterialList.add(borrowedMaterial);
		
		Date date = null;
		try {
			date = new SimpleDateFormat("dd.mm.yyyy", Locale.GERMAN).parse("12.04.1970");
			Grade grade = new Grade.Builder("11au").build();
			Parent parent = new Parent.Builder("Penny", "Wise").build();
			Student student = new Student.Builder(4,"Karl","August", date, grade).profile(profileTypeSet).borrowedList(borrowedMaterialList).parent(parent).build();
			Set<Student> students = new LinkedHashSet<Student>();
			students.add(student);
			PDFDunning.createSecondDunning(students, student.getBorrowedList()).savePDF("./testfiles/secondDunningPdf.pdf");
		}
		catch (ParseException e) {
			System.err.println("Could not format date " + e.getStackTrace());
		}		
		
	}

}
