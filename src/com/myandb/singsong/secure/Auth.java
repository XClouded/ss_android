package com.myandb.singsong.secure;

import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.myandb.singsong.file.Storage;
import com.myandb.singsong.model.User;
import com.myandb.singsong.util.Utility;

public class Auth {
	
	private static final String KEY_USER = "_useru_";
	private static final String KEY_ACCESS_TOKEN = "_token_";

	public void login(User user, String token) {
		Gson gson = Utility.getGsonInstance();
		Editor editor = Storage.getInstance().edit();
		editor.putString(KEY_USER, gson.toJson(user, User.class))
			.putString(KEY_ACCESS_TOKEN, token)
			.commit();
	}
	
	public void logout() {
		Storage.getInstance().edit().clear().commit();
	}
	
	public void update(User user) {
		if (isLoggedIn()) {
			User currentUser = getUser();
			if (currentUser.getId() == user.getId()) {
				Gson gson = Utility.getGsonInstance();
				Editor editor = Storage.getInstance().edit();
				editor.putString(KEY_USER, gson.toJson(user, User.class)).commit();
			}
		}
	}
	
	public void update(String token) {
		if (isLoggedIn()) {
			Editor editor = Storage.getInstance().edit();
			editor.putString(KEY_ACCESS_TOKEN, token).commit();
		}
	}
	
	public static boolean isLoggedIn() {
		return Storage.getInstance().contains(KEY_USER)
				&& Storage.getInstance().contains(KEY_ACCESS_TOKEN);
	}
	
	public static User getUser() {
		String userInJson = getUserInJson();
		if (!userInJson.equals("")) {
			return Utility.getGsonInstance().fromJson(getUserInJson(), User.class);
		} else {
			return null;
		}
	}
	
	public static String getUserInJson() {
		if (isLoggedIn()) {
			try {
				return Storage.getInstance().getString(KEY_USER, "");
			} catch (ClassCastException e) {
				return "";
			}
		}
		
		return "";
	}
	
	public static String getAccessToken() {
		try {
			return Storage.getInstance().getString(KEY_ACCESS_TOKEN, "");
		} catch (ClassCastException e) {
			return "";
		}
	}
	
}
