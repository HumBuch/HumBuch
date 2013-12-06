package de.dhbw.humbuch.pdfExport;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPTable;

import de.dhbw.humbuch.model.entity.Student;


public final class MyPDFClassList extends MyPDFHandler {
//	private Grade grade;
	
	//object student has to be replaced by a class object
	public MyPDFClassList(String path, Student student) {
		super(path);	
//		this.grade = student;
	}
	
	protected void insertDocumentParts(Document document){
		this.addHeading(document, "Ausgabe-Liste 2013");
		this.addContent(document);
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
}
