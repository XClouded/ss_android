package com.myandb.singsong;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.myandb.singsong.file.FileManager;
import com.myandb.singsong.file.Storage;
import com.myandb.singsong.util.TimeHelper;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.Application;
import android.graphics.Bitmap.CompressFormat;

public class App extends Application {

	public static final String CACHE_DIRECTORY_NAME = "_IMG_CACHE_";
	
	public static final int NOTI_ID_PUSH = 1000;
	public static final int NOTI_ID_SONG_UPLOAD = 1001;
	public static final int NOTI_ID_PHOTO_UPLOAD = 1002;
	public static final int NOTI_ID_PLAY_SONG = 1003;
	
	public static final int REQUEST_MY_PROFILE_FRAGMENT = 2000;
	public static final int REQUEST_NOTIFICATION_ACTIVITY = 2001;
	
	private static final boolean DEBUG = true;
	
	private RequestQueue mRequestQueue;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
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
		
		Storage.initialize(this);
		TimeHelper.initialize(this);
	}
	
	public RequestQueue getQueueInstance() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(this);
		}
		
		return mRequestQueue;
	}
	
	public static boolean isDegugging() {
		return DEBUG;
	}

}
