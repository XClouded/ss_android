package com.myandb.singsong.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.myandb.singsong.file.FileManager;
import com.myandb.singsong.util.ImageHelper.BitmapBuilder;

public class ResizeAsyncTask extends AsyncTask<InputStream, Integer, Bitmap> {

	private ImageView imageView;
	
	@Override
	protected Bitmap doInBackground(InputStream... params) {
		if (params != null && params.length > 0) {
			InputStream imageStream = params[0];
			File temp = FileManager.get(FileManager.TEMP);
			FileOutputStream output = null;
			Bitmap mayRotatedBitmap = null;
			Bitmap correctedBitmap = null;
			byte[] datas = null; 
			
			try {
				output = FileUtils.openOutputStream(temp);
				IOUtils.copy(imageStream, output);
				
				ExifInterface exif = new ExifInterface(temp.getAbsolutePath());
				int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
				int rotate = 0;
				
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotate = 90;
					break;
					
				case ExifInterface.ORIENTATION_ROTATE_180:
					rotate = 180;
					break;
					
				case ExifInterface.ORIENTATION_ROTATE_270:
					rotate = 270;
					break;
				}
				
				Matrix matrix = new Matrix();
				matrix.postRotate(rotate);
				
				BitmapBuilder builder = new BitmapBuilder();
				mayRotatedBitmap = builder.setSource(temp)
										  .enableCrop(false)
										  .setOutputSize(120)
										  .build();
				correctedBitmap = Bitmap.createBitmap(mayRotatedBitmap,
														0,
														0,
														mayRotatedBitmap.getWidth(),
														mayRotatedBitmap.getHeight(),
														matrix,
														true );
				
				datas = bitmapToByteArray(correctedBitmap);
				
				if (datas != null) {
					FileUtils.writeByteArrayToFile(temp, datas);
				}
				
				imageStream.close();
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				datas = null;
			}
			
			return correctedBitmap;
		}
		
		return null;
	}
	
	private byte[] bitmapToByteArray(Bitmap bitmap) {
		byte[] byteArray = null;
		
		if (bitmap != null) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();  
			bitmap.compress(CompressFormat.JPEG, 95, stream);
			byteArray = stream.toByteArray();
		}
        
        return byteArray;  
    } 

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		
		if (imageView != null && result != null) {
			imageView.setImageBitmap(result);
		}
	}	
	
	public void setImageView(ImageView profile) {
		this.imageView = profile;
	}
	
}