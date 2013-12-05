package de.dhbw.humbuch.model.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="subject")
public class Subject implements de.dhbw.humbuch.model.entity.Entity {
	
	@Id
	private int id;
	
	@OneToMany(mappedBy="subject")
	private List<TeachingMaterial> teachingMaterials;
	
	private String name;
	private String description;
	private String subjectMaster;
	
	public Subject() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<TeachingMaterial> getTeachingMaterials() {
		return teachingMaterials;
	}

	public void setTeachingMaterials(List<TeachingMaterial> teachingMaterials) {
		this.teachingMaterials = teachingMaterials;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSubjectMaster() {
		return subjectMaster;
	}

	public void setSubjectMaster(String subjectMaster) {
		this.subjectMaster = subjectMaster;
	}

	
}
