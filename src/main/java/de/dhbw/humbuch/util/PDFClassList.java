package de.dhbw.humbuch.util;

import java.util.Map;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPTable;

import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.TeachingMaterial;


public final class PDFClassList extends PDFHandler {
	private Grade grade;
	private Map<Grade, Map<TeachingMaterial, Integer>> gradesMap;
	
	public PDFClassList(Map<Grade, Map<TeachingMaterial, Integer>> gradesMap){
		super();
		this.gradesMap = gradesMap;
	}

	protected void insertDocumentParts(Document document) {
		if(this.gradesMap != null){
			for(Grade grade : this.gradesMap.keySet()){
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

		for(TeachingMaterial teachingMaterial : map.keySet()) {
			String[] contentArray = {teachingMaterial.getName(),
			                         ""+ map.get(teachingMaterial)};
			PDFHandler.fillTableWithContentWithoutSpace(table, true, contentArray, true, 5f);
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

		String[] contentArray = {"Klasse: ", "" + this.grade.toString()}; 

		PDFHandler.fillTableWithContentWithoutSpace(table, false, contentArray, false, 0f);
		
		try {
			document.add(table);
		}
		catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	public PDFClassList(){
		
	}
}
