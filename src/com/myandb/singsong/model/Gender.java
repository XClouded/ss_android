package com.myandb.singsong.model;

public enum Gender {
	
	BOYS("巢己"),
	
	GIRLS("咯己"),
	
	MIXED("去己"),
	
	NULL;
	
	private String title;
	
	Gender() {}
	
	Gender(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}

}
