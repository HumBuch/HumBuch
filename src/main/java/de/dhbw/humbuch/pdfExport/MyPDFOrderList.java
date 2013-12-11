package de.dhbw.humbuch.pdfExport;

import java.util.ArrayList;

import com.lowagie.text.Document;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

public final class MyPDFOrderList extends MyPDFHandler {
//	private ArrayList orderList;
	
	public MyPDFOrderList(String path, ArrayList orderList){
		super(path);
	}
	
	protected void insertDocumentParts(Document document) {
		this.addHeading(document, "Bestell-Liste 2013");
	}
	
	protected void addContent(Document document) {
		PdfPTable table = createTableWithOrderListHeader();
		
		//iterate over order-list and append data to table
		//TODO implement appending of data to this table
	}
	
	
	/**
	 * Creates and returns a table with a header row for an order-list
	 * 
	 * @return PdfPTable
	 */
	private PdfPTable createTableWithOrderListHeader(){
		PdfPTable table = new PdfPTable(10);
		PdfPCell cell = new PdfPCell(new Phrase(""));
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Bezeichnung Lehrmittel"));
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Verliehen"));
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Zur�ck"));
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Neuleihe"));
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Weiter in Leihe"));
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Verf�gbar"));
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Reserve"));
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Bestell.-Kalk."));
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Zu Bestellen"));
		table.addCell(cell);
		
		return table;
	}

}
