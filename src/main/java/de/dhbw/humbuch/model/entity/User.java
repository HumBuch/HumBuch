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
@Table(name="user")
public class User implements de.dhbw.humbuch.model.entity.Entity, Serializable {
	private static final long serialVersionUID = 12872765838454735L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String username;
	private String password;
	private String email;
	
	@ManyToMany
	@JoinTable(
			name="user_has_role",
			joinColumns={@JoinColumn(name="user_id", referencedColumnName="id")},
		    inverseJoinColumns={@JoinColumn(name="role_id", referencedColumnName="id")}
			)
	private List<Role> roles = new ArrayList<Role>();
	
	/**
	 * Required by Hibernate.<p>
	 * Use the {@link Builder} instead.
	 * 
	 * @see Builder
	 */
	@Deprecated
	public User() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public static class Builder {
		private final String username;
		private final String password;
		
		private String email;
		
		public Builder(String username, String password) {
			this.username = username;
			this.password = password;
		}
		
		public Builder email(String email) {
			this.email = email;
			return this;
		}
		
		public User build() {
			return new User(this);
		}
	}
	
	private User(Builder builder) {
		this.username = builder.username;
		this.password = builder.password;
		
		this.email = builder.email;
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
		if (!(obj instanceof User))
			return false;
		User other = (User) obj;
		if (getId() != other.getId())
			return false;
		return true;
	}
}
