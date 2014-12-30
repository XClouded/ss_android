package com.myandb.singsong.model;

public class Category extends Model {
	
	private String title;
	private int imageResourceId;
	
	public Category(String title) {
		this.title = title;
	}
	
	public Category(String title, int imageResId) {
		this.title = title;
		this.imageResourceId = imageResId;
	}
	
	public String getTitle() {
		return safeString(title);
	}
	
	public int getImageResourceId() {
		return imageResourceId;
	}

}
