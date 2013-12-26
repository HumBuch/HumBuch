package de.dhbw.humbuch.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.dhbw.humbuch.model.SubjectHandler;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.GradeTest;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.Subject;
import de.dhbw.humbuch.model.entity.TeachingMaterial;


public class PDFTest {
	public static void main(String[] args){
		testStudentPDF();
		testClassPDF();	
	}
	
	public static void testStudentPDF(){
		Set<Subject> profileTypeSet = SubjectHandler.createProfile(new String[]{"E", "", "F"}, "ev");
		List<BorrowedMaterial> borrowedMaterialList = new ArrayList<BorrowedMaterial>();
		
		TeachingMaterial teachingMaterial = new TeachingMaterial();

		teachingMaterial.setToGrade(6);
		teachingMaterial.setName("Bio1 - Bugs");
		teachingMaterial.setPrice(79.75);
		BorrowedMaterial borrowedMaterial = new BorrowedMaterial();
		borrowedMaterial.setTeachingMaterial(teachingMaterial);
		borrowedMaterialList.add(borrowedMaterial);
		
		teachingMaterial = new TeachingMaterial();

		teachingMaterial.setToGrade(11);
		teachingMaterial.setName("German1 - Faust");
		teachingMaterial.setPrice(22.49);
		borrowedMaterial = new BorrowedMaterial();
		borrowedMaterial.setTeachingMaterial(teachingMaterial);
		borrowedMaterialList.add(borrowedMaterial);
		
		Date date = null;
		try {
			date = new SimpleDateFormat("dd.mm.yyyy", Locale.GERMAN).parse("12.04.1970");
		}
		catch (ParseException e) {
			System.err.println("Could not format date " + e.getStackTrace());
		}		
		Grade grade = new Grade.Builder("11au").build();
		Student student = new Student.Builder(4,"Karl","August", date, grade).profile(profileTypeSet).borrowedList(borrowedMaterialList).build();
		new PDFStudentList(student).savePDF("./testfiles/FirstPdf.pdf");
	}
	
	public static void testClassPDF(){
		Grade grade = GradeTest.prepareGradeTest();
		new PDFClassList(grade).savePDF("./testfiles/FirstPdfClass.pdf");
	}

}
