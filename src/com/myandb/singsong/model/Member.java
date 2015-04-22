package com.myandb.singsong.model;

public class Member extends Model {

	private User user;
	private String rolePrefix;
	private String role;
	
	public User getUser() {
		return user;
	}
	
	public Role getRole() {
		return Role.valueOf(role);
	}
	
	public String getRolePrefix() {
		return safeString(rolePrefix);
	}
	
}
