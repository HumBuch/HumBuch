package de.dhbw.humbuch.util;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPTable;

import de.dhbw.humbuch.model.SubjectHandler;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Parent;
import de.dhbw.humbuch.model.entity.Student;


public class PDFDunning extends PDFHandler {
	Student student;
	Set<Student> students;
	Parent parent;
	List<BorrowedMaterial> borrowedMaterials;
	
	private PDFDunning(){};
	
	public static PDFDunning createFirstDunning(Set<Student> students, List<BorrowedMaterial> borrowedMaterials){
		PDFDunning pdfDunning = new PDFDunning();
		pdfDunning.students = students;
		pdfDunning.borrowedMaterials = borrowedMaterials;
		return pdfDunning;
	}
	
	public static PDFDunning createSecondDunning(Parent parent){
		PDFDunning pdfDunning = new PDFDunning();
		pdfDunning.parent = parent;
		return pdfDunning;
	}

	protected void insertDocumentParts(Document document) {
		for(Student student : this.students){
			this.addHeading(document, "Mahnung 2013");
			this.student = student;
			this.addStudentInformation(document);
			this.addContent(document);
			this.addSignatureField(document, "Schüler");
			document.newPage();
			this.resetPageNumber();
		}
	}

	protected void addContent(Document document) {
		PdfPTable table = PDFHandler.createMyStandardTable(1);
		PDFHandler.fillTableWithContent(table, false,
				new String[]{"Folgende Bücher wurden nicht rechtzeitig zurückgegeben: \n"}, false);
		try {
			document.add(table);
		}
		catch (DocumentException e) {
			e.printStackTrace();
		}
		
		table = this.createTableWithRentalInformationHeader();
		
		Iterator<BorrowedMaterial> iterator = this.borrowedMaterials.iterator();
		BorrowedMaterial borrowedMaterial;
		while(iterator.hasNext()){
			borrowedMaterial = (BorrowedMaterial) iterator.next();
			String[] contentArray = {//borrowedMaterial.getTeachingMaterial().getSubject().getName(),
			                         ""+borrowedMaterial.getTeachingMaterial().getToGrade(),
			                         borrowedMaterial.getTeachingMaterial().getName(),
			                      	 "" };
			PDFHandler.fillTableWithContent(table, true, contentArray);		
		}
	    
	    try {
			document.add(table);
		}
		catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Inserts information about the student like grade, language, name etc.
	 * 
	 * @param document represents the PDF before it is saved
	 */	
	private void addStudentInformation(Document document){
		PdfPTable table = PDFHandler.createMyStandardTable(2, new float[]{1f, 6f});

		String[] contentArray = {"Schüler: ", this.student.getFirstname() + " " + this.student.getLastname(),
		                         "Klasse: ", "" + this.student.getGrade().getGrade(),
		                         "Schuljahr: ", "#SCHOOLYEAR",
		                         "Sprachen: ", SubjectHandler.getLanguageProfile(this.student.getProfile()),
					             "Religion: ", SubjectHandler.getReligionProfile(this.student.getProfile()) + "\n"};
		PDFHandler.fillTableWithContentWithoutSpace(table, false, contentArray);
		
		try {
			document.add(table);
			PDFHandler.addEmptyLineToDocument(document, 1);
		}
		catch (DocumentException e) {
			e.printStackTrace();
		}
	}

}
