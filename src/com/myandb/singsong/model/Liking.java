package com.myandb.singsong.model;

public class Liking<T> extends Model {
	
	private T likeable;
	
	public T getLikeable() {
		return likeable;
	}
		
}
