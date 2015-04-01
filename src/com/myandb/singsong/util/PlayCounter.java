package com.myandb.singsong.util;

import org.json.JSONObject;

import com.myandb.singsong.App;
import com.myandb.singsong.net.JustRequest;

import android.content.Context;

public class PlayCounter {
	
	private static String savedEntityName = "";
	private static int savedEntityId = 0;
	private static int second = 0;
	
	public static void countAsync(Context context, String entityName, int entityId) {
		if (savedEntityName.equals(entityName) && savedEntityId == entityId) {
			second++;
		} else {
			second = 0;
		}
		
		savedEntityName = entityName;
		savedEntityId = entityId;
		
		if (second == 60) {
			String segment = new StringBuilder()
			.append(entityName)
			.append("/")
			.append(entityId)
			.append("/logs").toString();
			JustRequest request = new JustRequest(segment, null, new JSONObject());
			((App) context.getApplicationContext()).addShortLivedRequest(context, request);
		}
	}
	
}
