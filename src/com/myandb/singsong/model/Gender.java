package com.myandb.singsong.model;

public enum Gender {
	
	BOYS("����", "���ڳ�����"),
	
	GIRLS("����", "���ڳ�����"),
	
	MIXED("ȥ��", "���̱⸮��");
	
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
