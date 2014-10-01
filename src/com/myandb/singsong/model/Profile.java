package com.myandb.singsong.model;

public class Profile extends Model {
	
	private String kakaotalk;
	private String email;
	private String status_message;
	private int followings_num;
	private int followers_num;
	private int sing_num;
	
	public String getKakaotalk() {
		return toString(kakaotalk);
	}
	
	public void setKakaotalk(String kakaotalk) {
		this.kakaotalk = kakaotalk;
	}
	
	public String getEmail() {
		return toString(email);
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getStatusMessage() {
		return toString(status_message);
	}
	
	public void setStatusMessage(String message) {
		this.status_message = message;
	}
	
	public int getFollowingsNum() {
		return followings_num;
	}
	
	public String getWorkedFollowingsNum() {
		return toString(followings_num);
	}
	
	public int getFollowersNum() {
		return followers_num;
	}
	
	public String getWorkedFollowersNum() {
		return toString(followers_num);
	}
	
	public int getSingNum() {
		return sing_num;
	}
	
	public String getWorkedSingNum() {
		return toString(sing_num);
	}

}
