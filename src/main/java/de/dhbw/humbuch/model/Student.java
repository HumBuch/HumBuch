package de.dhbw.humbuch.model;

import java.util.ArrayList;
import java.util.Date;

public final class Student {

	private String[] foreignLanguages;
	private String birthDay;
	private String gender;
	private int id;
	private String tutorGroup;
	private String lastName;
	private String firstName;
	private String fullName;
	private String place;
	private int postalCode;
	private String streetName;
	private String profile;
	private ArrayList rentalList;

	
	public Student(String[] foreignLanguages, String birthDay,
			String sex, int id, String tutorGroup, String lastName,
			String firstName, String place, int postalCode, String streetName,
			String profile) {
		super();
		this.foreignLanguages = foreignLanguages;
		this.birthDay = birthDay;
		this.gender = sex;
		this.id = id;
		this.tutorGroup = tutorGroup;
		this.lastName = lastName;
		this.firstName = firstName;
		this.fullName = firstName + " " + lastName;
		this.place = place;
		this.postalCode = postalCode;
		this.streetName = streetName;
		this.profile = profile;
	}
	
	public String[] getForeignLanguages() {
		return foreignLanguages;
	}
	public void setForeignLanguages(String[] foreignLanguages) {
		this.foreignLanguages = foreignLanguages;
	}
	public String getBirthDay() {
		return birthDay;
	}
	public void setBirthDay(String birthDay) {
		this.birthDay = birthDay;
	}
	public String getSex() {
		return gender;
	}
	public void setSex(String sex) {
		this.gender = sex;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTutorGroup() {
		return tutorGroup;
	}
	public void setTutorGroup(String tutorGroup) {
		this.tutorGroup = tutorGroup;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public int getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(int postalCode) {
		this.postalCode = postalCode;
	}
	public String getStreetName() {
		return streetName;
	}
	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}	
	public String getFullName() {
		return fullName;
	}	
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public ArrayList getRentalList() {
		return rentalList;
	}	
	public void setRentalList(ArrayList rentalList) {
		this.rentalList = rentalList;
	}
}
