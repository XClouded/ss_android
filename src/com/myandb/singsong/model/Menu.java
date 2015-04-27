package com.myandb.singsong.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;

public class Menu {
	
	private Intent intent;
	private int titleResId;
	private int iconResId;
	private boolean loginRequired;
	private List<Menu> subMenus;
	
	public Menu(int titleResId, Intent intent, int iconResId) {
		this(titleResId, intent, iconResId, false);
	}
	
	public Menu(int titleResId, Intent intent, int iconResId, boolean loginRequired) {
		this.titleResId = titleResId;
		this.intent = intent;
		this.iconResId = iconResId;
		this.loginRequired = loginRequired;
	}
	
	public List<Menu> getSubMenus() {
		if (subMenus != null) {
			return subMenus;
		}
		return new ArrayList<Menu>();
	}
	
	public void addSubMenu(Menu subMenu) {
		if (subMenus == null) {
			subMenus = new ArrayList<Menu>();
		}
		subMenus.add(subMenu);
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
