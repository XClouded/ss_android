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
	
	public static class BlurAsyncTask extends AsyncTask<File, Integer, Bitmap> {
		
		private int radius = 5;
		private ImageView imageView;

		@Override
		protected Bitmap doInBackground(File... params) {
			File file = params[0];
			
			if (file == null || !file.exists() || radius < 1) {
				return null;
			}
			
			BitmapBuilder builder = new BitmapBuilder();
			Bitmap bitmap = builder.setOutputSize(100).setSource(file).build();
			if (bitmap != null) {
				Config config = bitmap.getConfig();
				if (config == null) {
					bitmap = bitmap.copy(Config.RGB_565, true); 
				} else {
					bitmap = bitmap.copy(bitmap.getConfig(), true);
				}
			} else {
				return null;
			}
			
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			
			int[] pix = new int[w * h];
			bitmap.getPixels(pix, 0, w, 0, 0, w, h);
			
			int wm = w - 1;
			int hm = h - 1;
			int wh = w * h;
			int div = radius + radius + 1;
			
			int r[] = new int[wh];
			int g[] = new int[wh];
			int b[] = new int[wh];
			int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
			int vmin[] = new int[Math.max(w, h)];
			
			int divsum = (div + 1) >> 1;
			divsum *= divsum;
			int dv[] = new int[256 * divsum];
			for (i = 0; i < 256 * divsum; i++) {
				dv[i] = (i / divsum);
			}
			
			yw = yi = 0;
			
			int[][] stack = new int[div][3];
			int stackpointer;
			int stackstart;
			int[] sir;
			int rbs;
			int r1 = radius + 1;
			int routsum, goutsum, boutsum;
			int rinsum, ginsum, binsum;
			
			for (y = 0; y < h; y++) {
				rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
				
				for (i = -radius; i <= radius; i++) {
					p = pix[yi + Math.min(wm, Math.max(i, 0))];
					sir = stack[i + radius];
					sir[0] = (p & 0xff0000) >> 16;
					sir[1] = (p & 0x00ff00) >> 8;
					sir[2] = (p & 0x0000ff);
					rbs = r1 - Math.abs(i);
					rsum += sir[0] * rbs;
					gsum += sir[1] * rbs;
					bsum += sir[2] * rbs;
					if (i > 0) {
						rinsum += sir[0];
						ginsum += sir[1];
						binsum += sir[2];
					} else {
						routsum += sir[0];
						goutsum += sir[1];
						boutsum += sir[2];
					}
				}
				
				stackpointer = radius;
				
				for (x = 0; x < w; x++) {
					
					r[yi] = dv[rsum];
					g[yi] = dv[gsum];
					b[yi] = dv[bsum];
					
					rsum -= routsum;
					gsum -= goutsum;
					bsum -= boutsum;
					
					stackstart = stackpointer - radius + div;
					sir = stack[stackstart % div];
					
					routsum -= sir[0];
					goutsum -= sir[1];
					boutsum -= sir[2];
					
					if (y == 0) {
						vmin[x] = Math.min(x + radius + 1, wm);
					}
					p = pix[yw + vmin[x]];
					
					sir[0] = (p & 0xff0000) >> 16;
					sir[1] = (p & 0x00ff00) >> 8;
					sir[2] = (p & 0x0000ff);
					
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
					
					rsum += rinsum;
					gsum += ginsum;
					bsum += binsum;
					
					stackpointer = (stackpointer + 1) % div;
					sir = stack[(stackpointer) % div];
					
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
					
					rinsum -= sir[0];
					ginsum -= sir[1];
					binsum -= sir[2];
					
					yi++;
				}
				
				yw += w;
			}
			
			for (x = 0; x < w; x++) {
				rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
				yp = -radius * w;
				for (i = -radius; i <= radius; i++) {
					yi = Math.max(0, yp) + x;
					
					sir = stack[i + radius];
					
					sir[0] = r[yi];
					sir[1] = g[yi];
					sir[2] = b[yi];
					
					rbs = r1 - Math.abs(i);
					
					rsum += r[yi] * rbs;
					gsum += g[yi] * rbs;
					bsum += b[yi] * rbs;
					
					if (i > 0) {
						rinsum += sir[0];
						ginsum += sir[1];
						binsum += sir[2];
					} else {
						routsum += sir[0];
						goutsum += sir[1];
						boutsum += sir[2];
					}
					
					if (i < hm) {
						yp += w;
					}
				}
				yi = x;
				stackpointer = radius;
				for (y = 0; y < h; y++) {
					// Preserve alpha channel: ( 0xff000000 & pix[yi] )
					pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];
				
					rsum -= routsum;
					gsum -= goutsum;
					bsum -= boutsum;
					
					stackstart = stackpointer - radius + div;
					sir = stack[stackstart % div];
					
					routsum -= sir[0];
					goutsum -= sir[1];
					boutsum -= sir[2];
					
					if (x == 0) {
						vmin[y] = Math.min(y + r1, hm) * w;
					}
					p = x + vmin[y];
					
					sir[0] = r[p];
					sir[1] = g[p];
					sir[2] = b[p];
					
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
					
					rsum += rinsum;
					gsum += ginsum;
					bsum += binsum;
					
					stackpointer = (stackpointer + 1) % div;
					sir = stack[stackpointer];
					
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
					
					rinsum -= sir[0];
					ginsum -= sir[1];
					binsum -= sir[2];
					
					yi += w;
				}
			}
			
			bitmap.setPixels(pix, 0, w, 0, 0, w, h);
			
			return (bitmap);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			
			if (imageView != null && result != null) {
				imageView.setImageBitmap(result);
			}
		}
		
		public void setRadius(int radius) {
			this.radius = radius;
		}
		
		public void setImageView(ImageView imageView) {
			this.imageView = imageView;
		}
		
	}
	
}
