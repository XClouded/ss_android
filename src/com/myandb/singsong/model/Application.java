package com.myandb.singsong.model;

public class Application extends Model {

	private User user;
	private Team team;
	private int state;
	
	public User getUser() {
		return user;
	}
	
	public Team getTeam() {
		return team;
	}
	
	public int getState() {
		return state;
	}
	
}
