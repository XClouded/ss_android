package com.myandb.singsong.model;

public class Invitation extends Model {
	
	private User inviter;
	private User invitee;
	private Team team;
	private int state;
	
	public User getInviter() {
		return inviter;
	}
	
	public User getInvitee() {
		return invitee;
	}
	
	public Team getTeam() {
		return team;
	}
	
	public int getState() {
		return state;
	}

}
