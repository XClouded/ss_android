package com.myandb.singsong.model;

public class Friendship extends Model {
	
	private int allow_notify;
	private int following_id;
	
	public Friendship() {
		allow_notify = 1;
	}
	
	public boolean isAllowNotify() {
		return allow_notify == 1;
	}
	
	public void setAllowNotify(boolean allowNotify) {
		allow_notify = allowNotify ? 1 : 0;
	}
	
	public int getFollowingUserId() {
		return following_id;
	}

}
