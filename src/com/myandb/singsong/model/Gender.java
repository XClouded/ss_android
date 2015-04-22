package com.myandb.singsong.model;

public enum Gender {
	
	BOYS("巢己", "巢磊尝府府"),
	
	GIRLS("咯己", "咯磊尝府府"),
	
	MIXED("去己", "鞍捞扁府副");
	
	private String title;
	private String description;
	
	Gender(String title, String description) {
		this.title = title;
		this.description = description;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getDescription() {
		return description;
	}

}
