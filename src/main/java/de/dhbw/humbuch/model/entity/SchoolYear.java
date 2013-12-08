package de.dhbw.humbuch.model.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="schoolYear")
public class SchoolYear implements de.dhbw.humbuch.model.entity.Entity {

	@Id
	private int year;
	
	private Date from;
	private Date to;
	private Date endFirstTerm;
	private Date beginSecondTerm;
	
	public SchoolYear() {}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}

	public Date getEndFirstTerm() {
		return endFirstTerm;
	}

	public void setEndFirstTerm(Date endFirstTerm) {
		this.endFirstTerm = endFirstTerm;
	}

	public Date getBeginSecondTerm() {
		return beginSecondTerm;
	}

	public void setBeginSecondTerm(Date beginSecondTerm) {
		this.beginSecondTerm = beginSecondTerm;
	}
	
	
}
