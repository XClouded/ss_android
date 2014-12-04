package com.myandb.singsong.util;

import org.json.JSONObject;

import com.myandb.singsong.App;
import com.myandb.singsong.net.JustRequest;

import android.content.Context;

public class PlayCounter {
	
	public static void countAsync(Context context, String entityName, int entityId) {
		String segment = new StringBuilder()
				.append(entityName)
				.append("/")
				.append(entityId)
				.append("/logs").toString();
		JustRequest request = new JustRequest(segment, new JSONObject());
		((App) context.getApplicationContext()).addRequest(context, request);
	}
	
}
