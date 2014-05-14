package de.dhbw.humbuch.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author David Vitt
 *
 */
@Entity
@Table(name="parent")
public class Parent implements de.dhbw.humbuch.model.entity.Entity, Serializable {
	private static final long serialVersionUID = -969816596774143705L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String title;
	private String firstname;
	private String lastname;
	private String street;
	private int postcode;
	private String city;
	
	@OneToMany(mappedBy="parent", fetch=FetchType.LAZY)
	private List<Student> studentList = new ArrayList<>();
	
	/**
	 * Required by Hibernate.<p>
	 * Use the {@link Builder} instead.
	 * 
	 * @see Builder
	 */
	@Deprecated
	public Parent() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public int getPostcode() {
		return postcode;
	}

	public void setPostcode(int postcode) {
		this.postcode = postcode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
	public static class Builder {
		private final String firstname;
		private final String lastname;

		private String title;
		private String street;
		private int postcode;
		private String city;
		
		public Builder(String firstname, String lastname) {
			this.firstname = firstname;
			this.lastname = lastname;
		}
		
		public Builder title(String title) {
			this.title = title;
			return this;
		}
		
		public Builder street(String street) {
			this.street = street;
			return this;
		}
		
		public Builder postcode(int postcode) {
			this.postcode = postcode; 
			return this;
		}
		
		public Builder city(String city) {
			this.city = city;
			return this;
		}
		
		public Parent build() {
			return new Parent(this);
		}
	}

	private Parent(Builder builder) {
		firstname = builder.firstname;
		lastname = builder.lastname;

		title = builder.title;
		street = builder.street;
		postcode = builder.postcode;
		city = builder.city;
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
		if (!(obj instanceof Parent))
			return false;
		Parent other = (Parent) obj;
		if (getId() != other.getId())
			return false;
		return true;
	}
	
}
