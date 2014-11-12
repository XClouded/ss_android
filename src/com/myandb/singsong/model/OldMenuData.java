package com.myandb.singsong.model;

import android.graphics.Bitmap;

public class OldMenuData {
	
	public enum PageName { MY_PAGE, NOTIFICATION, WORLD_SONG, MUSIC_LIST,WAITING_COLLABO, LEGEND, FIND_USER, NOTICE, ARTIST, SETTING }
	
	public static final int FRAGMENT = 1;
	public static final int ACTIVITY = 2;
	
	private String outText;
	private Bitmap icon;
	private PageName pageName;
	private int pageType;
	
	public OldMenuData(String out, Bitmap icon, PageName pageName) {
		this.outText = out;
		this.icon = icon;
		this.pageName = pageName;
		
		switch (pageName) {
		case MY_PAGE:
		case WAITING_COLLABO:
		case WORLD_SONG:
		case MUSIC_LIST:
		case LEGEND:
			pageType = FRAGMENT;
			break;
			
		case NOTIFICATION:
		case FIND_USER:
		case ARTIST:
		case NOTICE:
		case SETTING:
			pageType = ACTIVITY;
			break;
		}
	}
	
	public void setOutText(String outText) {
		this.outText = outText;
	}
	
	public String getOutText() {
		return outText;
	}
	
	public PageName getPageName() {
		return pageName;
	}
	
	public int getPageType() {
		return pageType;
	}
	
	public Bitmap getIcon() {
		return icon;
	}
	
}
