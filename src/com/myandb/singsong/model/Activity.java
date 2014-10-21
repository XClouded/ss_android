package com.myandb.singsong.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Activity extends Model {
	
	public static final int TYPE_UPDATE_NICKNAME = 1;
	public static final int TYPE_UPDATE_PHOTO = 2;
	public static final int TYPE_UPDATE_STATUS_MESSAGE = 3;
	public static final int TYPE_CREATE_FRIENDSHIP = 4;
	public static final int TYPE_CREATE_ROOT_SONG = 5;
	public static final int TYPE_CREATE_LEAF_SONG = 6;
	public static final int TYPE_CREATE_COMMENT = 7;
	public static final int TYPE_CREATE_LIKING = 8;
	public static final int TYPE_RECOMMEND_ARTIST = 9;
	
	public static final int TYPE_ADMIN_MESSAGE = 255;

	private int source_id;
	private int source_type;
	private int user_id;
	private int parent_id;
	private User user;
	private String metadata;
	
	public int getSourceType() {
		return source_type;
	}
	
	public int getSourceId() {
		return source_id;
	}
	
	public int getUserId() {
		return user_id;
	}
	
	public int getParentId() {
		return parent_id;
	}
	
	public User getCreator() {
		return user;
	}
	
	public JSONObject getMetadata() {
		if ("".equals(metadata)) {
			return new JSONObject();
		} else {
			try {
				return new JSONObject(metadata);
			} catch (JSONException e) {
				e.printStackTrace();
				return new JSONObject();
			}
		}
	}
	
}
