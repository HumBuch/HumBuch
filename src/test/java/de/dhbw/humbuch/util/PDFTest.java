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

import org.junit.Ignore;

import de.dhbw.humbuch.model.SubjectHandler;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.GradeTest;
import de.dhbw.humbuch.model.entity.Parent;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.Subject;
import de.dhbw.humbuch.model.entity.TeachingMaterial;


/**
 * This class contains test data to generate PDFs. It does NOT contain any JUnit
 * tests. Use this class to create PDFs. After the creation one has to check
 * them manually!
 * 
 * @author Benjamin Räthlein
 * 
 */

@Ignore("Manual test")
public class PDFTest {

	public static void main(String[] args) {
		testStudentPDF();
		testClassPDF();
		testDunningPDF();
		testDunningPDFWithParent();
	}

	public static void testStudentPDF() {
		Student student;
		Date date = null;
		try {
			date = new SimpleDateFormat("dd.mm.yyyy", Locale.GERMAN).parse("12.04.1970");
			Grade grade = new Grade.Builder("11au").build();
			Set<Subject> profileTypeSet = SubjectHandler.createProfile(new String[] { "E", "", "F" }, "ev");
			List<BorrowedMaterial> borrowedMaterialList = new ArrayList<BorrowedMaterial>();
			student = new Student.Builder(4, "Karl", "August", date, grade).profile(profileTypeSet).build();

			TeachingMaterial teachingMaterial = new TeachingMaterial.Builder(null, "Bio1 - Bugs", "123", null).price(79.75).toGrade(6).build();
			BorrowedMaterial borrowedMaterial = new BorrowedMaterial.Builder(student, teachingMaterial, null).build();
			borrowedMaterialList.add(borrowedMaterial);

			teachingMaterial = new TeachingMaterial.Builder(null, "German1 - Faust", "123", null).price(22.49).toGrade(11).build();
			borrowedMaterial = new BorrowedMaterial.Builder(student, teachingMaterial, null).build();
			borrowedMaterialList.add(borrowedMaterial);

			for(int i = 0; i < 56; i++){
				borrowedMaterial = new BorrowedMaterial.Builder(student, teachingMaterial, null).build();
				borrowedMaterialList.add(borrowedMaterial);
			}

			student.setBorrowedMaterials(borrowedMaterialList);

			PDFStudentList.Builder builder = new PDFStudentList.Builder().borrowedMaterialList(borrowedMaterialList).
					lendingList(borrowedMaterialList).returnList(borrowedMaterialList);
			new PDFStudentList(builder).savePDF("./testfiles/StudentPDFMasterList.pdf");
			
			builder = new PDFStudentList.Builder().borrowedMaterialList(borrowedMaterialList);
			new PDFStudentList(builder).savePDF("./testfiles/StudentPDFBorrowedList.pdf");
			
			builder = new PDFStudentList.Builder().lendingList(borrowedMaterialList);
			new PDFStudentList(builder).savePDF("./testfiles/StudentPDFLendingList.pdf");
			
			builder = new PDFStudentList.Builder().returnList(borrowedMaterialList);
			new PDFStudentList(builder).savePDF("./testfiles/StudentPDFReturnList.pdf");
		}
		catch (ParseException e) {
			System.err.println("Could not format date " + e.getStackTrace());
		}
	}

	public static void testClassPDF() {
		Map<Grade, Map<TeachingMaterial, Integer>> gradeMap = GradeTest.prepareGradeTest();
		new PDFClassList(gradeMap).savePDF("./testfiles/SecondPdfClass.pdf");

		Map<Grade, Map<TeachingMaterial, Integer>> multipleGradesMap = GradeTest.prepareMultipleGradeTest();
		new PDFClassList(multipleGradesMap).savePDF("./testfiles/MultiClassPDF.pdf");
	}

	public static void testDunningPDF() {

		Set<Subject> profileTypeSet = SubjectHandler.createProfile(new String[] { "E", "", "F" }, "ev");
		List<BorrowedMaterial> borrowedMaterialList = new ArrayList<BorrowedMaterial>();
		Date date;
		try {
			date = new SimpleDateFormat("dd.mm.yyyy", Locale.GERMAN).parse("12.04.1970");
			Grade grade = new Grade.Builder("11au").build();
			Parent parent = new Parent.Builder("Penny", "Wise").title("Frau").street("Elmstreet 31").city("Karlsruhe").postcode(1337).build();

			Student student = new Student.Builder(4, "Karl", "August", date, grade).profile(profileTypeSet).parent(parent).build();
			TeachingMaterial teachingMaterial = new TeachingMaterial.Builder(null, "Bio1 - Bugs", "123", null).price(79.75).toGrade(6).build();
			BorrowedMaterial borrowedMaterial = new BorrowedMaterial.Builder(student, teachingMaterial, null).build();
			borrowedMaterialList.add(borrowedMaterial);

			teachingMaterial = new TeachingMaterial.Builder(null, "German1 - Faust", "123", null).price(22.49).toGrade(11).build();
			borrowedMaterial = new BorrowedMaterial.Builder(student, teachingMaterial, null).build();
			borrowedMaterialList.add(borrowedMaterial);

			student.setBorrowedMaterials(borrowedMaterialList);
			Set<List<BorrowedMaterial>> borrowedMaterials = new LinkedHashSet<List<BorrowedMaterial>>();
			borrowedMaterials.add(student.getBorrowedMaterials());
			PDFDunning.createFirstDunning(borrowedMaterials).savePDF("./testfiles/DunningPdf.pdf");
		}
		catch (ParseException e) {
			System.err.println("Could not format date " + e.getStackTrace());
		}
	}

	public static void testDunningPDFWithParent() {

		Set<Subject> profileTypeSet = SubjectHandler.createProfile(new String[] { "E", "", "F" }, "ev");
		List<BorrowedMaterial> borrowedMaterialList = new ArrayList<BorrowedMaterial>();
		Date date;
		try {
			date = new SimpleDateFormat("dd.mm.yyyy", Locale.GERMAN).parse("12.04.1970");
			Grade grade = new Grade.Builder("11au").build();
			Parent parent = new Parent.Builder("Penny", "Wise").title("Frau").street("Elmstreet 31").city("Karlsruhe").postcode(1337).build();

			Student student = new Student.Builder(4, "Karl", "August", date, grade).profile(profileTypeSet).parent(parent).build();
			TeachingMaterial teachingMaterial = new TeachingMaterial.Builder(null, "Bio1 - Bugs", "123", null).price(79.75).toGrade(6).build();
			BorrowedMaterial borrowedMaterial = new BorrowedMaterial.Builder(student, teachingMaterial, null).build();
			borrowedMaterialList.add(borrowedMaterial);

			teachingMaterial = new TeachingMaterial.Builder(null, "German1 - Faust", "123", null).price(22.49).toGrade(11).build();
			borrowedMaterial = new BorrowedMaterial.Builder(student, teachingMaterial, null).build();
			borrowedMaterialList.add(borrowedMaterial);

			student.setBorrowedMaterials(borrowedMaterialList);
			Set<List<BorrowedMaterial>> borrowedMaterials = new LinkedHashSet<List<BorrowedMaterial>>();
			borrowedMaterials.add(student.getBorrowedMaterials());
			PDFDunning.createSecondDunning(borrowedMaterials).savePDF("./testfiles/secondDunningPdf.pdf");
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		

	}

}
