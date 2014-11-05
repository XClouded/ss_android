package com.myandb.singsong.model;

import android.content.Intent;

public class GlobalMenu {
	
	private int titleResId;
	private Intent intent;
	private int iconResId;
	
	public GlobalMenu(int titleResId, Intent intent) {
		this.titleResId = titleResId;
		this.intent = intent;
	}
	
	public GlobalMenu(int titleResId, Intent intent, int iconResId) {
		this(titleResId, intent);
		this.iconResId = iconResId;
	}
	
	public int getTitleResId() {
		return titleResId;
	}
	
	public Intent getIntent() {
		return intent;
	}
	
	public int getIconResId() {
		return iconResId;
	}

}
