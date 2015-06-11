package com.myandb.singsong.secure;

import java.util.UUID;

import android.content.SharedPreferences;

import com.facebook.Session;
import com.google.gson.Gson;
import com.myandb.singsong.model.Profile;
import com.myandb.singsong.model.User;
import com.myandb.singsong.util.Utility;

public class Authenticator {
	
	public static final int LOGIN_TYPE_PASSWORD = 1;
	public static final int LOGIN_TYPE_PASSWORD_EASY_LOGIN = 2;
	public static final int LOGIN_TYPE_TOKEN_APP_RESTART = 3;
	public static final int LOGIN_TYPE_TOKEN = 4;
	
	public static final int SINGSONG_LOGIN_TYPE_PASSWORD = 1;
	public static final int SINGSONG_LOGIN_TYPE_FACEBOOK = 2;
	
	public static final int LOGIN_PURPOSE_LOGIN = 1;
	public static final int LOGIN_PURPOSE_INTEGRATE = 2;
	
	private static final String KEY_USER = "_useru_";
	private static final String KEY_ACCESS_TOKEN = "_token_";
	private static final String KEY_DEVICE_UUID = "_uuid_";
	
	private static SharedPreferences preferences;

	public void login(User user, String token) {
		Gson gson = Utility.getGsonInstance();
		preferences.edit()
			.putString(KEY_USER, gson.toJson(user, User.class))
			.putString(KEY_ACCESS_TOKEN, token)
			.commit();
	}
	
	public void logout() {
		preferences.edit().clear().commit();
		logoutFacebookSession();
	}
	
	private void logoutFacebookSession() {
		Session session = Session.getActiveSession();
		if (session != null) {
			session.closeAndClearTokenInformation();
		}
	}
	
	public void update(Profile profile) {
		if (isLoggedIn()) {
			User currentUser = getUser();
			currentUser.setProfile(profile);
			update(currentUser);
		}
	}
	
	public void update(User user) {
		if (isLoggedIn()) {
			User currentUser = getUser();
			if (currentUser.getId() == user.getId()) {
				if (user.getProfile() == null) {
					Profile profile = currentUser.getProfile();
					user.setProfile(profile);
				}
				Gson gson = Utility.getGsonInstance();
				preferences.edit().putString(KEY_USER, gson.toJson(user, User.class)).commit();
			}
		}
	}
	
	public void update(String token) {
		if (isLoggedIn()) {
			preferences.edit().putString(KEY_ACCESS_TOKEN, token).commit();
		}
	}
	
	public static void initialize(SharedPreferences preferences) {
		Authenticator.preferences = preferences;
		
		initializeUuid();
	}
	
	private static void initializeUuid() {
		if (preferences != null) {
			if (!preferences.contains(KEY_DEVICE_UUID)) {
				preferences.edit().putString(KEY_DEVICE_UUID, UUID.randomUUID().toString()).commit();
			}
		}
	}
	
	public static String getDeviceUuid() {
		return preferences.getString(KEY_DEVICE_UUID, "");
	}
	
	public static boolean isLoggedIn() {
		return preferences.contains(KEY_USER) && preferences.contains(KEY_ACCESS_TOKEN);
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
				return preferences.getString(KEY_USER, "");
			} catch (ClassCastException e) {
				return "";
			}
		}
		
		return "";
	}
	
	public static String getAccessToken() {
		try {
			return preferences.getString(KEY_ACCESS_TOKEN, "");
		} catch (ClassCastException e) {
			return "";
		}
	}
	
}
