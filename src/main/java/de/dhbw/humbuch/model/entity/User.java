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
@Table(name="user")
public class User implements de.dhbw.humbuch.model.entity.Entity {
	
	@Id
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

}
