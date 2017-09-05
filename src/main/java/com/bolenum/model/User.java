package com.bolenum.model;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import java.sql.Timestamp;
import java.util.Date;
import org.hibernate.validator.constraints.NotEmpty;

    @Entity // This tells Hibernate to make a table out of this class
    @Table(name="UserDetails")
    public class User
    {
    	 @Id
    	 @GeneratedValue(strategy=GenerationType.AUTO)
         private Long userId;
         
    	 @NotNull
         private String firstName;
          
         private String middleName;
          
         private String lastName;
         
         @Pattern(regexp="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$",message="Incorrect email id")
         @NotNull
         @NotEmpty
         @Column(unique=true)
         private String emailId;
         
         @NotNull
         @NotEmpty
         private String password;
         
         private String address;
         
         private String city;
         
         private String state;
         
         private String country;
         
         private String mobileNumber;
         
         private String  gender;
         
         private Date dob;
         
         private Boolean isEnabled;
         
         private Boolean isDeleted;
         
         private Boolean isLocked;
         
         private Timestamp createdOn;
         
         private Timestamp updatedOn;
         
         private Timestamp deletedOn;

         @NotNull
         private Role role;
         
    	public String getEmailId() {
    		return emailId;
    	}

    	public void setEmailId(String emailId) {
    		this.emailId = emailId;
    	}

    	public String getLastName() {
    		return lastName;
    	}

    	public void setLastName(String lastName) {
    		this.lastName = lastName;
    	}

    	public String getMiddleName() {
    		return middleName;
    	}

    	public void setMiddleName(String middleName) {
    		this.middleName = middleName;
    	}

    	public String getFirstName() {
    		return firstName;
    	}

    	public void setFirstName(String firstName) {
    		this.firstName = firstName;
    	}

    	public Long getId() {
    		return userId;
    	}

    	public void setId(Long userId) {
    		this.userId = userId;
    	}

    	public String getPassword() {
    		return password;
    	}

    	public void setPassword(String password) {
    		this.password = password;
    	}

    	public String getAddress() {
    		return address;
    	}

    	public void setAddress(String address) {
    		this.address = address;
    	}

    	public Date getDob() {
    		return dob;
    	}

    	public void setDob(Date dob) {
    		this.dob = dob;
    	}

    	public String getCountry() {
    		return country;
    	}

    	public void setCountry(String country) {
    		this.country = country;
    	}

    	public String getState() {
    		return state;
    	}

    	public void setState(String state) {
    		this.state = state;
    	}

    	public String getCity() {
    		return city;
    	}

    	public void setCity(String city) {
    		this.city = city;
    	}

    	public String getMobileNumber() {
    		return mobileNumber;
    	}

    	public void setMobileNumber(String mobileNumber) {
    		this.mobileNumber = mobileNumber;
    	}

    	public boolean getLocked() {
    		return isLocked;
    	}

    	public void setLocked(Boolean isLocked) {
    		this.isLocked = isLocked;
    	}

    	public Timestamp getCreatedOn() {
    		return createdOn;
    	}

    	public void setCreatedOn(Timestamp createdOn) {
    		this.createdOn = createdOn;
    	}

    	public Timestamp getUpdatedOn() {
    		return updatedOn;
    	}

    	public void setUpdatedOn(Timestamp updatedOn) {
    		this.updatedOn = updatedOn;
    	}

    	public Timestamp getDeletedOn() {
    		return deletedOn;
    	}

    	public void setDeletedOn(Timestamp deletedOn) {
    		this.deletedOn = deletedOn;
    	}

    	public String getGender() {
    		return gender;
    	}

    	public void setGender(String gender) {
    		this.gender = gender;
    	}

    	public boolean isDeleted() {
    		return isDeleted;
    	}

    	public void setDeleted(Boolean isDeleted) {
    		this.isDeleted = isDeleted;
    	}

    	public Boolean getIsEnabled() {
    		return isEnabled;
    	}

    	public void setIsEnabled(Boolean isEnabled) {
    		this.isEnabled = isEnabled;
    	}
        
        public Role getRoles() {
             return this.role;
        }

        public void setRoles(Role role) {
        this.role = role;
    }
}
