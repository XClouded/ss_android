package com.myandb.singsong.net;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.Uri;

import com.myandb.singsong.event.OnCompleteListener;
import com.myandb.singsong.image.ResizeAsyncTask;

public class PhotoUploadHelper {
	
	private OnCompleteListener listener;
	private List<Photo> photos;
	
	public PhotoUploadHelper() {
		photos = new ArrayList<Photo>();
	}
	
	public void addPhoto(Photo photo) {
		if (photo != null) {
			photos.add(photo);
		}
	}
	
	public void upload(final Context context) {
		ResizeAsyncTask asyncTask = new ResizeAsyncTask();
		asyncTask.setContentResolver(context.getContentResolver());
		asyncTask.setOnCompleteListener(new ResizeAsyncTask.OnCompleteListener() {
			
			@Override
			public void onComplete(List<File> files) {
				if (files != null && files.size() > 0 && photos.size() == files.size()) {
					UploadManager manager = new UploadManager();
					uploadRecursive(context, manager, files, 0);
				} else {
					dispatchOnCompleteListener(new Exception());
				}
			}
			
			@Override
			public void onError(Exception exception) {
				dispatchOnCompleteListener(exception);
			}
		});
		
		Uri[] uris = new Uri[photos.size()];
		for (int i = 0, l = uris.length; i < l; i++) {
			uris[i] = photos.get(i).getUri();
		}
		asyncTask.execute(uris);
	}
	
	private void uploadRecursive(final Context context, final UploadManager manager, final List<File> files, final int index) {
		if (index < 0) {
			dispatchOnCompleteListener(new Exception());
		} else if (index < files.size()) {
			File file = files.get(index);
			Photo photo = photos.get(index);
			try {
				manager.start(context, file, photo.getBucket(), photo.getFileName(), "image/jpeg",
						new OnCompleteListener() {
							
							@Override
							public void done(Exception e) {
								int newIndex = index + 1;
								uploadRecursive(context, manager, files, newIndex);
							}
						}
				);
			} catch (Exception e) {
				dispatchOnCompleteListener(e);
			}
		} else {
			if (listener != null) {
				listener.done(null);
			}
		}
	}
	
	public void setOnCompleteListener(OnCompleteListener listener) {
		this.listener = listener;
	}
	
	private void dispatchOnCompleteListener(Exception exception) {
		if (listener != null) {
			listener.done(exception);
		}
	}
	
	public static final class Photo {
		
		private Uri uri;
		private String bucket;
		private String fileName;
		
		public Photo(Uri uri, String bucket, String fileName) {
			this.uri = uri;
			this.bucket = bucket;
			this.fileName = fileName;
		}
		
		public Uri getUri() {
			return uri;
		}
		
		public String getBucket() {
			return bucket;
		}
		
		public String getFileName() {
			return fileName;
		}
		
	}

}
 