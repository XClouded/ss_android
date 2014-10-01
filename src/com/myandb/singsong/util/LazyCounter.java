package com.myandb.singsong.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.SparseIntArray;

public class LazyCounter {

	private static LazyCounter sLazyCounter;
	
	private SparseIntArray mCountMap;
	
	private LazyCounter() {
		mCountMap = new SparseIntArray();
	}
	
	public static LazyCounter getInstance() {
		if (sLazyCounter == null) {
			sLazyCounter = new LazyCounter();
		}
		
		return sLazyCounter;
	}
	
	public void count(int id) {
		mCountMap.put(id, get(id) + 1);
	}
	
	public int get(int id) {
		return mCountMap.get(id, 0);
	}
	
	public JSONObject assemble(boolean reusable) {
		JSONObject message = null;
		
		try {
			JSONArray chunks = new JSONArray();
			
			int key = 0;
			JSONObject chunk;
			for (int i = 0, l = mCountMap.size(); i < l; i++) {
				key = mCountMap.keyAt(i);
				
				chunk = new JSONObject();
				chunk.put("id", key);
				chunk.put("num", mCountMap.get(key));
				
				chunks.put(chunk);
			}
			
			if (chunks.length() > 0) {
				message = new JSONObject();
				message.put("nums", chunks);
			}
		} catch (JSONException e) {
			// unhandled exception
		} finally {
			if (!reusable) {
				mCountMap.clear();
			}
		}
		
		return message;
	}
}
