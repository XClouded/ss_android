package com.myandb.singsong.model;

import android.content.Intent;

public class GlobalMenu {
	
	private Intent intent;
	private int titleResId;
	private int iconResId;
	private boolean loginRequired;
	
	public GlobalMenu(int titleResId, Intent intent, int iconResId) {
		this(titleResId, intent, iconResId, false);
	}
	
	public GlobalMenu(int titleResId, Intent intent, int iconResId, boolean loginRequired) {
		this.titleResId = titleResId;
		this.intent = intent;
		this.iconResId = iconResId;
		this.loginRequired = loginRequired;
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
	
	public boolean isLoginRequired() {
		return loginRequired;
	}

}
