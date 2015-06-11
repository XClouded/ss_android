package com.myandb.singsong;

public enum NotificationType {
	
	GCM_PUSH(1000),
	
	PLAY_CONTENT(2000),
			
	CONTENT_UPLOAD(3000),
	
	PHOTO_UPLOAD(4000);
	
	private int id;
	
	NotificationType(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

}
