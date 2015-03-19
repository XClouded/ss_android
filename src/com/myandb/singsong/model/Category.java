package com.myandb.singsong.model;

import com.myandb.singsong.R;

public class Category extends Model {
	
	private String title;
	private int imageResourceId;
	
	public Category(int id) {
		this.id = id;
		switch (id) {
		case 0:
			this.title = "ÀüÃ¼";
			this.imageResourceId = R.drawable.img_all;
			break;
		
		case 1:
			this.title = "¹ß¶óµå";
			this.imageResourceId = R.drawable.img_ballad;
			break;
			
		case 2:
			this.title = "´í½º";
			this.imageResourceId = R.drawable.img_dance;
			break;
			
		case 3:
			this.title = "·¦/ÈüÇÕ";
			this.imageResourceId = R.drawable.img_hiphop;
			break;
		
		case 4:
			this.title = "R&B/Soul";
			this.imageResourceId = R.drawable.img_rnb;
			break;
			
		case 5:
			this.title = "·Ï";
			this.imageResourceId = R.drawable.img_rock;
			break;
			
		case 6:
			this.title = "OST";
			this.imageResourceId = R.drawable.img_ost;
			break;
			
		case 7:
			this.title = "Æ®·ÎÆ®";
			this.imageResourceId = R.drawable.img_trot;
			break;
			
		case 8:
			this.title = "Æ÷Å©";
			this.imageResourceId = R.drawable.img_folk;
			break;
			
		case 9:
			this.title = "ÀÎµğÀ½¾Ç";
			this.imageResourceId = R.drawable.img_indie;
			break;

		default:
			this.title = "¹ß¶óµå";
			this.imageResourceId = R.drawable.img_ballad;
			break;
		}
	}
	
	public String getTitle() {
		return safeString(title);
	}
	
	public int getImageResourceId() {
		return imageResourceId;
	}
	
	public boolean canRepresentTeam() {
		return id > 0 && id < 10;
	}

}
