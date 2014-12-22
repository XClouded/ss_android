package com.myandb.singsong.image;

import java.io.File;
import java.util.Date;

import android.widget.ImageView;

import com.myandb.singsong.R;
import com.myandb.singsong.model.User;
import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.DiscCacheUtil;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.MemoryCacheUtil;

public class ImageHelper {
	
	private static final int TIME_DIFFERENCE_FROM_SERVER = 10 * 60 * 1000;
	
	private ImageHelper() {}
	
	public static void displayPhoto(User user, ImageView imageView) {
		displayPhoto(user, imageView, null);
	}
	
	public static void displayPhoto(User user, ImageView imageView, ImageLoadingListener listener) {
		ImageLoader imageLoader = ImageLoader.getInstance();
		
		if (user != null && user.hasPhoto()) {
			String url = user.getPhotoUrl();
			DiscCacheAware discCache = imageLoader.getDiscCache();
			File cached = DiscCacheUtil.findInCache(url, discCache);
			
			if (cached != null && cached.exists()) {
				Date updatedAt = user.getPhotoUpdatedAt();
				if (updatedAt != null && updatedAt.getTime() + TIME_DIFFERENCE_FROM_SERVER > cached.lastModified()) {
					DiscCacheUtil.removeFromCache(url, discCache);
					
					try {
						MemoryCacheUtil.removeFromCache(url, imageLoader.getMemoryCache());
					} catch (Exception e) {
						// unhandled exception
					}
				}
			}
			
			if (listener == null) {
				imageLoader.displayImage(url, imageView);;
			} else {
				imageLoader.displayImage(url, imageView, listener);
			}
		} else {
			imageView.setImageResource(R.drawable.user_default);
		}
	}
	
	public static void displayPhoto(String url, ImageView imageView) {
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(url, imageView);
	}
	
}
