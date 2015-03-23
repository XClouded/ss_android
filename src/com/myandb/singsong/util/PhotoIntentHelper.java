package com.myandb.singsong.util;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

public class PhotoIntentHelper {
	
	public static final int REQUEST_CODE_PHOTO_PICKER = 200;
	
	private static PhotoIntentHelper singleton;
	
	private Uri shotUri;
	private Uri photoUri;
	private int shotUriIndex;
	
	private PhotoIntentHelper() {}
	
	public static PhotoIntentHelper getInstance() {
		if (singleton == null) {
			synchronized (PhotoIntentHelper.class) {
				if (singleton == null) {
					singleton = new PhotoIntentHelper();
				}
			}
		}
		
		return singleton;
	}
	
	public void showPicker(Fragment fragment) {
		shotUri = makeUriFromFileName(generateShotUriFileName());
		Intent intent = makeChooserIntent(shotUri);
		fragment.startActivityForResult(intent, REQUEST_CODE_PHOTO_PICKER);
	}
	
	public void showPicker(Activity activity) {
		shotUri = makeUriFromFileName(generateShotUriFileName());
		Intent intent = makeChooserIntent(shotUri);
		activity.startActivityForResult(intent, REQUEST_CODE_PHOTO_PICKER);
	}
	
	private Intent makeChooserIntent(Uri uri) {
		Intent photoPickerIntent = new Intent();
		photoPickerIntent.setType("image/*");
		photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
		
		Intent takePickerIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		takePickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		Intent chooserIntent = Intent.createChooser(photoPickerIntent, "Select or take a new Picture");
		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { takePickerIntent });
		
		return chooserIntent;
	}
	
	private Uri makeUriFromFileName(String fileName) {
		File root = ExternalStorage.getRootDirectory();
		File shotFile = new File(root, fileName);
		return Uri.fromFile(shotFile);
	}
	
	private String generateShotUriFileName() {
		return "shot." + String.valueOf(shotUriIndex++) + ".tmp";
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode != REQUEST_CODE_PHOTO_PICKER) {
			return;
		}
		
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		
		if (data != null) {
			photoUri = data.getData();
		} else {
			photoUri = shotUri;
		}
	}
	
	public Uri getPhotoUri() {
		return photoUri != null ? photoUri : Uri.EMPTY;
	}
	
}
