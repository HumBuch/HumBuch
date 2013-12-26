package de.dhbw.humbuch.model.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="grade")
public class Grade implements de.dhbw.humbuch.model.entity.Entity {

	@Id
	private int id;
	
	private int grade;
	
	@Column(name="suffix")
	private String suffix;
	
	private String teacher;
	
	@OneToMany(mappedBy="grade")
	private List<Student> students = new ArrayList<>();

	public Grade() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}
	
	public static class Builder {
		private final int grade;
		private final String suffix;
		
		private String teacher;
		
		public Builder(String gradeString){
			String[] splittedString = splitBetweenCharsAndDigits(gradeString);
			this.grade = Integer.parseInt(splittedString[0]);
			this.suffix = splittedString[1];
		}
		
		public Builder(int grade, String suffix) {
			this.grade = grade;
			this.suffix = suffix;
		}
		
		public Builder teacher(String teacher) {
			this.teacher = teacher;
			return this;
		}
		
		public Grade build() {
			return new Grade(this);
		}
		
		private static String[] splitBetweenCharsAndDigits(String str){	
			return str.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
		}
	}
	
	private Grade(Builder builder) {
		grade = builder.grade;
		suffix = builder.suffix;
		
		teacher = builder.teacher;
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
		if (getClass() != obj.getClass())
			return false;
		Grade other = (Grade) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}
