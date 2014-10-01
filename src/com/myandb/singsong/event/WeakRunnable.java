package com.myandb.singsong.event;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.support.v4.app.Fragment;

public class WeakRunnable<T> implements Runnable {
	
	private WeakReference<T> weakReference;
	private String methodName;
	
	public WeakRunnable(T reference) {
		this(reference, null);
	}
	
	public WeakRunnable(T reference, String methodName) {
		this.weakReference = new WeakReference<T>(reference);
		this.methodName = methodName;
	}
	
	@Override
	public void run() {
		T reference = weakReference.get();
		
		if (reference != null) {
			if (reference instanceof Fragment) {
				if (((Fragment)reference).isAdded()) {
					onFilteredRun(reference);
				}
			} else {
				onFilteredRun(reference);
			}
		}
	}

	public void onFilteredRun(T reference) {
		if (methodName != null) {
			try {
				Method method = reference.getClass().getMethod(methodName);
				method.invoke(reference);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

}
