package de.dhbw.humbuch.model.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="student")
public class Student implements de.dhbw.humbuch.model.entity.Entity {
	
	@Id
	private int id;
	
	@ManyToOne
	@JoinColumn(name="gradeId", referencedColumnName="id")
	private Grade grade;
	private String lastname;
	private String firstname;
	private Date birthday;
	private String gender;
	
	@OneToMany(mappedBy="student")
	private List<BorrowedMaterial> borrowedList = new ArrayList<>();
	
	@OneToOne
	@JoinColumn(name="profileId")
	private Profile profile;
	
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

	public List<BorrowedMaterial> getBorrowedList() {
		return borrowedList;
	}

	public void setBorrowedList(List<BorrowedMaterial> borrowedList) {
		this.borrowedList = borrowedList;
	}

	public Profile getProfil() {
		return profile;
	}

	public void setProfil(Profile profil) {
		this.profile = profil;
	}
	
	
}
