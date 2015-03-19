package com.myandb.singsong.model;

public enum Gender {
	
	BOYS("����"),
	
	GIRLS("����"),
	
	MIXED("ȥ��"),
	
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
