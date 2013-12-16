package de.dhbw.humbuch.model.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="permission")
public class Permission implements de.dhbw.humbuch.model.entity.Entity {

	@Id
	private int id;
	private String name;
	private String description;
	
	public Permission() {}

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
		
		public Permission build() {
			return new Permission(this);
		}
	}
	
	private Permission(Builder builder) {
		this.name = builder.name;
		this.description = builder.description;
	}
}
