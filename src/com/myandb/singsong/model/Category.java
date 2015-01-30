package com.myandb.singsong.model;

import com.myandb.singsong.R;

public class Category extends Model {
	
	private String title;
	private int imageResourceId;
	
	public Category(int id) {
		this.id = id;
		switch (id) {
		case 0:
			this.title = "��ü";
			this.imageResourceId = R.drawable.img_ballad;
			break;
		
		case 1:
			this.title = "�߶��";
			this.imageResourceId = R.drawable.img_ballad;
			break;
			
		case 2:
			this.title = "��";
			this.imageResourceId = R.drawable.img_dance;
			break;
			
		case 3:
			this.title = "��/����";
			this.imageResourceId = R.drawable.img_hiphop;
			break;
		
		case 4:
			this.title = "R&B/Soul";
			this.imageResourceId = R.drawable.img_rnb;
			break;
			
		case 5:
			this.title = "��";
			this.imageResourceId = R.drawable.img_rock;
			break;
			
		case 6:
			this.title = "OST";
			this.imageResourceId = R.drawable.img_ost;
			break;
			
		case 7:
			this.title = "Ʈ��Ʈ";
			this.imageResourceId = R.drawable.img_trot;
			break;
			
		case 8:
			this.title = "��ũ";
			this.imageResourceId = R.drawable.img_folk;
			break;
			
		case 9:
			this.title = "�ε�����";
			this.imageResourceId = R.drawable.img_indie;
			break;

		default:
			this.title = "�߶��";
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

}
