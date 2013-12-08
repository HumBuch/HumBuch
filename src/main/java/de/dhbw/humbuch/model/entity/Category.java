package de.dhbw.humbuch.model.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="category")
public class Category implements de.dhbw.humbuch.model.entity.Entity {

	@Id
	private int id;
	private String name;
	private String description;
	
	@OneToMany(mappedBy="category")
	private List<TeachingMaterial> teachingMaterials = new ArrayList<>();
	
	public Category() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public List<TeachingMaterial> getTeachingMaterials() {
		return teachingMaterials;
	}

	public void setTeachingMaterials(List<TeachingMaterial> teachingMaterials) {
		this.teachingMaterials = teachingMaterials;
	}


	
}
