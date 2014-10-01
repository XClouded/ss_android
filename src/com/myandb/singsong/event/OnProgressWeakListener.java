package com.myandb.singsong.event;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.support.v4.app.Fragment;

public class OnProgressWeakListener<T> implements OnProgressListener {
	
	private WeakReference<T> weakReference;
	private String methodName;
	
	public OnProgressWeakListener(T reference) {
		this(reference, null);
	}
	
	public OnProgressWeakListener(T reference, String methodName) {
		this.weakReference = new WeakReference<T>(reference);
		this.methodName = methodName;
	}
	
	@Override
	public void done(Integer progress) {
		T reference = weakReference.get();
		
		if (reference != null) {
			if (reference instanceof Fragment) {
				if (((Fragment)reference).isAdded()) {
					filteredDone(reference, progress);
				}
			} else {
				filteredDone(reference, progress);
			}
		}
	}
	
	public void filteredDone(T reference, Integer progress) {
		if (methodName != null) {
			try {
				Method method = reference.getClass().getMethod(methodName, Integer.class);
				method.invoke(reference, progress);
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
