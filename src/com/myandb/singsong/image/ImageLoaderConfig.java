package com.myandb.singsong.image;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap.Config;

import com.myandb.singsong.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class ImageLoaderConfig {
	
	public static final String CACHE_DIRECTORY = "_IMG_CACHE_";
	
	public static ImageLoaderConfiguration createDefault(Context context) {
		final int threadPoolSize = 2;
		final int memoryCacheSize = 3 * 1024 * 1024;
		final int diskCacheSize = 500 * 1024 * 1024;
		final int fadeInDuration = 400;
		
		DisplayImageOptions.Builder optionBuilder = new DisplayImageOptions.Builder();
		optionBuilder.resetViewBeforeLoading(true)
			.showImageForEmptyUri(R.drawable.user_character)
			.showImageOnFail(R.drawable.user_character)
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
			.bitmapConfig(Config.RGB_565)
			.displayer(new NoBlinkFadeInBitmapDisplayer(fadeInDuration));
		
		File dir = context.getFilesDir();
		File cacheDir = new File(dir, CACHE_DIRECTORY);
		if (!cacheDir.exists()) {
			cacheDir.mkdir();
		}
		
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context);
		builder.threadPoolSize(threadPoolSize)
			.memoryCache(new LruMemoryCache(memoryCacheSize))
			.diskCache(new UnlimitedDiskCache(cacheDir))
			.diskCacheSize(diskCacheSize)
			.diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
			.denyCacheImageMultipleSizesInMemory()
			.tasksProcessingOrder(QueueProcessingType.LIFO)
			.defaultDisplayImageOptions(optionBuilder.build());
		
		return builder.build();
	}
	
}
