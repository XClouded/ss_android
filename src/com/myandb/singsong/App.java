package com.myandb.singsong;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.myandb.singsong.file.FileManager;
import com.myandb.singsong.secure.Auth;
import com.myandb.singsong.util.TimeHelper;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap.CompressFormat;

public class App extends Application {

	public static final boolean TESTING = false;
	
	public static final String CACHE_DIRECTORY_NAME = "_IMG_CACHE_";
	public static final String AUTH_PREFERENCE_FILE = "_SSaS_";
	
	public static final int NOTI_ID_GCM = 1000;
	public static final int NOTI_ID_SONG_UPLOAD = 1001;
	public static final int NOTI_ID_PHOTO_UPLOAD = 1002;
	public static final int NOTI_ID_PLAY_SONG = 1003;
	
	private RequestQueue requestQueue;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		initializeImageLoader();
		
		initializeUtilComponents();
	}
	
	private void initializeImageLoader() {
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(this);
		builder.threadPoolSize(2)
			.memoryCache(new LruMemoryCache(3 * 1024 * 1024))
			.memoryCacheSize(3 * 1024 * 1024)
			.discCache(new UnlimitedDiscCache(FileManager.getSubDirectory(CACHE_DIRECTORY_NAME), new Md5FileNameGenerator()))
			.discCacheSize(300 * 1024 * 1024)
			.discCacheExtraOptions(480, 800, CompressFormat.JPEG, 95, null)
			.denyCacheImageMultipleSizesInMemory()
			.tasksProcessingOrder(QueueProcessingType.LIFO);
		
		ImageLoader.getInstance().init(builder.build());
	}
	
	private void initializeUtilComponents() {
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
