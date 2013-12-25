package de.dhbw.humbuch.util;

import java.util.Iterator;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPTable;

import de.dhbw.humbuch.model.GradeHandler;
import de.dhbw.humbuch.model.MapperAmountAndBorrowedMaterial;
import de.dhbw.humbuch.model.entity.Grade;


public final class PDFClassList extends PDFHandler {

	private Grade grade;

	//object student has to be replaced by a class object
	public PDFClassList(Grade grade) {
		super();
		this.grade = grade;
	}

	protected void insertDocumentParts(Document document) {
		this.addHeading(document, "Ausgabe-Liste 2013");
		this.addGradeInformation(document);
		this.addContent(document);
	}

	protected void addContent(Document document) {
		PdfPTable table = this.createTableWithRentalInformationHeader();

		List<MapperAmountAndBorrowedMaterial> gradeRentalList = GradeHandler.getAllRentedBooksOfGrade(this.grade);

		Iterator<MapperAmountAndBorrowedMaterial> iterator = gradeRentalList.iterator();
		MapperAmountAndBorrowedMaterial gradeRental;
		while (iterator.hasNext()) {
			gradeRental = iterator.next();
			String[] contentArray = {""+gradeRental.getBorrowedMaterial().getTeachingMaterial().getToGrade(),
			                         gradeRental.getBorrowedMaterial().getTeachingMaterial().getName(),
			                         ""+gradeRental.getAmount()};
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
	 * Inserts information about the grade.
	 * 
	 * @param document represents the PDF before it is saved
	 */	
	private void addGradeInformation(Document document){
		PdfPTable table = PDFHandler.createMyStandardTable(2, new float[]{1f, 6f});

		String[] contentArray = {"Klasse: ", "" + this.grade.getGrade(),
		                         "Schuljahr: ", "#SCHOOLYEAR"}; 
//		                         "Sprachenfolge: "+ ProfileHandler.getLanguageProfile(this.student.getProfile()) + "\n"
//					             + "Religionsunterricht: " + this.student.getProfile().getReligion().toString() + "\n"};
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
