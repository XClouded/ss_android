package com.myandb.singsong.util;

import java.io.File;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.myandb.singsong.R;
import com.myandb.singsong.image.BitmapBuilder;
import com.myandb.singsong.model.User;
import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.DiscCacheUtil;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.assist.MemoryCacheUtil;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class ImageHelper {
	
	public static void displayPhoto(User user, ImageView imageView) {
		ImageLoader imageLoader = ImageLoader.getInstance();
		
		if (user != null && user.hasPhoto()) {
			String url = user.getPhotoUrl();
			DiscCacheAware discCache = imageLoader.getDiscCache();
			File cached = DiscCacheUtil.findInCache(url, discCache);
			
			if (cached != null && cached.exists()) {
				Date updatedAt = user.getPhotoUpdatedAt();
				if (updatedAt != null && updatedAt.getTime() > cached.lastModified()) {
					DiscCacheUtil.removeFromCache(url, discCache);
					
					try {
						MemoryCacheUtil.removeFromCache(url, imageLoader.getMemoryCache());
					} catch (Exception e) {
						// unhandled exception
					}
				}
			}
			
			ImageHelper.displayPhoto(url, imageView);
		} else {
			imageView.setImageResource(R.drawable.user_default);
		}
	}
	
	public static void displayPhoto(String url, ImageView imageView) {
		ImageLoader imageLoader = ImageLoader.getInstance();
		
		imageLoader.displayImage(url, imageView, displayImageOptions);
	}
	private static DisplayImageOptions.Builder optionBuilder = new DisplayImageOptions.Builder()
		.resetViewBeforeLoading(true)
		.showImageForEmptyUri(R.drawable.user_default)
		.showImageOnFail(R.drawable.user_default)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		.bitmapConfig(Config.RGB_565);
	
	private static DisplayImageOptions displayImageOptions = optionBuilder.displayer(new NoBlinkFadeInBitmapDisplayer(400)).build();
	
	private static class NoBlinkFadeInBitmapDisplayer extends FadeInBitmapDisplayer {

		public NoBlinkFadeInBitmapDisplayer(int durationMillis) {
			super(durationMillis);
		}

		@Override
		public Bitmap display(Bitmap bitmap, ImageView imageView, LoadedFrom loadedFrom) {
			if (loadedFrom == LoadedFrom.MEMORY_CACHE || loadedFrom == LoadedFrom.DISC_CACHE) {
				imageView.setImageBitmap(bitmap);
				return bitmap;
	        } else {
	        	return super.display(bitmap, imageView, loadedFrom);
	        }
		}

	}
	
}
