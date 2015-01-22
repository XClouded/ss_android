package com.myandb.singsong.image;

import java.io.File;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;

public class BitmapBuilder {
	
	private final static int SOURCE_FILE = 1;
	private final static int SOURCE_RESOURCE = 2;
	
	private File file;
	private Resources resources;
	private int resourceId;
	private boolean autoScale = true;
	private boolean crop = false;
	private int scaleSize = 1;
	private int outputSize = 60;
	private int sourceType;
	private Config config;
	
	public BitmapBuilder setSource(File file) {
		if (file != null && file.exists()) {
			this.file = file;
			this.sourceType = SOURCE_FILE;
		}
		return this;
	}
	
	public BitmapBuilder setSource(Resources resources, int resourceId) {
		if (resources != null) {
			this.resources = resources;
			this.resourceId = resourceId;
			this.sourceType = SOURCE_RESOURCE;
		}
		return this;
	}
	
	public BitmapBuilder setOutputSize(int outputSize) {
		if (outputSize > 0) {
			this.autoScale = true;
			this.outputSize = outputSize;
		}
		
		return this;
	}
	
	public BitmapBuilder setFixedScaleSize(int scaleSize) {
		if (scaleSize > 1) {
			this.autoScale = false;
			this.scaleSize = scaleSize;
		}
		
		return this;
	}
	
	public BitmapBuilder enableCrop(boolean isCrop) {
		this.crop = isCrop;
		
		return this;
	}
	
	public Bitmap build() {
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inJustDecodeBounds = true;
		
		if (config != null) {
			option.inPreferredConfig = config;
		} else {
			option.inPreferredConfig = Config.RGB_565;
		}
		
		switch (sourceType) {
		case SOURCE_FILE:
			BitmapFactory.decodeFile(file.getAbsolutePath(), option);
			break;
			
		case SOURCE_RESOURCE:
			BitmapFactory.decodeResource(resources, resourceId, option);
			break;

		default:
			return null;
		}
		
		if (autoScale) {
			float widthScale = option.outWidth / ((float) outputSize);
			float heightScale = option.outHeight / ((float) outputSize);
			float scale = widthScale > heightScale ? heightScale : widthScale;
			
			if (scale >= 10) {
				option.inSampleSize = 10;
			} else if (scale >= 8) {
				option.inSampleSize = 8;
			} else if (scale >= 6) {
				option.inSampleSize = 6;
			} else if (scale >= 4) {
				option.inSampleSize = 4;
			} else if (scale >= 2) {
				option.inSampleSize = 2;
			} else {
				option.inSampleSize = 1;
			}
		} else {
			option.inSampleSize = scaleSize;
		}
		
		option.inJustDecodeBounds = false;
		Bitmap outputBitmap = null;
		
		switch (sourceType) {
		case SOURCE_FILE:
			outputBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), option);
			break;
			
		case SOURCE_RESOURCE:
			outputBitmap = BitmapFactory.decodeResource(resources, resourceId, option);
			break;

		default:
			return null;
		}
		
		if (crop) {
			if (outputBitmap != null) {
				int width = outputBitmap.getWidth();
				int height = outputBitmap.getHeight();
				
				if (width >= height) {
					return Bitmap.createBitmap(
							outputBitmap,
							width / 2 - height / 2,
							0,
							height,
							height
					);
				} else {
					return Bitmap.createBitmap(
							outputBitmap,
							0,
							height / 2 - width / 2,
							width,
							width
					);
				}
			}
		}
		
		return outputBitmap;
	}
	
}
