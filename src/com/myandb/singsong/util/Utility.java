package com.myandb.singsong.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

public class Utility {
	
	private static Gson gson;
	
	private Utility() {}
	
	public static void recursiveRecycle(View root) {
		if (root == null) {
			return;
		}
		
		if (root instanceof ViewGroup) {
			ViewGroup group = (ViewGroup) root;
			int count = group.getChildCount();
			for (int i = 0; i < count; i++) {
				recursiveRecycle(group.getChildAt(i));
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
	
	public static Gson getGsonInstance() {
		if (gson == null) {
			gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		}
		
		return gson;
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
		if (spannable != null) {
			spannable.setSpan(
					new ForegroundColorSpan(Color.parseColor(color)), 0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}
	
	public static String getHtmlAnchor(String address, String name) {
		String anchor = "<a href=\"";
		anchor += address;
		anchor += "\">";
		anchor += name;
		anchor += "</a>"; 
		
		return anchor;
	}
	
}
