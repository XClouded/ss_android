package com.myandb.singsong.net;

import java.lang.reflect.Method;

import org.json.JSONObject;

import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.myandb.singsong.util.Utility;

public class JSONObjectSuccessListener implements Listener<JSONObject> {
	
	private Object receiver;
	private Method method;
	private Class<?> responseType;
	
	public JSONObjectSuccessListener(Object receiver, String methodName) {
		this(receiver, methodName, null);
	}
	
	public JSONObjectSuccessListener(Object receiver, String methodName, Class<?> responseType) {
		try {
			this.receiver = receiver;
			this.responseType = responseType;
			
			if (responseType == null) {
				method = receiver.getClass().getMethod(methodName, JSONObject.class);
			} else {
				method = receiver.getClass().getMethod(methodName, responseType);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	final public void onResponse(JSONObject response) {
		try {
			if (responseType == null) {
				method.invoke(receiver, response);
			} else {
				Gson gson = Utility.getGsonInstance();
				method.invoke(receiver, gson.fromJson(response.toString(), responseType));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
