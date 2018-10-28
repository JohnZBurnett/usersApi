package com.restful.users.domain;

public class User {
	private String firstName;
	private String lastName;
	private int id;
	
	public User() {
		
	}
	
	public User(int id, String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.id = id; 
	}
	
	public int getId() {
		return id; 
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	public void setLastName( String lastName) {
		this.lastName = lastName; 
	}
	
	
}
