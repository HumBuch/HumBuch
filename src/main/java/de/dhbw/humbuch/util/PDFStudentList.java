package de.dhbw.humbuch.util;

import java.util.Iterator;
import java.util.LinkedHashSet;
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
	private Set<Student> students;
	private List<BorrowedMaterial> returnList;
	private List<BorrowedMaterial> lendingList;
	
	public PDFStudentList(Builder builder){
		super();
		this.students = builder.students;
		this.returnList = builder.returnList;
		this.lendingList = builder.lendingList;
	}
	
	protected void insertDocumentParts(Document document){
		if(this.student != null){
			this.addHeading(document, "Ausgabe-Liste 2013");
			this.addStudentInformation(document);
			this.addContent(document);
			this.addRentalDisclosure(document);
			this.addSignatureField(document, "Schüler");
		}
		else if(this.students != null){
			for(Student student : this.students){
				this.addHeading(document, "Ausgabe-Liste 2013");
				this.student = student;
				this.addStudentInformation(document);
				this.addContent(document);
				this.addRentalDisclosure(document);
				this.addSignatureField(document, "Schüler");
				document.newPage();
				this.resetPageNumber();
			}
		}
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
	 * Inserts information about the student like grade, language, name etc.
	 * 
	 * @param document represents the PDF before it is saved
	 */	
	private void addStudentInformation(Document document){
		PdfPTable table = PDFHandler.createMyStandardTable(2, new float[]{1f, 6f});

		String[] contentArray = {"Schüler: ", this.student.getFirstname() + " " + this.student.getLastname(),
		                         "Klasse: ", "" + this.student.getGrade().getGrade(),
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
		private Set<Student> students;
		private List<BorrowedMaterial> returnList;
		private List<BorrowedMaterial> lendingList;
				
		public Builder(Student student){
			this.students = new LinkedHashSet<Student>();
			this.students.add(student);
		}
		
		public Builder(Set<Student> students){
			this.students = students;
		}
		
		public Builder returnList(List<BorrowedMaterial> borrowedMaterial){
			this.returnList = borrowedMaterial;
			return this;
		}
		
		public Builder lendingList(List<BorrowedMaterial> borrowedMaterial){
			this.lendingList = borrowedMaterial;
			return this;
		}
		
		public PDFStudentList build(){
			return new PDFStudentList(this);
		}
		
	}
}
