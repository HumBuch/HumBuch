package de.dhbw.humbuch.model.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.EnumSet;
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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.dhbw.humbuch.model.entity.SchoolYear.Term;

/**
 * @author David Vitt
 *
 */
@Entity
@Table(name="teachingMaterial")
public class TeachingMaterial implements de.dhbw.humbuch.model.entity.Entity, Serializable, Comparable<TeachingMaterial> {
	private static final long serialVersionUID = -6153270685462221761L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="categoryId", referencedColumnName="id")
	private Category category;
	
	@ElementCollection(targetClass=Subject.class)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name="teachingMaterialSubject", joinColumns = @JoinColumn(name="teachingMaterialId"))
	@Column(name="subject")
	private Set<Subject> profile = EnumSet.noneOf(Subject.class);
	
	private String name;
	private String producer;
	private String identifyingNumber;
	private double price;
	private String comment;
	
	private int fromGrade;
	private int toGrade;
	
	@Enumerated(EnumType.ORDINAL)
	private Term fromTerm = Term.FIRST;
	
	@Enumerated(EnumType.ORDINAL)
	private Term toTerm = Term.SECOND;
	
	private Date validFrom;
	private Date validUntil;
	
	/**
	 * Required by Hibernate.<p>
	 * Use the {@link Builder} instead.
	 * 
	 * @see Builder
	 */
	@Deprecated
	public TeachingMaterial() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProducer() {
		return producer;
	}

	public void setProducer(String producer) {
		this.producer = producer;
	}

	public String getIdentifyingNumber() {
		return identifyingNumber;
	}

	public void setIdentifyingNumber(String identifyingNumber) {
		this.identifyingNumber = identifyingNumber;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getFromGrade() {
		return fromGrade;
	}

	public void setFromGrade(int fromGrade) {
		this.fromGrade = fromGrade;
	}

	public int getToGrade() {
		return toGrade;
	}

	public void setToGrade(int toGrade) {
		this.toGrade = toGrade;
	}

	public Term getFromTerm() {
		return fromTerm;
	}

	public void setFromTerm(Term fromTerm) {
		this.fromTerm = fromTerm;
	}

	public Term getToTerm() {
		return toTerm;
	}

	public void setToTerm(Term toTerm) {
		this.toTerm = toTerm;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(Date validUntil) {
		this.validUntil = validUntil;
	}

	public Set<Subject> getProfile() {
		return profile;
	}

	public void setProfile(Set<Subject> profile) {
		this.profile = profile;
	}

	public void addSubject(Subject subject) {
		getProfile().add(subject);
	}
	
	public boolean hasSubject(Subject subject) {
		return getProfile().contains(subject);
	}
	
	public static class Builder {
		private final Category category;
		private final String name;
		private final String identifyingNumber;
		private final Date validFrom;
		
		private Set<Subject> profile;
		private String producer;
		private double price;
		private String comment;
		private int fromGrade;
		private int toGrade;
		private Term fromTerm;
		private Term toTerm;
		private Date validUntil;
		
		public Builder(Category category, String name, String identifyingNumber, Date validFrom) {
			this.category = category;
			this.name = name;
			this.identifyingNumber = identifyingNumber;
			this.validFrom = validFrom;
			this.fromTerm = Term.FIRST;
			this.toTerm = Term.SECOND;
		}
		
		public Builder profile(Set<Subject> profile) {
			this.profile = profile;
			return this;
		}
		
		public Builder producer(String producer) {
			this.producer = producer;
			return this;
		}
		
		public Builder price(double price) {
			this.price = price;
			return this;
		}
		
		public Builder comment(String comment) {
			this.comment = comment;
			return this;
		}
		
		public Builder fromGrade(int fromGrade) {
			this.fromGrade = fromGrade;
			return this;
		}
		
		public Builder toGrade(int toGrade) {
			this.toGrade = toGrade;
			return this;
		}
		
		public Builder fromTerm(Term fromTerm) {
			this.fromTerm = fromTerm;
			return this;
		}
		
		public Builder toTerm(Term toTerm) {
			this.toTerm = toTerm;
			return this;
		}
		
		public Builder validUntil(Date validUntil) {
			this.validUntil = validUntil;
			return this;
		}
		
		public TeachingMaterial build() {
			return new TeachingMaterial(this);
		}
	}
	
	private TeachingMaterial(Builder builder) {
		this.category = builder.category;
		this.name = builder.name;
		this.identifyingNumber = builder.identifyingNumber;
		this.validFrom = builder.validFrom;
		
		this.profile = builder.profile;
		this.producer = builder.producer;
		this.price = builder.price;
		this.comment = builder.comment;
		this.fromGrade = builder.fromGrade;
		this.toGrade = builder.toGrade;
		this.fromTerm = builder.fromTerm;
		this.toTerm = builder.toTerm;
		this.validUntil = builder.validUntil;
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
		if (!(obj instanceof TeachingMaterial))
			return false;
		TeachingMaterial other = (TeachingMaterial) obj;
		if (getId() != other.getId())
			return false;
		return true;
	}

	@Override
	public int compareTo(TeachingMaterial o) {
		int compareResult = getName().compareTo(o.getName());
		if(compareResult != 0) {
			return compareResult;
		}
		
		return Integer.compare(hashCode(), o.hashCode());
	}
	
}
