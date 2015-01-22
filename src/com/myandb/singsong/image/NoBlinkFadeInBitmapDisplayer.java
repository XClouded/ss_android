package com.myandb.singsong.image;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public final class NoBlinkFadeInBitmapDisplayer extends FadeInBitmapDisplayer {
	
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
