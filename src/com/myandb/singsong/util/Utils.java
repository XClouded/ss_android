package com.myandb.singsong.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

public class Utils {

	public static final String EMPTY = "";
	
	private static Calendar calendar = Calendar.getInstance(Locale.KOREA);
	
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
	
	private Utils() {}
	
	public static void recycleRecursive(View root) {
		if (root == null) {
			return;
		}
		
		if (root instanceof ViewGroup) {
			ViewGroup group = (ViewGroup) root;
			int count = group.getChildCount();
			for (int i = 0; i < count; i++) {
				recycleRecursive(group.getChildAt(i));
			}
			
			if (!(root instanceof AdapterView)) {
				group.removeAllViews();
			}
		}
		
		if (root instanceof ImageView) {
			ImageView imageView = (ImageView)root;
			imageView.setImageDrawable(null);
		}
		
		if (root.getBackground() != null) {
			root.getBackground().setCallback(null);
			root.setBackgroundResource(0);
		}
		
		root = null;
	}
	
	public static void getRelativeSizeSpan(Spannable spannable, float size) {
		if (spannable != null) {
			spannable.setSpan(
					new RelativeSizeSpan(size), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}
	
	public static void getStyleSpan(Spannable spannable, int style) {
		if (spannable != null) {
			spannable.setSpan(
					new StyleSpan(style), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}
	
	public static Spannable getBoldSpan(CharSequence charSequence) {
		Spannable boldSpan = new SpannableString(charSequence);
		getStyleSpan(boldSpan, Typeface.BOLD);
		return boldSpan;
	}
	
	public static void getColorSpan(Spannable spannable, String color) {
		getColorSpan(spannable, Color.parseColor(color));
	}
	
	public static void getColorSpan(Spannable spannable, int color) {
		if (spannable != null) {
			spannable.setSpan(
					new ForegroundColorSpan(color), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}
	
	public static String createHtmlAnchor(String address, String name) {
		String anchor = "<a href=\"";
		anchor += address;
		anchor += "\">";
		anchor += name;
		anchor += "</a>"; 
		
		return anchor;
	}
	
	public static String getTimeLag(Date currentDate, Date createdDate) {
		long differenceInMils = currentDate.getTime() - createdDate.getTime();
		
		int differenceInSecs = (int) (differenceInMils / 1000);
		
		if (differenceInSecs < 60) {
			return "방금 전";
		} else {
			int differenceInMins = differenceInSecs / 60;
			
			if (differenceInMins < 60) {
				return String.valueOf(differenceInMins) + "분 전";
			} else {
				int differenceInHours = differenceInMins / 60;
				
				if (differenceInHours < 24) {
					return String.valueOf(differenceInHours) + "시간 전";
				} else {
					int differenceInDays = differenceInHours / 24;
					
					if (differenceInDays < 30) {
						return String.valueOf(differenceInDays) + "일 전";
					} else {
						return format.format(createdDate.getTime());
					}
				}
			}
		}
	}

	public static String getDateString(int field, int value) {
		calendar.add(field, value);
		
		String dateString = format.format(calendar.getTime());
		
		calendar.add(field, -value);
		
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
	
	public static String getColonFormatDuration(int milliSeconds) {
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
	
}
