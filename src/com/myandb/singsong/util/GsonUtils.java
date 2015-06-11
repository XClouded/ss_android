package com.myandb.singsong.util;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtils {
	
	private static Gson gson;
	
	private GsonUtils() {}
	
	public static Gson getGsonInstance() {
		if (gson == null) {
			gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		}
		
		return gson;
	}
	
	public static final String toJson(Object src) {
		return getGsonInstance().toJson(src);
	}
	
	public static final <T> T fromJson(JSONObject json, Class<T> clazz) {
		if (json != null) {
			return fromJson(json.toString(), clazz);
		}
		return null;
	}
	
	public static final <T> T fromJson(String json, Class<T> clazz) {
		if (!Utils.EMPTY.equals(json)) {
			return getGsonInstance().fromJson(json, clazz);
		}
		return null;
	}

}
