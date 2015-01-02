package com.myandb.singsong.model;

public class Category extends Model {
	
	private String title;
	private int imageResourceId;
	
	public Category(int id, String title) {
		this.id = id;
		this.title = title;
	}
	
	public Category(int id, String title, int imageResId) {
		this.id = id;
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
