package com.myandb.singsong.image;

import android.content.Context;
import android.graphics.Bitmap.CompressFormat;

import com.myandb.singsong.file.FileManager;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class ImageLoaderConfig {
	
	public static final String CACHE_DIRECTORY = "_IMG_CACHE_";
	
	public static ImageLoaderConfiguration createDefault(Context context) {
		final int threadPoolSize = 2;
		final int memoryCacheSize = 3 * 1024 * 1024;
		final int discCacheSize = 300 * 1024 * 1024;
		final int discCacheImageWidth = 480;
		final int discCacheImageHeight = 800;
		final int discCacheImageQuality = 95;
		
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context);
		builder.threadPoolSize(threadPoolSize)
			.memoryCache(new LruMemoryCache(memoryCacheSize))
			.memoryCacheSize(memoryCacheSize)
			.discCache(new UnlimitedDiscCache(FileManager.getSubDirectory(CACHE_DIRECTORY), new Md5FileNameGenerator()))
			.discCacheSize(discCacheSize)
			.discCacheExtraOptions(discCacheImageWidth, discCacheImageHeight, CompressFormat.JPEG, discCacheImageQuality, null)
			.denyCacheImageMultipleSizesInMemory()
			.tasksProcessingOrder(QueueProcessingType.LIFO);
		return builder.build();
	}
	
}
