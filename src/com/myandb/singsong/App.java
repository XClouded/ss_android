package com.myandb.singsong;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.myandb.singsong.image.ImageLoaderConfig;
import com.myandb.singsong.net.SelectAllRequestFilter;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.service.TokenValidationService;
import com.myandb.singsong.util.StringFormatter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

public class App extends Application {
	
	public static final ApplicationMode MODE = ApplicationMode.DEBUGGING;

	public static final int INVALID_RESOURCE_ID = 0; 
	
	public static final String FACEBOOK_NAMESPACE = "collabokaraoke_";
	
	public static final String AUTH_PREFERENCE_FILE = "_SSaS_";
	
	public static final int NOTI_ID_GCM = 1000;
	public static final int NOTI_ID_SONG_UPLOAD = 1001;
	public static final int NOTI_ID_PHOTO_UPLOAD = 1002;
	public static final int NOTI_ID_PLAY_SONG = 1003;
	
	public static String APP_VERSION = "";
	
	private RequestQueue requestQueue;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		ImageLoader.getInstance().init(ImageLoaderConfig.createDefault(this));
		
		Authenticator.initialize(getSharedPreferences(AUTH_PREFERENCE_FILE, Context.MODE_PRIVATE));
		
		StringFormatter.initialize(getResources());
		
		initializeSimpleFacebook();
		
		initializeAppVersion();
		
		startTokenValidationService();
	}
	
	private void initializeAppVersion() {
		try {
			APP_VERSION = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initializeSimpleFacebook() {
		Permission[] permissions = new Permission[] {
			Permission.PUBLIC_PROFILE,
			Permission.EMAIL,
			Permission.USER_FRIENDS,
			Permission.PUBLISH_ACTION
		};
		
		SimpleFacebookConfiguration config = new SimpleFacebookConfiguration.Builder()
			.setAppId(getString(R.string.fb_app_id))
			.setNamespace(FACEBOOK_NAMESPACE)
			.setPermissions(permissions)
			.build();
		
		SimpleFacebook.setConfiguration(config);
	}
	
	private RequestQueue getQueueInstance() {
		if (requestQueue == null) {
			requestQueue = Volley.newRequestQueue(this);
		}
		return requestQueue;
	}
	
	public <T> void addLongLivedRequest(Request<T> request) {
		if (request != null) {
			getQueueInstance().add(request);
		}
	}
	
	public <T> void addShortLivedRequest(Object taggableContext, Request<T> request) {
		if (taggableContext != null && request != null) {
			request.setTag(taggableContext.hashCode());
			getQueueInstance().add(request);
		}
	}
	
	public void cancelRequests(Object taggableContext) {
		if (taggableContext != null) {
			getQueueInstance().cancelAll(taggableContext.hashCode());
		}
	}
	
	public void cancelAllRequests() {
		getQueueInstance().cancelAll(new SelectAllRequestFilter());
	}
	
	private void startTokenValidationService() {
		Intent intent = new Intent(this, TokenValidationService.class);
		startService(intent);
	}

}
