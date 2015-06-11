package com.myandb.singsong;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.myandb.singsong.image.ImageLoaderConfig;
import com.myandb.singsong.net.SelectAllRequestFilter;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.util.StringFormatter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sromku.simple.fb.SimpleFacebook;

import android.app.Application;

public class App extends Application {
	
	public static final ServerConfig SERVER_CONFIG = ServerConfig.TEST;
	
	public static final int NOTI_ID_GCM = 1000;
	public static final int NOTI_ID_SONG_UPLOAD = 1001;
	public static final int NOTI_ID_PHOTO_UPLOAD = 1002;
	public static final int NOTI_ID_PLAY_SONG = 1003;
	
	private static String versionName;
	
	private RequestQueue requestQueue;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		initializeVersionName();
		
		Authenticator.initialize(this);
		
		ImageLoader.getInstance().init(ImageLoaderConfig.getConfig(this));
		
		StringFormatter.initialize(getResources());
		
		SimpleFacebook.setConfiguration(FacebookConfig.getConfig());
	}
	
	private void initializeVersionName() {
		try {
			versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	public static String getVersionName() {
		if (versionName == null) {
			versionName = "";
		}
		return versionName;
	}

}
