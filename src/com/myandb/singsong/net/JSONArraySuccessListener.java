package com.myandb.singsong.net;

import java.lang.reflect.Method;

import org.json.JSONArray;

import com.android.volley.Response.Listener;

public class JSONArraySuccessListener implements Listener<JSONArray> {
	
	private Object receiver;
	private Method method;
	
	public JSONArraySuccessListener(Object receiver, String methodName) {
		try {
			this.receiver = receiver;
			method = receiver.getClass().getMethod(methodName, JSONArray.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	final public void onResponse(JSONArray response) {
		try {
			method.invoke(receiver, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
