package com.myandb.singsong.model;

import com.myandb.singsong.R;

public enum Role {
	
	HIGH_NOTE("��������", R.drawable.img_dance),
	
	LOW_NOTE("��������", R.drawable.img_ballad),
	
	FACIAL("�󱼸���", R.drawable.img_rnb);
	
	private String title;
	private int imageResourceId;
	
	Role(String title, int imageResourceId) {
		this.title = title;
		this.imageResourceId = imageResourceId;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getImageResourceId() {
		return imageResourceId;
	}

}
