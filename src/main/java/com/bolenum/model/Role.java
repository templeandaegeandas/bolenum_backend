package com.bolenum.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 
 * @author Himanshu Kumar
 *
 */
@Entity
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank
	@Column(unique=true)
	private String name;

	@NotBlank
	private String description;

	@ManyToMany(fetch = FetchType.EAGER)
	private Set<Privilege> privileges;

	public Role() {

	}

	public Role(String name, String description, Set<Privilege> privileges) {
		this.name = name;
		this.description = description;
		this.privileges = privileges;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public Set<Privilege> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(Set<Privilege> privileges) {
		this.privileges = privileges;
	}

}
