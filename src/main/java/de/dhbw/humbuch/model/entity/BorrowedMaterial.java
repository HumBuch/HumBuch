package de.dhbw.humbuch.model.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="borrowedMaterial")
public class BorrowedMaterial implements de.dhbw.humbuch.model.entity.Entity {
	
	@Id
	private int id;
	
	@ManyToOne
	@JoinColumn(name="studentId", referencedColumnName="id")
	private Student student;
	
	@ManyToOne
	@JoinColumn(name="teachingMaterialId", referencedColumnName="id")
	private TeachingMaterial teachingMaterial;
	
	private Date borrowFrom;
	private Date borrowTo;
	private Date returnDate;
	private boolean defect;
	private String defectComment;
	
	public BorrowedMaterial() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public TeachingMaterial getTeachingMaterial() {
		return teachingMaterial;
	}

	public void setTeachingMaterial(TeachingMaterial teachingMaterial) {
		this.teachingMaterial = teachingMaterial;
	}

	public Date getBorrowFrom() {
		return borrowFrom;
	}

	public void setBorrowFrom(Date borrowFrom) {
		this.borrowFrom = borrowFrom;
	}

	public Date getBorrowTo() {
		return borrowTo;
	}

	public void setBorrowTo(Date borrowTo) {
		this.borrowTo = borrowTo;
	}

	public Date getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}

	public boolean isDefect() {
		return defect;
	}

	public void setDefect(boolean defect) {
		this.defect = defect;
	}

	public String getDefectComment() {
		return defectComment;
	}

	public void setDefectComment(String defectComment) {
		this.defectComment = defectComment;
	}

	
	
}
