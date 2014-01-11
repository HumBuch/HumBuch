package de.dhbw.humbuch.model.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Dunning {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	private Student student;
	
	@ElementCollection(targetClass = Type.class)
	@Enumerated(EnumType.STRING)
//	@CollectionTable(name="dunning", joinColumns = @JoinColumn(name="studentId"))
	@Column(name="type")
	private Type type = Type.TYPE1;
	
	@ElementCollection(targetClass = Status.class)
	@Enumerated(EnumType.STRING)
//	@CollectionTable(name="dunning", joinColumns = @JoinColumn(name="studentId"))
	@Column(name="status")
	private Status status = Status.OPENED;
	
	private Set<BorrowedMaterial> borrowedMaterials = new HashSet<BorrowedMaterial>();
	
//	private Date openingDate;
//	private Date sentDate;
//	private Date closedDate;
	
	private Map<Status, Date> statusDateMapping = new HashMap<Status, Date>();
	
	/**
	 * Use the {@link Builder} instead.
	 * 
	 * @see Builder
	 */
	@Deprecated
	public Dunning() {}
	
	public Date getStatusDate(Status status) {
		return statusDateMapping.get(status);
	}
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		if(this.status != status) {
			this.status = status;
			statusDateMapping.put(status, new Date());
		}
	}

	public Set<BorrowedMaterial> getBorrowedMaterials() {
		return borrowedMaterials;
	}

	public void setBorrowedMaterials(Set<BorrowedMaterial> borrowedMaterials) {
		this.borrowedMaterials = borrowedMaterials;
	}

	public boolean addBorrowedMaterials(BorrowedMaterial borrowedMaterial) {
		return borrowedMaterials.add(borrowedMaterial);
	}
	
	public int getId() {
		return id;
	}

	public Student getStudent() {
		return student;
	}

	public Type getType() {
		return type;
	}

	public static class Builder {
		private Student student;
		private Type type;
		private Status status;
		private Set<BorrowedMaterial> borrowedMaterials;
		
		public Builder(Student student) {
			this.student = student;
		}
		
		public Builder type(Type type) {
			this.type = type;
			return this;
		}
		
		public Builder status(Status status) {
			this.status = status;
			return this;
		}
		
		public Builder borrowedMaterials(Set<BorrowedMaterial> borrowedMaterials) {
			this.borrowedMaterials = borrowedMaterials;
			return this;
		}
		
		public Dunning build() {
			return new Dunning(this);
		}
	}
	
	private Dunning(Builder builder) {
		student = builder.student;
		type = builder.type;
		status = builder.status;
		borrowedMaterials = builder.borrowedMaterials;
	}
	
	public enum Status {
		OPENED("zu senden"),
		SENT("gesendet"),
		CLOSED("erledigt");

		private String value;
		
		private Status(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}
	}
	
	public enum Type {
		TYPE1("1. Mahnung"), 
		TYPE2("2. Mahnung");

		private String value;

		private Type(String value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return value;
		}
	}
}
