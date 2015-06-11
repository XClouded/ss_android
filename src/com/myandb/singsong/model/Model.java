package com.myandb.singsong.model;

import java.util.Date;

import com.myandb.singsong.util.GsonUtils;
import com.myandb.singsong.util.Utils;

public abstract class Model {
	
	public static final String STORAGE_HOST = "https://ssproxy.ucloudbiz.olleh.com/v1/AUTH_ddf209b4-06d2-4076-a550-1cd504b084a2/";
	public static final String STORAGE_ALBUM = "album_photo/";
	public static final String STORAGE_MUSEONLINE_LRC = "lrc/";
	public static final String STORAGE_NXING_LRC = "nxing_lrc/";
	public static final String STORAGE_MUSIC = "nxing_music/";
	public static final String STORAGE_SONG = "song/";
	public static final String STORAGE_USER = "user_photo/";
	public static final String STORAGE_IMAGE = "image/";
	public static final String SUFFIX_JPG = ".jpg";
	public static final String SUFFIX_OGG = ".ogg";
	public static final String SUFFIX_LRC = ".lrc";
	
	protected int id;
	protected Date created_at;
	
	public final int getId() {
		return id;
	}
	
	public final Date getCreatedTime() {
		return created_at;
	}
	
	public String getWorkedCreatedTime(Date currentDate) {
		return Utils.getTimeLag(currentDate, created_at);
	}
	
	public static final String safeString(String string) {
		return string != null ? string : "";
	}
	
	public static final String safeString(int num) {
		String string = "";
		
		if (num < 1000) {
			string += String.valueOf(num);
		} else if (num < 1000000) {
			float floatNum = num / 1000f;
			
			string += String.format("%.1f", floatNum);
			string += "K"; 
		} else {
			float floatNum = num / 1000000f;
			
			string += String.format("%.1f", floatNum);
			string += "M";
		}
		
		return string;
	}

	@Override
	public String toString() {
		return GsonUtils.getGsonInstance().toJson(this, getClass());
	}

}
