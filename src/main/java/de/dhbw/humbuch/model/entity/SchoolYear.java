package de.dhbw.humbuch.model.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="schoolYear")
public class SchoolYear implements de.dhbw.humbuch.model.entity.Entity, Serializable, Comparable<SchoolYear> {
	private static final long serialVersionUID = -3752454317452902743L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	private String year;
	private Date fromDate;
	private Date toDate;
	private Date endFirstTerm;
	private Date beginSecondTerm;
	
	public SchoolYear() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
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
	
	public Date getEndDateOfTerm(Term term) {
		if(term == Term.FIRST) {
			return getEndFirstTerm();
		}
		else {
			return getToDate();
		}
	}
	
	public Term getCurrentTerm() {
//		if(getEndFirstTerm().after(new Date())) {
//			return Term.FIRST;
//		} else {
//			return Term.SECOND;
//		}
		
		if(getBeginSecondTerm().compareTo(new Date()) <= 0) {
			return Term.SECOND;
		} else {
			return Term.FIRST;
		}
	}
	
	public boolean isCurrentYear() {
		Date today = new Date();
		if(getFromDate().before(today) && getToDate().after(today)) {
			return true;
		}
		
		return false;
	}
	
	public enum Term {
		FIRST("1. Halbjahr"),
		SECOND("2. Halbjahr");
		
		private String value;
		
		private Term(String value) {
			this.value = value;
		}
		
		@Override
		public String toString() {
			return value;
		}
	}
	
	public static class Builder {
		private final String year;
		private final Date fromDate;
		private final Date toDate;
		
		private Date endFirstTerm;
		private Date beginSecondTerm;
		
		public Builder(String year, Date fromDate, Date toDate) {
			this.year = year;
			this.fromDate = fromDate;
			this.toDate = toDate;
		}
		
		public Builder endFirstTerm(Date endFirstTerm) {
			this.endFirstTerm = endFirstTerm;
			return this;
		}
		
		public Builder beginSecondTerm(Date beginSecondTerm) {
			this.beginSecondTerm = beginSecondTerm;
			return this;
		}
		
		public SchoolYear build() {
			return new SchoolYear(this);
		}
	}
	
	private SchoolYear(Builder builder) {
		this.year = builder.year;
		this.fromDate = builder.fromDate;
		this.toDate = builder.toDate;

		this.endFirstTerm = builder.endFirstTerm;
		this.beginSecondTerm = builder.beginSecondTerm;
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
		if (!(obj instanceof SchoolYear))
			return false;
		SchoolYear other = (SchoolYear) obj;
		if (getId() != other.getId())
			return false;
		return true;
	}

	@Override
	public int compareTo(SchoolYear o) {
		int compareResult = getFromDate().compareTo(o.getFromDate());
		if(compareResult != 0) {
			return compareResult;
		}
		
		return Integer.compare(hashCode(), o.hashCode());
	}
	
}
