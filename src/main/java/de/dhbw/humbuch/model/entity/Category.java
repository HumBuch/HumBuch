package de.dhbw.humbuch.model.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
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
	
	@OneToMany(mappedBy="category", fetch=FetchType.LAZY)
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

	public static class Builder {
		private final String name;
		
		private String description;
		
		public Builder(String name) {
			this.name = name;
		}
		
		public Builder description(String description) {
			this.description = description;
			return this;
		}
		
		public Category build() {
			return new Category(this);
		}
	}
	
	private Category(Builder builder) {
		this.name = builder.name;
		
		this.description = builder.description;
	}
}
