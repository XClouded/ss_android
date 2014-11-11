package com.myandb.singsong.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.res.Resources;
import android.media.MediaMetadataRetriever;

import com.myandb.singsong.R;

public class TimeHelper {
	
	public static final int INVALID_TIME = -1;
	public static final int TODAY = 1;
	public static final int PAST = 2;
	
	private static Calendar today = Calendar.getInstance(Locale.KOREA);
	private static Calendar varDay = Calendar.getInstance(Locale.KOREA);
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
	private static String nowString;
	private static String minuteString;
	private static String hourString;
	private static String dayString;
	
	private TimeHelper() { }
	
	public static void initialize(Resources resources) {
		nowString = resources.getString(R.string.now);
		minuteString = resources.getString(R.string.minute);
		hourString = resources.getString(R.string.hour);
		dayString = resources.getString(R.string.day);
	}
	
	public static int getTodayInNumber() {
		return (today.get(Calendar.YEAR) - 1) * 365 + today.get(Calendar.DAY_OF_YEAR); 
	}
	
	public static int checkToday(String dateInString) {
		if ("".equals(dateInString)) {
			return TimeHelper.INVALID_TIME;
		}
		
		try {
			varDay.setTime(format.parse(dateInString));
			
			if (today.get(Calendar.YEAR) == varDay.get(Calendar.YEAR)) {
				if (today.get(Calendar.DAY_OF_YEAR) == varDay.get(Calendar.DAY_OF_YEAR)) {
					return TimeHelper.TODAY;
				} else {
					return TimeHelper.PAST;
				}
			} else {
				return TimeHelper.PAST;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return TimeHelper.INVALID_TIME;
	}
	
	public static String getDateString(int field, int value) {
		varDay.add(field, value);
		
		String dateString = format.format(varDay.getTime());
		
		varDay.add(field, -value);
		
		return dateString;
	}
	
	public static String getDateString(int field, int value, int dayOfWeek) {
		int currentDayOfWeek = varDay.get(Calendar.DAY_OF_WEEK);
		
		varDay.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		
		String dateString = getDateString(field, value);
		
		varDay.set(Calendar.DAY_OF_WEEK, currentDayOfWeek);
		
		return dateString;
	}

	public static int getDuration(File file) {
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		int duration;
    	
    	try {
    		retriever.setDataSource(file.getAbsolutePath());
    		duration = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
    	} catch (Exception e) {
    		duration = 0;
    	} finally { 
    		retriever.release();
    	}
    	
    	return duration;
	}
	
	public static String getDuration(int milliSeconds) {
		String string = "";
		
		int secs = (int) (milliSeconds / 1000);
		int min = (int) (secs / 60);
		int sec = secs % 60;
		
		if (min < 10) {
			string += "0";
		}
		
		string += String.valueOf(min);
		string += ":";
		if (sec < 10) {
			string += "0";
		}
		string += String.valueOf(sec);
		
		return string;
	}
	
	public static String getTimeLag(Date currentDate, Date createdDate) {
		long differenceInMils = currentDate.getTime() - createdDate.getTime();
		
		int differenceInSecs = (int) (differenceInMils / 1000);
		
		if (differenceInSecs < 60) {
			return nowString;
		} else {
			int differenceInMins = differenceInSecs / 60;
			
			if (differenceInMins < 60) {
				return String.valueOf(differenceInMins) + minuteString;
			} else {
				int differenceInHours = differenceInMins / 60;
				
				if (differenceInHours < 24) {
					return String.valueOf(differenceInHours) + hourString;
				} else {
					int differenceInDays = differenceInHours / 24;
					
					if (differenceInDays < 30) {
						return String.valueOf(differenceInDays) + dayString;
					} else {
						return format.format(createdDate.getTime());
					}
				}
			}
		}
	}

}
