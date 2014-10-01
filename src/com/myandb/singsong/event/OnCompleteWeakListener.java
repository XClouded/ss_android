package com.myandb.singsong.event;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.support.v4.app.Fragment;

public class OnCompleteWeakListener<T> implements OnCompleteListener {

	private WeakReference<T> weakReference;
	private String onSuccessMethodName;
	private String onErrorMethodName;
	
	public OnCompleteWeakListener(T reference) {
		this(reference, null);
	}
	
	public OnCompleteWeakListener(
			T reference, String onSuccessMethodName) {
		
		this(reference, onSuccessMethodName, null);
	}
	
	public OnCompleteWeakListener(
			T reference, String onSuccessMethodName, String onErrorMethodName) {
		
		this.weakReference = new WeakReference<T>(reference);
		this.onSuccessMethodName = onSuccessMethodName;
		this.onErrorMethodName = onErrorMethodName;
	}

	@Override
	public void done(Exception error) {
		T reference = weakReference.get();
		
		if (reference != null) {
			if (reference instanceof Fragment) {
				if (((Fragment)reference).isAdded()) {
					filteredDone(reference, error);
				}
			} else {
				filteredDone(reference, error);
			}
		}
	}
	
	public void filteredDone(T reference, Exception error) {
		try {
			if (error == null) {
				if (onSuccessMethodName != null) {
					Method method = reference.getClass().getMethod(onSuccessMethodName);
					method.invoke(reference);
				}
			} else {
				error.printStackTrace();
				
				if (onErrorMethodName != null) {
					Method method = reference.getClass().getMethod(onErrorMethodName);
					method.invoke(reference);
				}
			}
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