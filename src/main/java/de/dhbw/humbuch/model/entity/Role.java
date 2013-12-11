package de.dhbw.humbuch.model.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="role")
public class Role implements de.dhbw.humbuch.model.entity.Entity {
	
	@Id
	private int id;
	private String name;
	private String description;
	
	@ManyToMany
	@JoinTable(
			name="role_has_permission",
			joinColumns={@JoinColumn(name="role_id", referencedColumnName="id")},
		    inverseJoinColumns={@JoinColumn(name="permission_id", referencedColumnName="id")}
			)
	private List<Permission> permissions = new ArrayList<Permission>();
	
	public Role() {}

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

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}
	
}
