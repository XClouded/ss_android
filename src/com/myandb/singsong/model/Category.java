package com.myandb.singsong.model;

public class Category extends Model {
	
	private String title;
	
	public Category(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return safeString(title);
	}

}
