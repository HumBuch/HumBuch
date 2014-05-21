package de.dhbw.humbuch.util;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import de.dhbw.humbuch.model.SubjectHandler;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Student;

/**
 * Create a borrowed material list, a return list or a lending list.
 * Borrowed material list contains material that has to be borrowed by a student.
 * Return list contains material that has to be returned by a student.
 * Lending list contains material that has to be lent by a student.
 * 
 * @author Benjamin Räthlein
 *
 */
public final class PDFStudentList extends PDFHandler {

	private Student student;
	private List<BorrowedMaterial> borrowedMaterialList;
	private List<BorrowedMaterial> returnList;
	private List<BorrowedMaterial> lendingList;
	private boolean needEmpyLine = false;

	private Set<Builder> builders;

	/**
	 * For each student in the builder objects a PDF is created. This PDF can
	 * contain three different kinds of lists.
	 * 
	 * @param builder
	 *            can contain three kinds of lists. One list of materials the
	 *            student already received, one list of materials the student
	 *            will receive and one list of materials the student has to
	 *            return
	 * 
	 */
	public PDFStudentList(Builder... builder) {
		super();
		this.builders = new LinkedHashSet<>();
		for (Builder b : builder) {
			this.builders.add(b);
		}
	}

	/**
	 * For each student in the builder objects a PDF is created. This PDF can
	 * contain three different kinds of lists.
	 * 
	 * @param builder
	 *            can contain three kinds of lists. One list of materials the
	 *            student already received, one list of materials the student
	 *            will receive and one list of materials the student has to
	 *            return
	 * 
	 */
	public PDFStudentList(Set<Builder> builder) {
		super();
		this.builders = builder;
	}

	protected void insertDocumentParts(Document document) {
		for (Builder builder : builders) {
			if (builder.student != null) {
				this.student = builder.student;
			}
			else {
				continue;
			}
			this.borrowedMaterialList = builder.borrowedMaterialList;
			this.lendingList = builder.lendingList;
			this.returnList = builder.returnList;

			this.addHeading(document);
			this.addStudentInformation(document);
			
			this.addInformationAboutDocument(document, this.getDocumentTitle());
			this.addContent(document);
			document.newPage();
			this.resetPageNumber();
		}
	}

	private String getDocumentTitle() {
		if(this.borrowedMaterialList != null && !this.borrowedMaterialList.isEmpty()
				&& (this.lendingList == null || this.lendingList.isEmpty()) 
				&& (this.returnList == null || this.returnList.isEmpty())){
			return "Ausgeliehene Materialien";
		}
		if(this.lendingList != null && !this.lendingList.isEmpty()
				&& (this.borrowedMaterialList == null || this.borrowedMaterialList.isEmpty()) 
				&& (this.returnList == null || this.returnList.isEmpty())){
			return "Auszuleihende Materialien";
		}
		if(this.returnList != null && !this.returnList.isEmpty()
				&& (this.lendingList == null || this.lendingList.isEmpty()) 
				&& (this.borrowedMaterialList == null || this.borrowedMaterialList.isEmpty())){
			return "Zurückzugebende Materialien";
		}
		else{
			return "Material-Informationen";
		}
	}

	protected void addContent(Document document) {
		if (this.borrowedMaterialList != null && !this.borrowedMaterialList.isEmpty()) {
			PdfPTable table = PDFHandler.createMyStandardTable(1);
			new PDFHandler.TableBuilder(table,
					new String[] { "\nDie folgenden Lehrmittel befinden sich im Besitz des Schülers/der Schülerin:" })
					.font(FontFactory.getFont("Helvetica", 10, Font.BOLD)).fillTable();

			try {
				document.add(table);
				addEmptyLineToDocument(document, 1);
			}
			catch (DocumentException e) {
				e.printStackTrace();
			}

			table = this.createTableWithRentalInformationHeaderWithoutSignColumn();

			for (BorrowedMaterial borrowedMaterial : this.borrowedMaterialList) {
				String[] contentArray = { borrowedMaterial.getTeachingMaterial().getName(),
											"" + borrowedMaterial.getTeachingMaterial().getToGrade()};
				new PDFHandler.TableBuilder(table, contentArray).withBorder(true).isCenterAligned(true).padding(PDFHandler.CELL_PADDING).fillTable();
			}
			try {
				document.add(table);
				PDFHandler.addEmptyLineToDocument(document, 1);
			}
			catch (DocumentException e) {
				e.printStackTrace();
			}
		}		
		if (this.lendingList != null && !this.lendingList.isEmpty()) {
			PdfPTable table = PDFHandler.createMyStandardTable(1);

			new PDFHandler.TableBuilder(table, new String[] { "\nDie folgenden Lehrmittel sollen ausgeliehen werden:" })
					.font(FontFactory.getFont("Helvetica", 10, Font.BOLD)).fillTable();

			try {
				document.add(table);
				addEmptyLineToDocument(document, 1);
			}
			catch (DocumentException e) {
				e.printStackTrace();
			}

			table = this.createTableWithRentalInformationHeader();

			for (BorrowedMaterial borrowedMaterial : this.lendingList) {
				String[] contentArray = { borrowedMaterial.getTeachingMaterial().getName(),
											"" + borrowedMaterial.getTeachingMaterial().getToGrade(),
											"" };

				new PDFHandler.TableBuilder(table, contentArray).isCenterAligned(true).withBorder(true).padding(PDFHandler.CELL_PADDING).fillTable();
			}

			try {
				document.add(table);
				this.needEmpyLine = true;
			}
			catch (DocumentException e) {
				e.printStackTrace();
			}
		}
		if (this.returnList != null && !this.returnList.isEmpty()) {
			if(this.needEmpyLine){
				addEmptyLineToDocument(document, 1);
				this.needEmpyLine = false;
			}
			
			PdfPTable table = PDFHandler.createMyStandardTable(1);
			new PDFHandler.TableBuilder(table, new String[] { "\nDie folgenden Lehrmittel müssen zurückgegeben werden:" })
					.font(FontFactory.getFont("Helvetica", 10, Font.BOLD)).fillTable();

			try {
				document.add(table);
				addEmptyLineToDocument(document, 1);
			}
			catch (DocumentException e) {
				e.printStackTrace();
			}

			table = this.createTableForReturnPDF();

			for (BorrowedMaterial borrowedMaterial : this.returnList) {
				String[] contentArray = { borrowedMaterial.getTeachingMaterial().getName(),
											"" + borrowedMaterial.getTeachingMaterial().getToGrade(),
											"", "" };

				new PDFHandler.TableBuilder(table, contentArray).withBorder(true).isCenterAligned(true).padding(PDFHandler.CELL_PADDING).fillTable();
			}

			try {
				document.add(table);
				PDFHandler.addEmptyLineToDocument(document, 1);
				this.addRentalDisclosure(document);
				this.addSignatureField(document, "");
			}
			catch (DocumentException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Inserts information about the student like grade, language, name etc.
	 * 
	 * @param document
	 *            represents the PDF before it is saved
	 */
	private void addStudentInformation(Document document) {
		PdfPTable table = PDFHandler.createMyStandardTable(2, new float[] { 1f, 6f });

		String[] contentArray = { "Schüler: ", this.student.getFirstname() + " " + this.student.getLastname(),
									"Klasse: ", "" + this.student.getGrade().toString(),
									"Sprachen: ", SubjectHandler.getLanguageProfile(this.student.getProfile()),
									"Religion: ", SubjectHandler.getReligionProfile(this.student.getProfile()) };

		new PDFHandler.TableBuilder(table, contentArray).fillTable();

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
	 * @param document
	 *            represents the PDF before it is saved
	 */
	private void addRentalDisclosure(Document document) {
		PdfPTable table = PDFHandler.createMyStandardTable(1);
		
		new PDFHandler.TableBuilder(table, 
				new String[] { "\nDie oben markierten Lehrmittel hat der Schüler/die Schülerin zurückgegeben.\n" +
						"Die ausgeliehenen Lehrmittel wurden auf Vollständigkeit und Beschädigung überprüft. " +
						"Beschädigte oder verlorengegangene Lehrmittel wurden ersetzt." }).leading(1.25f).fillTable();

		try {
			document.add(table);
			addEmptyLineToDocument(document, 2);
		}
		catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * A table is generated with the header: Klasse, Bezeichnung Lehrmittel, Zurückgegeben (Ja, Nein)
	 * 
	 * @return PdfPTable
	 */
	protected PdfPTable createTableForReturnPDF() {		
		PdfPTable table = createMyStandardTable(4, new float[] { 2.25f, 1f, 0.5f, 0.5f });
		Font font = FontFactory.getFont("Helvetica", 12, Font.BOLD);
		
		PdfPCell cell = null;
		cell = new PdfPCell(new Phrase("Bezeichnung Lehrmittel", font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorderWidthBottom(0);
		cell.setPadding(CELL_PADDING);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("bis Klasse", font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorderWidthBottom(0);
		cell.setPadding(CELL_PADDING);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Zurückgegeben", font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setColspan(2);
		cell.setPadding(CELL_PADDING);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase(""));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorderWidthTop(0);
		cell.setPadding(CELL_PADDING);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase(""));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setBorderWidthTop(0);
		cell.setPadding(CELL_PADDING);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Ja", font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPadding(CELL_PADDING);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Nein", font));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPadding(CELL_PADDING);
		table.addCell(cell);

		return table;
	}


	public static class Builder {

		private Student student;
		private List<BorrowedMaterial> borrowedMaterialList;
		private List<BorrowedMaterial> lendingList;
		private List<BorrowedMaterial> returnList;

		/**
		 * A builder object can contain three different kinds of lists. Each
		 * list is optional. The builder can be passed to the constructor of
		 * PDFStudentList and the lists the builder contains will be printed
		 */
		public Builder() {
		}

		/**
		 * 
		 * @param borrowedMaterialList
		 *            list of materials the student already received
		 * @return the builder object that has to be passed to the
		 *         PDFStudentList constructor
		 */
		public Builder borrowedMaterialList(List<BorrowedMaterial> borrowedMaterialList) {
			this.borrowedMaterialList = borrowedMaterialList;
			if (borrowedMaterialList != null) {
				this.student = borrowedMaterialList.get(0).getStudent();
			}
			return this;
		}

		/**
		 * 
		 * @param borrowedMaterialList
		 *            list of materials the student will receive
		 * @return the builder object that has to be passed to the
		 *         PDFStudentList constructor
		 */
		public Builder lendingList(List<BorrowedMaterial> borrowedMaterialList) {
			this.lendingList = borrowedMaterialList;
			if (borrowedMaterialList != null) {
				this.student = borrowedMaterialList.get(0).getStudent();
			}
			return this;
		}

		/**
		 * 
		 * @param borrowedMaterialList
		 *            list of materials the student has to return
		 * @return
		 */
		public Builder returnList(List<BorrowedMaterial> borrowedMaterialList) {
			this.returnList = borrowedMaterialList;
			if (borrowedMaterialList != null) {
				this.student = borrowedMaterialList.get(0).getStudent();
			}
			return this;
		}

		@Deprecated
		public PDFStudentList build() {
			return new PDFStudentList(this);
		}

	}
}
