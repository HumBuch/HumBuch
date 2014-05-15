package de.dhbw.humbuch.model.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.Table;

/**
 * @author David Vitt
 *
 */
@Entity
@Table(name="dunning")
public class Dunning implements de.dhbw.humbuch.model.entity.Entity, Serializable {
	private static final long serialVersionUID = -6187908554803908912L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="studentId", referencedColumnName="id")
	private Student student;
	
	@Enumerated(EnumType.STRING)
	@Column(name="type")
	private Type type = Type.TYPE1;
	
	@Enumerated(EnumType.STRING)
	@Column(name="status")
	private Status status;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name="dunning_has_borrowedMaterial",
			joinColumns={@JoinColumn(name="dunningId", referencedColumnName="id")},
			inverseJoinColumns={@JoinColumn(name="borrowedMaterialId", referencedColumnName="id"),
								@JoinColumn(name="borrowedMaterial_studentId", referencedColumnName="studentId")}
			)
	private Set<BorrowedMaterial> borrowedMaterials = new HashSet<BorrowedMaterial>();
	
	@ElementCollection
	@MapKeyEnumerated(EnumType.STRING)
	@MapKeyColumn(name="status")
	@CollectionTable(name="dunningDate", joinColumns = @JoinColumn(name="dunningId"))
	@Column(name="statusDate")
	private Map<Status, Date> statusDateMapping = new HashMap<Status, Date>();
	
	/**
	 * Required by Hibernate.<p>
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
		private int id;
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
		
		public Builder id(int id) {
			this.id = id;
			return this;
		}
		
		public Dunning build() {
			return new Dunning(this);
		}
	}
	
	private Dunning(Builder builder) {
		id = builder.id;
		student = builder.student;
		type = builder.type;
		setStatus(Status.OPENED);
		status = builder.status;
		borrowedMaterials = builder.borrowedMaterials;
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
		if (!(obj instanceof Dunning))
			return false;
		Dunning other = (Dunning) obj;
		if (getId() != other.getId())
			return false;
		return true;
	}

	public enum Status {
		OPENED("Offen"),
		SENT("Versendet"),
		CLOSED("Erledigt");

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
