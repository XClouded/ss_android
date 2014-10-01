package com.myandb.singsong.event;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.support.v4.app.Fragment;

import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.myandb.singsong.util.Utility;

public class OnVolleyWeakResponse<T, RT> implements Listener<RT> {
	
	private WeakReference<T> weakReference;
	private String methodName;
	private Class<?> paramType;
	
	public OnVolleyWeakResponse(T reference) {
		this(reference, null, null);
	}
	
	public OnVolleyWeakResponse(T reference, String methodName) {
		this(reference, methodName, null);
	}
	
	public OnVolleyWeakResponse(T reference, String methodName, Class<?> paramType) {
		this.weakReference = new WeakReference<T>(reference);
		this.methodName = methodName;
		this.paramType = paramType;
	}

	@Override
	final public void onResponse(RT response) {
		T reference = weakReference.get();
			
		if (reference != null) {
			if (reference instanceof Fragment) {
				if (((Fragment) reference).isAdded()) {
					onFilteredResponse(reference, response);
				}
			} else {
				onFilteredResponse(reference, response);
			}
		}
	}
	
	protected void onFilteredResponse(T reference, RT response) {
		if (methodName != null) {
			try {
				if (paramType == null) {
					Method method = reference.getClass().getMethod(methodName, response.getClass());
					method.invoke(reference, response);
				} else {
					Gson gson = Utility.getGsonInstance();
					
					Method method = reference.getClass().getMethod(methodName, paramType);
					method.invoke(reference, gson.fromJson(response.toString(), paramType));
				}
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
		}
	}

}
