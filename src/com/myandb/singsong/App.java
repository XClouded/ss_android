package com.myandb.singsong;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.myandb.singsong.image.ImageLoaderConfig;
import com.myandb.singsong.secure.Auth;
import com.myandb.singsong.util.TimeHelper;
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
		
		Auth.initialize(getSharedPreferences(AUTH_PREFERENCE_FILE, Context.MODE_PRIVATE));
		
		TimeHelper.initialize(getResources());
	}
	
	public RequestQueue getQueueInstance() {
		if (requestQueue == null) {
			requestQueue = Volley.newRequestQueue(this);
		}
		return requestQueue;
	}

}
