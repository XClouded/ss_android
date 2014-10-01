package com.myandb.singsong.util;

import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.myandb.singsong.App;
import com.myandb.singsong.net.OAuthJustRequest;
import com.myandb.singsong.net.UrlBuilder;

import android.content.Context;

public class Logger {
	
	public static void countAsync(Context context, String entityName, int entityId) {
		UrlBuilder urlBuilder = UrlBuilder.getInstance();
		String url = urlBuilder.l(entityName).l(entityId).l("logs").build();
		
		OAuthJustRequest request = new OAuthJustRequest(url, new JSONObject());
		
		RequestQueue queue = ((App) context.getApplicationContext()).getQueueInstance();
		queue.add(request);
	}
	
}
