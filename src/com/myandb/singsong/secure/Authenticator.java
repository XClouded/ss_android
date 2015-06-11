package com.myandb.singsong.secure;

import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.facebook.Session;
import com.myandb.singsong.GCMIntentService;
import com.myandb.singsong.model.Profile;
import com.myandb.singsong.model.User;
import com.myandb.singsong.service.TokenValidationService;
import com.myandb.singsong.util.GsonUtils;
import com.myandb.singsong.util.Utils;

public class Authenticator {
	
	public static final String FILE_NAME = "_AUTHENTICATION_";
	public static final int FILE_MODE = Context.MODE_PRIVATE;
	
	private static final String KEY_USER = "_USER_";
	private static final String KEY_ACCESS_TOKEN = "_ACCESS_TOKEN_";
	private static final String KEY_DEVICE_UUID = "_UUID_";
	
	private static SharedPreferences preferences;
	
	public static void initialize(Context context) {
		preferences = context.getSharedPreferences(FILE_NAME, FILE_MODE);
		
		initializeDeviceUuid();
		
		startTokenValidationService(context);
	}
	
	private static void initializeDeviceUuid() {
		if (!preferences.contains(KEY_DEVICE_UUID)) {
			String uuid = UUID.randomUUID().toString();
			putString(KEY_DEVICE_UUID, uuid);
		}
	}
	
	private static void startTokenValidationService(Context context) {
		Intent service = new Intent(context, TokenValidationService.class);
		context.startService(service);
	}

	public void login(Context context, User user, String token) {
		preferences.edit()
			.putString(KEY_USER, user.toString())
			.putString(KEY_ACCESS_TOKEN, token)
			.commit();
		
		if (context != null) {
			GCMIntentService.register(context);
		}
	}
	
	public void logout(Context context) {
		if (context != null) {
			GCMIntentService.unregister(context);
		}
		
		preferences.edit().clear().commit();
		
		closeAndClearFacebookSession();
	}
	
	private void closeAndClearFacebookSession() {
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
		return GsonUtils.fromJson(getUserInJson(), User.class);
	}
	
	public static String getUserInJson() {
		return getString(KEY_USER);
	}
	
	public static String getAccessToken() {
		return getString(KEY_ACCESS_TOKEN);
	}
	
	public static String getDeviceUuid() {
		return getString(KEY_DEVICE_UUID);
	}
	
	private static void putString(String key, String value) {
		preferences.edit().putString(key, value).commit();
	}
	
	private static String getString(String key) {
		try {
			return preferences.getString(key, Utils.EMPTY);
		} catch (ClassCastException e) {
			e.printStackTrace();
			return Utils.EMPTY;
		}
	}
	
}
