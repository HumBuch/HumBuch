package de.dhbw.humbuch.model.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="teachingMaterial")
public class TeachingMaterial implements de.dhbw.humbuch.model.entity.Entity {

	@Id
	private int id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="categoryId", referencedColumnName="id")
	private Category category;
	
	@ElementCollection(targetClass=ProfileType.class)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name="teachingMaterialProfile", joinColumns = @JoinColumn(name="teachingMaterialId"))
	@Column(name="profileType")
	private Set<ProfileType> profileTypes = new HashSet<ProfileType>();
	
	private String name;
	private String producer;
	private String identifyingNumber;
	private double price;
	private String comment;
	
	private int fromGrade;
	private int fromTerm;
	private int toGrade;
	private int toTerm;
	private Date validFrom;
	private Date validUntil;
	
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

	public int getFromTerm() {
		return fromTerm;
	}

	public void setFromTerm(int fromTerm) {
		this.fromTerm = fromTerm;
	}

	public int getToGrade() {
		return toGrade;
	}

	public void setToGrade(int toGrade) {
		this.toGrade = toGrade;
	}

	public int getToTerm() {
		return toTerm;
	}

	public void setToTerm(int toTerm) {
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

	public Set<ProfileType> getProfileTypes() {
		return profileTypes;
	}

	public void setProfileTypes(Set<ProfileType> profileTypes) {
		this.profileTypes = profileTypes;
	}

}
