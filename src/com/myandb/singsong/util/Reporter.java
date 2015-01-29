package com.myandb.singsong.util;

import java.util.HashMap;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;

import android.content.Context;

public class Reporter {
	
	private static Reporter singleton;
	
	private Context context;
	
	private Reporter(Context context) {
		if (context != null) {
			this.context = context.getApplicationContext();
		}
	}
	
	public static Reporter getInstance(Context context) {
		if (singleton == null) {
			singleton = new Reporter(context);
		}
		return singleton;
	}
	
	public void reportUIActionOnAnalytics(String action, String label) {
		reportOnAnalytics("UI Action", action, label);
	}
	
	public void reportExceptionOnAnalytics(String action, String label) {
		reportOnAnalytics("Exception", action, label);
	}
	
	public void reportOnAnalytics(String category, String action, String label) {
		if (context == null) {
			return;
		}
		
		HashMap<String, String> hitParameters = new HashMap<String, String>();
		hitParameters.put(Fields.HIT_TYPE, com.google.analytics.tracking.android.HitTypes.EVENT);
		hitParameters.put(Fields.EVENT_CATEGORY, category);
		hitParameters.put(Fields.EVENT_ACTION, action);
		hitParameters.put(Fields.EVENT_LABEL, label);
		EasyTracker.getInstance(context).send(hitParameters);
		
		context = null;
	}

}
