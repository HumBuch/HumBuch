package de.dhbw.humbuch.util;

import java.util.List;
import java.util.Set;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPTable;

import de.dhbw.humbuch.model.SubjectHandler;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Student;

public final class PDFStudentList extends PDFHandler{
	private Student student;
	private List<BorrowedMaterial> borrowedMaterialList;
	private List<BorrowedMaterial> returnList;
	private List<BorrowedMaterial> lendingList;
	
	private Builder[] builders;
	
	/**
	 * The content of the PDFs is printed in the order of the students-set.
	 * @param builder
	 */
	public PDFStudentList(Builder... builder){
		super();
		this.builders = builder;
	}
	
	protected void insertDocumentParts(Document document){
		for(Builder builder : builders){
			this.student = builder.student;
			this.borrowedMaterialList = builder.borrowedMaterialList;
			this.lendingList = builder.lendingList;
			this.returnList = builder.returnList;

			this.addHeading(document, "Ausgabe-Liste 2013");
			this.addStudentInformation(document);
			this.addContent(document);
			this.addRentalDisclosure(document);
			this.addSignatureField(document, "Schüler");
			document.newPage();
			this.resetPageNumber();
		}		
	}
	
	protected void addContent(Document document) {
		if(this.borrowedMaterialList != null && !this.borrowedMaterialList.isEmpty()){
			PdfPTable table = PDFHandler.createMyStandardTable(1);
			PDFHandler.fillTableWithContent(table, false,
					new String[]{"\nDie folgenden Bücher befinden sich im Besitz des Schülers/der Schülerin: \n"}, false);
			 try {
					document.add(table);
			}
			catch (DocumentException e) {
				e.printStackTrace();
			}
			
			table = this.createTableWithRentalInformationHeader();

			for(BorrowedMaterial borrowedMaterial : this.borrowedMaterialList){
				String[] contentArray = {//borrowedMaterial.getTeachingMaterial().getSubject().getName(),
				                         ""+borrowedMaterial.getTeachingMaterial().getToGrade(),
				                         borrowedMaterial.getTeachingMaterial().getName(),
				                      	 "" };
				PDFHandler.fillTableWithContent(table, true, contentArray);		
			}
		    try {
				document.add(table);
				PDFHandler.addEmptyLineToDocument(document, 1);
			}
			catch (DocumentException e) {
				e.printStackTrace();
			}
		}
	    if(this.returnList != null && !this.returnList.isEmpty()){
	    	PdfPTable table = PDFHandler.createMyStandardTable(1);
			PDFHandler.fillTableWithContent(table, false,
					new String[]{"\n Die folgenden Bücher müssen zurückgegeben werden: \n"}, false);
			 try {
					document.add(table);
			}
			catch (DocumentException e) {
				e.printStackTrace();
			}
	    	
	    	table = this.createTableWithRentalInformationHeader();
			
	    	for(BorrowedMaterial borrowedMaterial : this.returnList){				
				String[] contentArray = {//borrowedMaterial.getTeachingMaterial().getSubject().getName(),
				                         ""+borrowedMaterial.getTeachingMaterial().getToGrade(),
				                         borrowedMaterial.getTeachingMaterial().getName(),
				                      	 "" };
				PDFHandler.fillTableWithContent(table, true, contentArray);		
			}
		    
		    try {
				document.add(table);
				PDFHandler.addEmptyLineToDocument(document, 1);
			}
			catch (DocumentException e) {
				e.printStackTrace();
			}
	    }
	    if(this.lendingList != null && !this.lendingList.isEmpty()){
	    	PdfPTable table = PDFHandler.createMyStandardTable(1);
			PDFHandler.fillTableWithContent(table, false,
					new String[]{"\n Die folgenden Bücher sollen ausgeliehen werden: \n"}, false);
			 try {
					document.add(table);
			}
			catch (DocumentException e) {
				e.printStackTrace();
			}	    	
	    	
	    	table = this.createTableWithRentalInformationHeader();
	    	
	    	for(BorrowedMaterial borrowedMaterial : this.lendingList){
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
	    
	}
	
	/**
	 * Inserts information about the student like grade, language, name etc.
	 * 
	 * @param document represents the PDF before it is saved
	 */	
	private void addStudentInformation(Document document){
		PdfPTable table = PDFHandler.createMyStandardTable(2, new float[]{1f, 6f});

		String[] contentArray = {"Schüler: ", this.student.getFirstname() + " " + this.student.getLastname(),
		                         "Klasse: ", "" + this.student.getGrade().toString(),
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
	
	/**
	 * 
	 * @param document represents the PDF before it is saved
	 */
	private void addRentalDisclosure(Document document){
		PdfPTable table = PDFHandler.createMyStandardTable(1);
		PDFHandler.fillTableWithContent(table, false,
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
	
	public static class Builder{		
		private Student student;
		private List<BorrowedMaterial> borrowedMaterialList;
		private List<BorrowedMaterial> lendingList;
		private List<BorrowedMaterial> returnList;
				
		public Builder(Student student){
		}
		
		public Builder(Set<Student> students){
		}
		
		public Builder borrowedMaterialList(List<BorrowedMaterial> borrowedMaterialList){
			this.borrowedMaterialList = borrowedMaterialList;
			if(borrowedMaterialList != null){
				this.student = borrowedMaterialList.get(0).getStudent();
			}
			return this;
		}
		
		public Builder lendingList(List<BorrowedMaterial> borrowedMaterialList){
			this.lendingList = borrowedMaterialList;
			if(borrowedMaterialList != null){
				this.student = borrowedMaterialList.get(0).getStudent();
			}
			return this;
		}
		
		public Builder returnList(List<BorrowedMaterial> borrowedMaterialList){
			this.returnList = borrowedMaterialList;
			if(borrowedMaterialList != null){
				this.student = borrowedMaterialList.get(0).getStudent();
			}
			return this;
		}
		
		@Deprecated
		public PDFStudentList build(){
			return new PDFStudentList(this);
		}
		
	}
}
