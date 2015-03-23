package com.myandb.singsong.image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.myandb.singsong.util.ExternalStorage;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;

public class ResizeAsyncTask extends AsyncTask<Uri, Integer, Exception> {

	private List<File> resizedFiles;
	private OnCompleteListener listener;
	private ContentResolver contentResolver;
	private int outputSize = 256;
	
	@Override
	protected Exception doInBackground(Uri... params) {
		if (params == null || params.length == 0) {
			return new IllegalArgumentException();
		}
		
		if (resizedFiles != null) {
			resizedFiles.clear();
		}
		resizedFiles = new ArrayList<File>();
		
		for (Uri uri : params) {
			try {
				File originalFile = new File(uri.toString());
				File resizedFile = new File(ExternalStorage.getRootDirectory(), originalFile.getName() + ".resized");
				InputStream inputStream = contentResolver.openInputStream(uri);
				FileOutputStream outputStream = FileUtils.openOutputStream(resizedFile);
				Bitmap mayRotatedBitmap = null;
				Bitmap correctedBitmap = null;
				byte[] datas = null;
				
				IOUtils.copy(inputStream, outputStream);
				
				ExifInterface exif = new ExifInterface(resizedFile.getAbsolutePath());
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
				mayRotatedBitmap = builder.setSource(resizedFile)
						.enableCrop(false)
						.setOutputSize(outputSize)
						.build();
				
				correctedBitmap = Bitmap.createBitmap(
						mayRotatedBitmap,
						0,
						0,
						mayRotatedBitmap.getWidth(),
						mayRotatedBitmap.getHeight(),
						matrix,
						true 
				);
				
				datas = bitmapToByteArray(correctedBitmap);
				
				if (datas != null) {
					FileUtils.writeByteArrayToFile(resizedFile, datas);
				}
				
				inputStream.close();
				outputStream.close();
				
				originalFile.delete();
				resizedFiles.add(resizedFile);
			} catch (Exception e) {
				return e;
			}
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
	protected void onPostExecute(Exception exception) {
		super.onPostExecute(exception);
		
		if (listener != null) {
			if (exception == null) {
				listener.onComplete(resizedFiles);
			} else {
				listener.onError(exception);
			}
		}
	}
	
	public void setContentResolver(ContentResolver resolver) {
		this.contentResolver = resolver;
	}
	
	public void setOnCompleteListener(OnCompleteListener listener) {
		this.listener = listener;
	}
	
	public void setOutputSize(int size) {
		this.outputSize = size;
	}
	
	public interface OnCompleteListener {
		
		public void onComplete(List<File> files);
		
		public void onError(Exception exception);
		
	}
	
}