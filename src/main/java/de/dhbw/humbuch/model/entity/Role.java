package de.dhbw.humbuch.model.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * @author David Vitt
 *
 */
@Entity
@Table(name="role")
public class Role implements de.dhbw.humbuch.model.entity.Entity, Serializable {
	private static final long serialVersionUID = 193511645036784700L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
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
	
	/**
	 * Required by Hibernate.<p>
	 * Use the {@link Builder} instead.
	 * 
	 * @see Builder
	 */
	@Deprecated
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
		
		public Role build() {
			return new Role(this);
		}
	}
	
	private Role(Builder builder) {
		this.name = builder.name;
		this.description = builder.description;
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
		if (!(obj instanceof Role))
			return false;
		Role other = (Role) obj;
		if (getId() != other.getId())
			return false;
		return true;
	}
	
}
