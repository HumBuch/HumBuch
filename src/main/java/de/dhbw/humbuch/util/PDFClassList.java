package de.dhbw.humbuch.util;

import java.util.Map;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPTable;

import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.TeachingMaterial;

/**
 * Create a class list. It contains information about the amount of books belonging to a class.
 * @author Benjamin Räthlein
 *
 */
public final class PDFClassList extends PDFHandler {

	private Grade grade;
	private Map<Grade, Map<TeachingMaterial, Integer>> gradesMap;

	/**
	 * Prints all teachingMaterials and their amount of each grade in the map.
	 * 
	 * @param gradesMap
	 *            To each grade in the map another map belongs that contains all
	 *            teachingMaterials of the grade and the amount of their
	 *            occurrences.
	 */
	public PDFClassList(Map<Grade, Map<TeachingMaterial, Integer>> gradesMap) {
		super();
		this.gradesMap = gradesMap;
	}

	protected void insertDocumentParts(Document document) {
		if (this.gradesMap != null) {
			for (Grade grade : this.gradesMap.keySet()) {
				this.grade = grade;
				this.addHeading(document);
				this.addGradeInformation(document);
				this.addInformationAboutDocument(document, "Klassen-Liste");
				this.addContent(document);
				document.newPage();
				this.resetPageNumber();
			}
		}
	}

	protected void addContent(Document document) {
		PdfPTable table = this.createTableWithRentalInformationHeaderForClass();

		Map<TeachingMaterial, Integer> map = this.gradesMap.get(this.grade);

		for (TeachingMaterial teachingMaterial : map.keySet()) {
			String[] contentArray = { teachingMaterial.getName(), "" + map.get(teachingMaterial) };
			new PDFHandler.TableBuilder(table, contentArray).withBorder(true).isCenterAligned(true).padding(PDFHandler.CELL_PADDING).fillTable();
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
	 * @param document
	 *            represents the PDF before it is saved
	 */
	private void addGradeInformation(Document document) {
		PdfPTable table = PDFHandler.createMyStandardTable(2, new float[] { 1f, 6f });

		String[] contentArray = { "Klasse: ", "" + this.grade.toString() };
		new PDFHandler.TableBuilder(table, contentArray).withBorder(false).isCenterAligned(false).fillTable();
		
		try {
			document.add(table);
			PDFHandler.addEmptyLineToDocument(document, 1);
		}
		catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	public PDFClassList() {

	}
}
