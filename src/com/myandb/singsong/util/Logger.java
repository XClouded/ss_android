package com.myandb.singsong.util;

import com.myandb.singsong.BuildConfig;

import android.util.Log;

public class Logger {
	
	private static final String TAG = "debug";
	
	public static void log(Object... values) {
		if (BuildConfig.DEBUG) {
			String message = "";
			for (Object value : values) {
				message += String.valueOf(value);
				message += " "; 
			}
			Log.e(TAG, message);
		}
	}

}
