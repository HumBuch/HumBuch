package de.dhbw.humbuch.util;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPTable;

import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Student;

/**
 * Create dunnings. First dunning addresses the student, second dunning the parent of the student.
 * @author Benjamin Räthlein
 *
 */
public class PDFDunning extends PDFHandler {

	Student student;
	Set<Student> students;
	boolean secondDunning;
	List<BorrowedMaterial> borrowedMaterials;
	Set<List<BorrowedMaterial>> borrowedMaterialsSet;

	private PDFDunning() {
	};

	/**
	 * 
	 * For each BorrowedMaterial list a Dunning PDF is created. It addresses the
	 * student the list belongs to.
	 * 
	 * @param borrowedMaterials
	 *            Set of borrowedMaterial lists
	 * @return an instance of PDFDunning
	 */
	public static PDFDunning createFirstDunning(Set<List<BorrowedMaterial>> borrowedMaterials) {
		PDFDunning pdfDunning = new PDFDunning();
		pdfDunning.borrowedMaterialsSet = borrowedMaterials;
		pdfDunning.secondDunning = false;
		return pdfDunning;
	}

	/**
	 * 
	 * For each BorrowedMaterial list a Dunning PDF is created. It addresses the
	 * parent of the student the list belongs to.
	 * 
	 * @param borrowedMaterials
	 *            Set of borrowedMaterial lists
	 * @return an instance of PDFDunning
	 */
	public static PDFDunning createSecondDunning(Set<List<BorrowedMaterial>> borrowedMaterials) {
		PDFDunning pdfDunning = new PDFDunning();
		pdfDunning.borrowedMaterialsSet = borrowedMaterials;
		pdfDunning.secondDunning = true;
		return pdfDunning;
	}

	protected void insertDocumentParts(Document document) {
		for (List<BorrowedMaterial> borrowedMaterials : this.borrowedMaterialsSet) {
			this.addHeading(document);
			if (borrowedMaterials.size() != 0) {
				this.borrowedMaterials = borrowedMaterials;
				this.student = borrowedMaterials.get(0).getStudent();
				if (this.secondDunning) {
					this.addParentInformation(document);
				}
				this.addStudentInformation(document);
				this.addInformationAboutDocument(document, "Mahnungs-Liste");
				this.addContent(document);
				document.newPage();
				this.resetPageNumber();
			}
		}
	}

	protected void addContent(Document document) {
		PdfPTable table = PDFHandler.createMyStandardTable(1);
		String dunningText = "";
		if (!this.secondDunning) {
			dunningText = "Wir bitten darum, folgende Lehrmittel innerhalb von 2 Wochen zurückzugeben oder Ersatz zu beschaffen:";
		}
		else {
			dunningText = "Sehr geehrte/r " + student.getParent().getTitle() + " " + student.getParent().getLastname() + ",\n\n"
					+ "leider müssen wir Ihnen mitteilen, dass " + student.getFirstname() + " trotz bereits erfolgter Mahnung die unten aufgelisteten"
					+ " Lehrmittel nicht zurückgegeben hat. Wir bitten darum, folgende Lehrmittel innerhalb von 2 Wochen zurückzugeben oder Ersatz zu beschaffen. \n\n"
					+ "Mit freundlichen Grüßen \n"
					+ "Ihre Schulverwaltung";
		}
		new PDFHandler.TableBuilder(table, new String[] { dunningText }).leading(1.25f).fillTable();

		try {
			document.add(table);
			addEmptyLineToDocument(document, 1);
		}
		catch (DocumentException e) {
			e.printStackTrace();
		}

		table = this.createTableWithRentalInformationHeaderWithoutSignColumn();

		Iterator<BorrowedMaterial> iterator = this.borrowedMaterials.iterator();
		BorrowedMaterial borrowedMaterial;
		while (iterator.hasNext()) {
			borrowedMaterial = (BorrowedMaterial) iterator.next();
			String[] contentArray = { borrowedMaterial.getTeachingMaterial().getName(),
										"" + borrowedMaterial.getTeachingMaterial().getToGrade()};

			new PDFHandler.TableBuilder(table, contentArray).withBorder(true).
					isCenterAligned(true).padding(PDFHandler.CELL_PADDING).fillTable();
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
	 * @param document
	 *            represents the PDF before it is saved
	 */
	private void addStudentInformation(Document document) {
		PdfPTable table = PDFHandler.createMyStandardTable(2, new float[] { 1f, 6f });
		table.setSpacingBefore(20f);
		
		String[] contentArray = { "Schüler: ", this.student.getFirstname() + " " + this.student.getLastname(),
									"Klasse: ", "" + this.student.getGrade().toString() };
		
		new PDFHandler.TableBuilder(table, contentArray).fillTable();
		
		try {
			document.add(table);
			PDFHandler.addEmptyLineToDocument(document, 1);

		}
		catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	private void addParentInformation(Document document) {				
		PdfPTable table = PDFHandler.createMyStandardTable(1);
		table.setSpacingBefore(23f);
		
		String[] contentArray = { this.student.getParent().getTitle(),
									this.student.getParent().getFirstname() + " " + this.student.getParent().getLastname(),
									this.student.getParent().getStreet(),
									this.student.getParent().getCity() + " " + this.student.getParent().getPostcode() + "\n" };
		new PDFHandler.TableBuilder(table, contentArray).fillTable();

		try {
			document.add(table);
			PDFHandler.addEmptyLineToDocument(document, 1);
		}
		catch (DocumentException e) {
			e.printStackTrace();
		}
	}

}
