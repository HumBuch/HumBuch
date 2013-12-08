package de.dhbw.humbuch.model.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="teachingMaterial")
public class TeachingMaterial implements de.dhbw.humbuch.model.entity.Entity {

	@Id
	private int id;
	
	@ManyToOne
	@JoinColumn(name="categoryId", referencedColumnName="id")
	private Category category;
	
	@ManyToOne
	@JoinColumn(name="subjectId", referencedColumnName="id")
	private Subject subject;
	
	private String name;
	private int fromGrade;
	private int fromTerm;
	private int toGrade;
	private int toTerm;
	private Date validFrom;
	private Date validUntil;
	private double price;
	private String publisher;
	private String orderNo;
	private int numberInStock;
	
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

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public int getNumberInStock() {
		return numberInStock;
	}

	public void setNumberInStock(int numberInStock) {
		this.numberInStock = numberInStock;
	}

	public int getFromTerm() {
		return fromTerm;
	}

	public void setFromTerm(int fromTerm) {
		this.fromTerm = fromTerm;
	}

	public int getToTerm() {
		return toTerm;
	}

	public void setToTerm(int toTerm) {
		this.toTerm = toTerm;
	}

	
}
