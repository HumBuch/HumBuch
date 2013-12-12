package de.dhbw.humbuch.tests;

import java.util.ArrayList;
import java.util.List;

import de.dhbw.humbuch.model.ProfileHandler;
import de.dhbw.humbuch.model.StudentHandler;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Profile;
import de.dhbw.humbuch.model.entity.Religion;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.TeachingMaterial;
import de.dhbw.humbuch.pdfExport.MyPDFClassList;
import de.dhbw.humbuch.pdfExport.MyPDFStudentList;


public class PDFTest {
	public static void main(String[] args){
		testStudentPDF();
		testClassPDF();	
	}
	
	public static void testStudentPDF(){
		Profile profile = ProfileHandler.createProfile("E", "", "F");
		profile.setReligion(Religion.ETHICS);
		List<BorrowedMaterial> borrowedMaterialList = new ArrayList<BorrowedMaterial>();
		
		TeachingMaterial teachingMaterial = new TeachingMaterial();
		//Subject subject = new Subject();
//		subject.setName("Biology");
//		teachingMaterial.setSubject(subject);
		teachingMaterial.setToGrade(6);
		teachingMaterial.setName("Bio1 - Bugs");
		teachingMaterial.setPrice(79.75);
		BorrowedMaterial borrowedMaterial = new BorrowedMaterial();
		borrowedMaterial.setTeachingMaterial(teachingMaterial);
		borrowedMaterialList.add(borrowedMaterial);
		
		teachingMaterial = new TeachingMaterial();
//		subject = new Subject();
//		subject.setName("German");
//		teachingMaterial.setSubject(subject);
		teachingMaterial.setToGrade(11);
		teachingMaterial.setName("German1 - Faust");
		teachingMaterial.setPrice(22.49);
		borrowedMaterial = new BorrowedMaterial();
		borrowedMaterial.setTeachingMaterial(teachingMaterial);
		borrowedMaterialList.add(borrowedMaterial);
		
		Student student = StudentHandler.createStudentObject("Karl", "August", "12.04.1970", "m", "11au", profile);
		student.setBorrowedList(borrowedMaterialList);
		new MyPDFStudentList(student).savePDF("./testfiles/FirstPdf.pdf");
	}
	
	public static void testClassPDF(){
		Grade grade = GradeTest.prepareGradeTest();
		new MyPDFClassList(grade).savePDF("./testfiles/FirstPdfClass.pdf");
	}

}
