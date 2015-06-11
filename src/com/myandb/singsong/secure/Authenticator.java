package com.myandb.singsong.secure;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.Session;
import com.myandb.singsong.model.Model;
import com.myandb.singsong.model.Profile;
import com.myandb.singsong.model.User;

public class Authenticator {
	
	public static final String FILE_NAME = "_AUTHENTICATION_";
	
	public static final int LOGIN_TYPE_PASSWORD = 1;
	public static final int LOGIN_TYPE_PASSWORD_EASY_LOGIN = 2;
	public static final int LOGIN_TYPE_TOKEN_APP_RESTART = 3;
	public static final int LOGIN_TYPE_TOKEN = 4;
	
	public static final int SINGSONG_LOGIN_TYPE_PASSWORD = 1;
	public static final int SINGSONG_LOGIN_TYPE_FACEBOOK = 2;
	
	public static final int LOGIN_PURPOSE_LOGIN = 1;
	public static final int LOGIN_PURPOSE_INTEGRATE = 2;
	
	private static final String KEY_USER = "_USER_";
	private static final String KEY_ACCESS_TOKEN = "_ACCESS_TOKEN_";
	private static final String KEY_DEVICE_UUID = "_UUID_";
	
	private static SharedPreferences preferences;
	
	public static void initialize(Context context) {
		preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		
		initializeDeviceUuid();
	}
	
	private static void initializeDeviceUuid() {
		if (!preferences.contains(KEY_DEVICE_UUID)) {
			String uuid = UUID.randomUUID().toString();
			putString(KEY_DEVICE_UUID, uuid);
		}
	}
	
	public static String getDeviceUuid() {
		return preferences.getString(KEY_DEVICE_UUID, StringUtils.EMPTY);
	}

	public void login(User user, String token) {
		preferences.edit()
			.putString(KEY_USER, user.toString())
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
				putString(KEY_USER, user.toString());
			}
		}
	}
	
	public void update(String token) {
		if (isLoggedIn()) {
			putString(KEY_ACCESS_TOKEN, token);
		}
	}
	
	public static boolean isLoggedIn() {
		return preferences.contains(KEY_USER) && preferences.contains(KEY_ACCESS_TOKEN);
	}
	
	public static User getUser() {
		return Model.fromJson(getUserInJson(), User.class);
	}
	
	public static String getUserInJson() {
		try {
			return preferences.getString(KEY_USER, StringUtils.EMPTY);
		} catch (ClassCastException e) {
			return StringUtils.EMPTY;
		}
	}
	
	public static String getAccessToken() {
		try {
			return preferences.getString(KEY_ACCESS_TOKEN, StringUtils.EMPTY);
		} catch (ClassCastException e) {
			return StringUtils.EMPTY;
		}
	}
	
	private static void putString(String key, String value) {
		preferences.edit().putString(key, value).commit();
	}
	
}
