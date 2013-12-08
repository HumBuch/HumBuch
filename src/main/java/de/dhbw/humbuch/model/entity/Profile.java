package de.dhbw.humbuch.model.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="profile")
public class Profile implements de.dhbw.humbuch.model.entity.Entity {

	@Id
	private int id;
	
//	@OneToOne
//	@JoinColumn(name="student_Id")
//	private Student student;
	
	private boolean english;
	private boolean latin;
	private boolean french;
	
	@Enumerated(EnumType.STRING)
	private Religion religion;
	
	public Profile() {}

	public boolean isEnglish() {
		return english;
	}

	public void setEnglish(boolean english) {
		this.english = english;
	}

	public boolean isLatin() {
		return latin;
	}

	public void setLatin(boolean latin) {
		this.latin = latin;
	}

	public boolean isFrench() {
		return french;
	}

	public void setFrench(boolean french) {
		this.french = french;
	}

	public Religion getReligion() {
		return religion;
	}

	public void setReligion(Religion religion) {
		this.religion = religion;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	

}
