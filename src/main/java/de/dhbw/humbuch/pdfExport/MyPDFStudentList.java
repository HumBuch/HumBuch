package de.dhbw.humbuch.pdfExport;

import java.util.Iterator;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import de.dhbw.humbuch.model.ProfileHandler;
import de.dhbw.humbuch.model.StudentHandler;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Student;


public final class MyPDFStudentList extends MyPDFHandler{
	private Student student;

	public MyPDFStudentList(Student student) {
		super();	
		this.student = student;
	}
	
	protected void insertDocumentParts(Document document){
		this.addHeading(document, "Ausgabe-Liste 2013");
		this.addStudentInformation(document);
		this.addContent(document);
		this.addRentalDisclosure(document);
		this.addSignatureField(document, "Schüler");
	}
	
	protected void addContent(Document document) {
		PdfPTable table = this.createTableWithRentalInformationHeader();
		
		Iterator<BorrowedMaterial> iterator = this.student.getBorrowedList().iterator();
		BorrowedMaterial borrowedMaterial;
		while(iterator.hasNext()){
			borrowedMaterial = (BorrowedMaterial) iterator.next();
			String[] contentArray = {//borrowedMaterial.getTeachingMaterial().getSubject().getName(),
			                         ""+borrowedMaterial.getTeachingMaterial().getToGrade(),
			                         borrowedMaterial.getTeachingMaterial().getName(),
			                      	 "" };
			MyPDFHandler.fillTableWithContent(table, true, contentArray);		
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
		PdfPTable table = MyPDFHandler.createMyStandardTable(2, new float[]{1f, 6f});

		String[] contentArray = {"Schüler: ", StudentHandler.getFullNameOfStudent(student),
		                         "Klasse: ", "" + this.student.getGrade().getGrade(),
		                         "Schuljahr: ", "#SCHOOLYEAR"}; 
//		                         "Sprachenfolge: "+ ProfileHandler.getLanguageProfile(this.student.getProfile()) + "\n"
//					             + "Religionsunterricht: " + this.student.getProfile().getReligion().toString() + "\n"};
		MyPDFHandler.fillTableWithContentWithoutSpace(table, false, contentArray);
		
		try {
			document.add(table);
			MyPDFHandler.addEmptyLineToDocument(document, 1);
		}
		catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param document represents the PDF before it is saved
	 */
	private void addRentalDisclosure(Document document){
		PdfPTable table = MyPDFHandler.createMyStandardTable(1);
		MyPDFHandler.fillTableWithContent(table, false,
				new String[]{"\nDie oben angeführten Schulbücher habe ich erhalten.\n" +
				"Die ausgeliehenen Bücher habe ich auf Vollständigkeit und Beschädigung überprüft. "+
				"Beschädigte oder verlorengegangene Bücher müssen ersetzt werden.\n"}, false);
		try {
			document.add(table);
		}
		catch (DocumentException e) {
			e.printStackTrace();
		}
	}
}
