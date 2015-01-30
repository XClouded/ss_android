package com.myandb.singsong.image;

import java.io.File;
import java.util.Date;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.myandb.singsong.R;
import com.myandb.singsong.model.User;
import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

public class ImageHelper {
	
	private static final int TIME_DIFFERENCE_FROM_SERVER = 2 * 60 * 1000;
	
	private ImageHelper() {}
	
	public static void displayPhoto(User user, ImageView imageView) {
		displayPhoto(user, imageView, null);
	}
	
	public static void displayPhoto(User user, ImageView imageView, ImageLoadingListener listener) {
		ImageLoader imageLoader = ImageLoader.getInstance();
		
		if (user != null && user.hasPhoto()) {
			String url = user.getPhotoUrl();
			DiskCache diskCache = imageLoader.getDiskCache();
			File cached = DiskCacheUtils.findInCache(url, diskCache);
			
			if (cached != null && cached.exists()) {
				Date updatedAt = user.getPhotoUpdatedAt();
				if (updatedAt != null && updatedAt.getTime() + TIME_DIFFERENCE_FROM_SERVER > cached.lastModified()) {
					DiskCacheUtils.removeFromCache(url, diskCache);
					
					try {
						MemoryCacheUtils.removeFromCache(url, imageLoader.getMemoryCache());
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
			imageView.setImageResource(R.drawable.user_character);
		}
	}
	
	public static void displayPhoto(String url, ImageView imageView) {
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(url, imageView);
	}
	
	public static void displayBlurPhoto(String url, final ImageView imageView, ImageSize imageSize, final int radius) {
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.loadImage(url, imageSize, new SimpleImageLoadingListener() {

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				super.onLoadingComplete(imageUri, view, loadedImage);
				BlurAsyncTask blurTask = new BlurAsyncTask();
				blurTask.setImageView(imageView);
				blurTask.setRadius(radius);
				blurTask.execute(loadedImage);
			}
		});
	}
	
}
