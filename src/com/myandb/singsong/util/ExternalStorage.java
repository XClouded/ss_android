package com.myandb.singsong.util;

import java.io.File;

import android.os.Environment;

public class ExternalStorage {
	
	public static final String ROOT_DIRECTORY = "CollaboKaraoke";
	
	private ExternalStorage() {}
	
	public static File getRootDirectory() {
		File root = new File(Environment.getExternalStorageDirectory() + "/" + ROOT_DIRECTORY);
		if (!root.exists()) {
			root.mkdir();
		}
		return root;
	}

}
