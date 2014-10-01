package com.myandb.singsong.file;

import java.io.File;
import java.io.IOException;

import android.os.Environment;

public class FileManager {
	
	public static final String USER_PHOTO = "user.dat";
	public static final String TEMP = "temp.dat";
	public static final String TEMP_2 = "temp2.dat";
	public static final String MUSIC_OGG = "music.ogg";
	public static final String MUSIC_RAW = "music.raw";
	public static final String VOICE_RAW = "voice.raw";
	public static final String SONG_OGG = "song.ogg";
	public static final String LYRIC = "lyric.lrc";
	
	private FileManager() {}
	
	private static File mkdir(String parentDirectoryName, String directoryName) {
		File directory = new File(parentDirectoryName, directoryName);
		
		if (!directory.exists()) {
			if (directory.mkdir()) {
				return directory;
			}
		}
		
		return directory;
	}
	
	public static File getRootDirectory() {
		File sdcard = Environment.getExternalStorageDirectory();
		
		return mkdir(sdcard.getAbsolutePath(), "SingSongRecorder");
	}
	
	public static File getSubDirectory(String dirName) {		
		return mkdir(getRootDirectory().getAbsolutePath(), dirName);
	}
	
	public static File get(String fileName) {
		File file = new File(getRootDirectory().getAbsolutePath() + "/" + fileName);
    	
    	return file;
	}
    
    public static File getSecure(String fileName) {
    	File file = new File(getRootDirectory().getAbsolutePath() + "/" + fileName);
    	
    	try {
    		if ( !file.exists() ) {
    			file.createNewFile();
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	
    	return file;
    }
	
	public static boolean isExist(String fileName) {
		File file = new File(getRootDirectory().getAbsolutePath() + "/" + fileName);
		
		return file.exists();
	}

}
