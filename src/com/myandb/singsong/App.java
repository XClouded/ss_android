package com.myandb.singsong;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.myandb.singsong.image.ImageLoaderConfig;
import com.myandb.singsong.net.SelectAllRequestFilter;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.util.StringFormatter;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Application;
import android.content.Context;

public class App extends Application {

	public static final boolean TESTING = false;
	
	public static final String AUTH_PREFERENCE_FILE = "_SSaS_";
	
	public static final int NOTI_ID_GCM = 1000;
	public static final int NOTI_ID_SONG_UPLOAD = 1001;
	public static final int NOTI_ID_PHOTO_UPLOAD = 1002;
	public static final int NOTI_ID_PLAY_SONG = 1003;
	
	private RequestQueue requestQueue;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		ImageLoader.getInstance().init(ImageLoaderConfig.createDefault(this));
		
		Authenticator.initialize(getSharedPreferences(AUTH_PREFERENCE_FILE, Context.MODE_PRIVATE));
		
		StringFormatter.initialize(getResources());
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
	
	public <T> void addShortLivedRequest(Object cancelable, Request<T> request) {
		if (cancelable != null && request != null) {
			request.setTag(cancelable.hashCode());
			getQueueInstance().add(request);
		}
	}
	
	public void cancelRequests(Object cancelable) {
		if (cancelable != null) {
			getQueueInstance().cancelAll(cancelable.hashCode());
		}
	}
	
	public void cancelAllRequests() {
		getQueueInstance().cancelAll(new SelectAllRequestFilter());
	}

}
