package com.myandb.singsong.image;

import java.io.File;
import java.util.Date;

import android.widget.ImageView;

import com.myandb.singsong.R;
import com.myandb.singsong.model.User;
import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.DiscCacheUtil;
import com.nostra13.universalimageloader.core.assist.MemoryCacheUtil;

public class ImageHelper {
	
	private ImageHelper() {}
	
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
			
			imageLoader.displayImage(url, imageView);;
		} else {
			imageView.setImageResource(R.drawable.user_default);
		}
	}
	
	public static void displayPhoto(String url, ImageView imageView) {
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(url, imageView);
	}
	
}
