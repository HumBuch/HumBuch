package de.dhbw.humbuch.model.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="borrowedMaterial")
public class BorrowedMaterial implements de.dhbw.humbuch.model.entity.Entity, Serializable {
	private static final long serialVersionUID = -7956138735111492455L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="studentId", referencedColumnName="id")
	private Student student;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="teachingMaterialId", referencedColumnName="id")
	private TeachingMaterial teachingMaterial;
	
	private Date borrowFrom;
	private Date borrowUntil;
	private Date returnDate;
	private boolean received;
	private boolean defect;
	private String defectComment;
	
	/**
	 * Required by Hibernate.<p>
	 * Use the {@link Builder} instead.
	 * 
	 * @see Builder
	 */
	@Deprecated
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

	public boolean isReceived() {
		return received;
	}

	public void setReceived(boolean received) {
		this.received = received;
	}

	public static class Builder {
		private final Student student;
		private final TeachingMaterial teachingMaterial;
		private final Date borrowFrom;
		
		private Date borrowUntil;
		private Date returnDate;
		private boolean received;
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
		
		public Builder received(boolean received) {
			this.received = received;
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
		this.received = builder.received;
		this.defect = builder.defect;
		this.defectComment = builder.defectComment;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BorrowedMaterial other = (BorrowedMaterial) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
