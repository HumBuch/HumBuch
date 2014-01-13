package de.dhbw.humbuch.util;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
	private Map<Student, List<BorrowedMaterial>> studentToReturnListsMap;
	private List<BorrowedMaterial> returnList;
	private Map<Student, List<BorrowedMaterial>> studentToLendingListsMap;
	private List<BorrowedMaterial> lendingList;
	
	public PDFStudentList(Builder builder){
		super();
		this.students = builder.students;
		this.studentToReturnListsMap = builder.studentToReturnListsMap;
		this.studentToLendingListsMap = builder.studentToLendingListsMap;
	}
	
	protected void insertDocumentParts(Document document){
		if(this.students != null){
			for(Student student : this.students){
				this.addHeading(document, "Ausgabe-Liste 2013");
				this.student = student;
				this.returnList = studentToReturnListsMap.get(student);
				this.lendingList = studentToLendingListsMap.get(student);
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
		PdfPTable table = PDFHandler.createMyStandardTable(1);
		PDFHandler.fillTableWithContent(table, false,
				new String[]{"\nDie folgenden Bücher befinden im Besitz des Schülers/der Schülerin: \n"}, false);
		 try {
				document.add(table);
		}
		catch (DocumentException e) {
			e.printStackTrace();
		}
		
		table = this.createTableWithRentalInformationHeader();
		
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
			PDFHandler.addEmptyLineToDocument(document, 2);
		}
		catch (DocumentException e) {
			e.printStackTrace();
		}
	    
	    if(this.returnList != null && !this.returnList.isEmpty()){
	        table = PDFHandler.createMyStandardTable(1);
			PDFHandler.fillTableWithContent(table, false,
					new String[]{"\n Die folgenden Bücher müssen zurückgegeben werden: \n"}, false);
			 try {
					document.add(table);
			}
			catch (DocumentException e) {
				e.printStackTrace();
			}
	    	
	    	table = this.createTableWithRentalInformationHeader();
			
			iterator = this.returnList.iterator();
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
				PDFHandler.addEmptyLineToDocument(document, 2);
			}
			catch (DocumentException e) {
				e.printStackTrace();
			}
	    }
	    if(this.lendingList != null && !this.lendingList.isEmpty()){
	    	table = PDFHandler.createMyStandardTable(1);
			PDFHandler.fillTableWithContent(table, false,
					new String[]{"\n Die folgenden Bücher sollen ausgeliehen werden: \n"}, false);
			 try {
					document.add(table);
			}
			catch (DocumentException e) {
				e.printStackTrace();
			}	    	
	    	
	    	table = this.createTableWithRentalInformationHeader();
			
			iterator = this.lendingList.iterator();
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
		private Set<Student> students;
		private Map<Student, List<BorrowedMaterial>> studentToReturnListsMap;
		private Map<Student, List<BorrowedMaterial>> studentToLendingListsMap;
				
		public Builder(Student student){
			this.students = new LinkedHashSet<Student>();
			this.students.add(student);
		}
		
		public Builder(Set<Student> students){
			this.students = students;
		}
		
		public Builder returnMap(Map<Student, List<BorrowedMaterial>> studentToBorrowedMaterialMap){
			this.studentToReturnListsMap = studentToBorrowedMaterialMap;
			return this;
		}
		
		public Builder lendingMap(Map<Student, List<BorrowedMaterial>> studentToBorrowedMaterialMap){
			this.studentToLendingListsMap = studentToBorrowedMaterialMap;
			return this;
		}
		
		public PDFStudentList build(){
			return new PDFStudentList(this);
		}
		
	}
}
