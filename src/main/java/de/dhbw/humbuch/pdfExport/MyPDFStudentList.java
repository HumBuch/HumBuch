package de.dhbw.humbuch.pdfExport;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import de.dhbw.humbuch.model.ProfileHandler;
import de.dhbw.humbuch.model.StudentHandler;
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

//		Not implemented yet since no data are available
//
//		Iterator iterator = this.student.getRentalList().iterator();
//		String rentalList;
//		PdfPCell cell;
//		while(iterator.hasNext()){
//			rentalList = (String) iterator.next();
//			cell = new PdfPCell(new Phrase(rentalList));
//			table.addCell(cell);
//		}
	    
	    try {
			document.add(table);
		}
		catch (DocumentException e) {
			e.printStackTrace();
		}
	    
	    System.out.println("test");
	    System.out.println(document);	
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
		}
		catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param document represents the PDF before it is saved
	 */
	private void addRentalDisclosure(Document document){
		String disclosure = "Die oben angeführten Schulbücher habe ich erhalten.\n" +
								"Die ausgeliehenen Bücher habe ich auf VollstÃ¤ndigkeit und Beschädigung überprüft. "+
								"Beschädigte oder verlorengegangene Bücher müssen ersetzt werden.\n";
		PdfPTable table = new PdfPTable(1);
		PdfPCell cell = new PdfPCell(new Phrase(disclosure));
		table.addCell(cell);
		try {
			document.add(table);
		}
		catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
//		new MyPDFStudentList(null).savePDF("./testfiles/FirstPdf.pdf");
		new MyPDFStudentList(null).printPDF();
	}
}
