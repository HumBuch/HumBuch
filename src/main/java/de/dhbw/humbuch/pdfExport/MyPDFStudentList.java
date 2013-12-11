package de.dhbw.humbuch.pdfExport;

import java.util.Iterator;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import de.dhbw.humbuch.model.ProfileHandler;
import de.dhbw.humbuch.model.StudentHandler;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;


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
		PdfPCell cell;
		while(iterator.hasNext()){
			borrowedMaterial = (BorrowedMaterial) iterator.next();
			cell = new PdfPCell(new Phrase(borrowedMaterial.getTeachingMaterial().getSubject().getName()));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(borrowedMaterial.getTeachingMaterial().getToGrade()));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(borrowedMaterial.getTeachingMaterial().getName()));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(""+borrowedMaterial.getTeachingMaterial().getPrice()));
			table.addCell(cell);
			cell = new PdfPCell(new Phrase(" "));
			table.addCell(cell);
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
		PdfPTable table = new PdfPTable(5);
		
		//header of table which contains information about the student
		PdfPCell cell = new PdfPCell(new Phrase("Schuljahr"));
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Klasse"));
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Sprachenfolge"));
		table.addCell(cell);

		cell = new PdfPCell(new Phrase("Religions-\nunterricht"));
		table.addCell(cell);

		cell = new PdfPCell(new Phrase("Schüler"));
		table.addCell(cell);
		
		//Table-Content
		cell = new PdfPCell(new Phrase("#SCHOOLYEAR"));
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase(this.student.getGrade().getGrade()));
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase(ProfileHandler.getLanguageProfile(this.student.getProfile())));
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase(this.student.getProfile().getReligion().toString()));
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase(StudentHandler.getFullNameOfStudent(student)));
		table.addCell(cell);
		
		try {
			document.add(table);
			Paragraph paragraph = new Paragraph();
			addEmptyLine(paragraph, 2);
			document.add(paragraph);
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
		String disclosure = "Die oben angeführten Schulbücher habe ich erhalten.\n" +
								"Die ausgeliehenen Bücher habe ich auf Vollständigkeit und Beschädigung überprüft. "+
								"Beschädigte oder verlorengegangene Bücher müssen ersetzt werden.\n";
		PdfPTable table = new PdfPTable(1);
		PdfPCell cell = new PdfPCell(new Phrase(disclosure));
		table.addCell(cell);
		try {
			document.add(table);
		}
		catch (DocumentException e) {
			e.printStackTrace();
		}
	}
}
