package de.dhbw.humbuch.pdfExport;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public abstract class MyPDFHandler {
	Document document;
	
	/**
	 * 
	 * @param path links to the directory where the PDF file should be saved
	 */
	public MyPDFHandler(String path){
		this.document = new Document();
		try {
			PdfWriter.getInstance(document, new FileOutputStream(path));
		}
		catch (FileNotFoundException e) {			
			e.printStackTrace();
		}
		catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates the pdf with the information in the 
	 * 	object that was passed to the constructor previously.
	 */	
	public void createPDF(){
		this.document.open();
		this.addMetaData(document);
		this.insertDocumentParts(document);
		this.document.close();
	}
	
	
	/**
	 * Adds meta data to the PDF document.
	 * The information of using iText must be part of the meta data due to the
	 * 	license of the iText library!
	 * 
	 * @param document represents the PDF before it is saved
	 */
	private void addMetaData(Document document) {
		document.addTitle("Humbuch Schule");
		document.addSubject("Using iText");
		document.addKeywords("Java, PDF, iText");
		document.addAuthor("Schlager");
		document.addCreator("Schlager");
	}
	
	/**
	 * Set the logo of Humboldt on the left corner and the label 'Ausgabe-Liste 2013' 
	 * 	on the right corner on top of the document
	 * 
	 * @param document reference of the pdfDocument object
	 */	
	protected void addHeading(Document document, String listType) {
		Paragraph paragraph = new Paragraph();
		PdfPTable table = new PdfPTable(2);
		PdfPCell cell;		

		try {
			Image img = Image.getInstance("./res/Logo_Humboldt_Gym_70_klein.png");	
			img.setAlignment(Element.ALIGN_BOTTOM);
			cell = new PdfPCell(img);			
//			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//			
//			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
			cell.setBorder(0);
			table.addCell(cell);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (BadElementException e) {
			e.printStackTrace();
		}

//	    cell = new PdfPCell(new Phrase("Humboldt-Gymnasium-Karlsruhe"));
//		cell.setBorder(0);
//	    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//	    table.addCell(cell);
	    
	    cell = new PdfPCell(new Phrase(listType));
	    cell.setBorder(0);
	    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
	    cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
	    table.addCell(cell);
	    
	    paragraph.add(table);  
	    addEmptyLine(paragraph, 2);  
     
	    try {
			document.add(paragraph);
		}
		catch (DocumentException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Adds a signature field with a date field to the document.
	 * Should be the last part that is added to the document.
	 * 
	 * @param document represents the PDF before it is saved
	 * @param role word for the kind of person that shall sign the paper
	 */
	protected void addSignatureField(Document document, String role) {
		Paragraph paragraph = new Paragraph();
		addEmptyLine(paragraph, 2);
		
		//this table contains the signatureTable and the dataTable.
		// this purpose makes it easier to format
	    PdfPTable table = new PdfPTable(2);
	    
	    //the first column is double times greater than the second column
	    try {
			table.setWidths(new float[]{20f, 10f});
		}
		catch (DocumentException e) {
			e.printStackTrace();
		}
	    
	    //create and fill signature table
	    PdfPTable signatureTable = new PdfPTable(1);	    
	    PdfPCell cell = new PdfPCell(new Phrase(""));	    
	    //just the bottom border will be displayed (line for signature)
	    cell.setBorderWidthTop(0);
	    cell.setBorderWidthLeft(0);
	    cell.setBorderWidthRight(0);
	    
	    signatureTable.addCell(cell);
	    
	    cell = new PdfPCell(new Phrase("Unterschrift " + role));
	    cell.setBorder(0);
	    	    
	    signatureTable.addCell(cell);
	    
	    //put signature table into the 'parent' table
	    cell = new PdfPCell(signatureTable);
	    cell.setBorder(0);
	    table.addCell(cell);
	    
	    //create and fill date table
	    PdfPTable dateTable = new PdfPTable(1);	    
	    
	    cell = new PdfPCell(new Phrase(""));
	    //just the bottom border will be displayed (line for date)
	    cell.setBorderWidthTop(0);
	    cell.setBorderWidthLeft(0);
	    cell.setBorderWidthRight(0);
	       
	    dateTable.addCell(cell);
	 
	    cell = new PdfPCell(new Phrase("Datum"));
	    cell.setBorder(0);
	    	    
	    dateTable.addCell(cell);
	    
	    //put date table into the 'parent' table
	    cell = new PdfPCell(dateTable);
	    cell.setBorder(0);
	    table.addCell(cell);
	   
	    paragraph.add(table);
	    try {
			document.add(paragraph);
		}
		catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return PdfPTable
	 */
	protected PdfPTable createTableWithRentalInformationHeader(){
		PdfPTable table = new PdfPTable(5);
		
		PdfPCell cell = new PdfPCell(new Phrase("Fach"));
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Klasse"));
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Bezeichnung Lehrmittel"));
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Preis"));
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Anzahl"));
		table.addCell(cell);
		
		return table;		
	}
	
	protected static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}
	
	
	/**
	 * In this method all parts of the document shall be 'put' together.
	 * @param document represents the PDF before it is saved
	 */
	protected abstract void insertDocumentParts(Document document);
	
	/**
	 * In this method the PDF-specific information shall 
	 * 	be inserted into the document.
	 * @param document represents the PDF before it is saved
	 */
	protected abstract void addContent(Document document);
} 