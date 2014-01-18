package de.dhbw.humbuch.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="student")
public class Student implements de.dhbw.humbuch.model.entity.Entity, Serializable {
	private static final long serialVersionUID = -3020872456290703528L;

	@Id
	private int id;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="gradeId", referencedColumnName="id")
	private Grade grade;
	private String lastname;
	private String firstname;
	private Date birthday;
	private String gender;
	private boolean leavingSchool;
	
	@OneToMany(mappedBy="student", fetch=FetchType.LAZY)
	private List<BorrowedMaterial> borrowedList = new ArrayList<BorrowedMaterial>();

	@ElementCollection(targetClass=Subject.class)
	@Enumerated(EnumType.STRING)
	@CollectionTable(name="studentSubject", joinColumns = @JoinColumn(name="studentId"))
	@Column(name="subject")
	private Set<Subject> profile = EnumSet.noneOf(Subject.class);
	
	@ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="parentId", referencedColumnName="id")
	private Parent parent;
	
	@OneToMany(mappedBy="student", fetch=FetchType.LAZY)
	private List<Dunning> dunningList = new ArrayList<Dunning>();
	
	/**
	 * Required by Hibernate.<p>
	 * Use the {@link Builder} instead.
	 * 
	 * @see Builder
	 */
	@Deprecated
	public Student() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Grade getGrade() {
		return grade;
	}

	public void setGrade(Grade grade) {
		this.grade = grade;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Parent getParent() {
		return parent;
	}

	public void setParent(Parent parent) {
		this.parent = parent;
	}

	public List<BorrowedMaterial> getBorrowedList() {
		return borrowedList;
	}

	public void setBorrowedList(List<BorrowedMaterial> borrowedList) {
		this.borrowedList = borrowedList;
	}
	
	public Set<Subject> getProfile() {
		return profile;
	}

	public void setProfile(Set<Subject> profile) {
		this.profile = profile;
	}
	
	public boolean isLeavingSchool() {
		return leavingSchool;
	}

	public void setLeavingSchool(boolean leavingSchool) {
		this.leavingSchool = leavingSchool;
	}
	
	public List<Dunning> getDunningList() {
		return dunningList;
	}

	public List<BorrowedMaterial> getUnreceivedBorrowedList() {
		List<BorrowedMaterial> unreceivedBorrowedMaterials = new ArrayList<BorrowedMaterial>();
		for (BorrowedMaterial borrowedMaterial : getBorrowedList()) {
			if(!borrowedMaterial.isReceived()) {
				unreceivedBorrowedMaterials.add(borrowedMaterial);
			}
		}
		
		return unreceivedBorrowedMaterials;
	}
	
	public List<BorrowedMaterial> getReceivedBorrowedMaterials() {
		List<BorrowedMaterial> receivedBorrowedMaterials = getBorrowedList();
		receivedBorrowedMaterials.removeAll(getUnreceivedBorrowedList());
		return receivedBorrowedMaterials;
	}
	
	public boolean hasUnreceivedBorrowedMaterials() {
		for(BorrowedMaterial borrowedMaterial : getBorrowedList()) {
			if(!borrowedMaterial.isReceived()) {
				return true;
			}
		}
		
		return false;
	}

	public static class Builder {
		private final int id;
		private final String firstname;
		private final String lastname;
		private final Date birthday;
		private final Grade grade;
		
		private String gender;
		private List<BorrowedMaterial> borrowedList = new ArrayList<BorrowedMaterial>();
		private Set<Subject> profile = EnumSet.noneOf(Subject.class);
		private Parent parent;
		private boolean leavingSchool;
		
		public Builder(int id, String firstname, String lastname, Date birthday, Grade grade) {
			this.id = id;
			this.firstname = firstname;
			this.lastname = lastname;
			this.birthday = birthday;
			this.grade = grade;
		}
		
		public Builder gender(String gender) {
			this.gender = gender;
			return this;
		}
		
		public Builder borrowedList(List<BorrowedMaterial> borrowedList) {
			this.borrowedList = borrowedList;
			return this;
		}
		
		public Builder profile(Set<Subject> profile) {
			this.profile = profile;
			return this;
		}
		
		public Builder parent(Parent parent) {
			this.parent = parent;
			return this;
		}
		
		public Builder leavingSchool(boolean leavingSchool){
			this.leavingSchool = leavingSchool;
			return this;
		}
		
		public Student build() {
			return new Student(this);
		}
	}
	
	private Student(Builder builder) {
		id = builder.id;
		firstname = builder.firstname;
		lastname = builder.lastname;
		birthday = builder.birthday;
		grade = builder.grade;
		leavingSchool = builder.leavingSchool;
		
		gender = builder.gender;
		borrowedList = builder.borrowedList;
		profile = builder.profile;
		parent = builder.parent;
		
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
		if (!(obj instanceof Student))
			return false;
		Student other = (Student) obj;
		if (getId() != other.getId())
			return false;
		return true;
	}
}
