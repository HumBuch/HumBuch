package de.dhbw.humbuch.model.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="borrowedMaterial")
public class BorrowedMaterial implements de.dhbw.humbuch.model.entity.Entity {
	
	@Id
	private int id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="studentId", referencedColumnName="id")
	private Student student;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="teachingMaterialId", referencedColumnName="id")
	private TeachingMaterial teachingMaterial;
	
	private Date borrowFrom;
	private Date borrowUntil;
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

	public Date getBorrowUntil() {
		return borrowUntil;
	}

	public void setBorrowUntil(Date borrowUntil) {
		this.borrowUntil = borrowUntil;
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

	public static class Builder {
		private final Student student;
		private final TeachingMaterial teachingMaterial;
		private final Date borrowFrom;
		
		private Date borrowUntil;
		private Date returnDate;
		private boolean defect;
		private String defectComment;
		
		public Builder(Student student, TeachingMaterial teachingMaterial, Date borrowFrom) {
			this.student = student;
			this.teachingMaterial = teachingMaterial;
			this.borrowFrom = borrowFrom;
		}
		
		public Builder borrowUntil(Date borrowUntil) {
			this.borrowUntil = borrowUntil;
			return this;
		}
		
		public Builder returnDate(Date returnDate) {
			this.returnDate = returnDate;
			return this;
		}
		
		public Builder defect(boolean defect) {
			this.defect = defect;
			return this;
		}
		
		public Builder defectComment(String defectComment) {
			this.defectComment = defectComment;
			return this;
		}
		
		public BorrowedMaterial build() {
			return new BorrowedMaterial(this);
		}
	}
	
	private BorrowedMaterial(Builder builder) {
		this.student = builder.student;
		this.teachingMaterial = builder.teachingMaterial;
		this.borrowFrom = builder.borrowFrom;
		
		this.borrowUntil = builder.borrowUntil;
		this.returnDate = builder.returnDate;
		this.defect = builder.defect;
		this.defectComment = builder.defectComment;
	}
}
