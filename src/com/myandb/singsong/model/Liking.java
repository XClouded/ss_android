package com.myandb.singsong.model;

public class Liking<T> extends Model {
	
	private T likeable;
	private User user;
	
	public T getLikeable() {
		return likeable;
	}
	
	public User getUser() {
		return user;
	}
		
}
