package com.myandb.singsong.model;

public class Member extends Model {

	private User user;
	private String role;
	private String description;
	
	public User getUser() {
		return user;
	}
	
	public String getRole() {
		return safeString(role);
	}
	
	public String getDescription() {
		return safeString(description);
	}
	
}
