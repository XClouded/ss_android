package com.myandb.singsong.image;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

public final class NoBlinkFadeInBitmapDisplayer extends FadeInBitmapDisplayer {
	
	public NoBlinkFadeInBitmapDisplayer(int durationMillis) {
		super(durationMillis);
	}

	@Override
	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
		super.display(bitmap, imageAware, loadedFrom);
		if (loadedFrom == LoadedFrom.MEMORY_CACHE || loadedFrom == LoadedFrom.DISC_CACHE) {
			imageAware.setImageBitmap(bitmap);
		}
	}
	
}
