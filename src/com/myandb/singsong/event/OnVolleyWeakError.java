package com.myandb.singsong.event;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.myandb.singsong.adapter.AutoLoadAdapter;
import com.myandb.singsong.secure.Authenticator;

public class OnVolleyWeakError<T> implements ErrorListener {
	
	private WeakReference<T> weakReference;
	private String methodName;
	
	public OnVolleyWeakError(T reference) {
		this(reference, null);
	}
	
	public OnVolleyWeakError(T reference, String methodName) {
		this.weakReference = new WeakReference<T>(reference);
		this.methodName = methodName;
	}
	
	@Override
	public void onErrorResponse(VolleyError error) {
		T reference = weakReference.get();
			
		if (reference != null) {
			if (reference instanceof Fragment) {
				if (((Fragment) reference).isAdded()) {
					onFilteredResponse(reference, error);
				}
			} else {
				onFilteredResponse(reference, error);
			}
		}
	}

	public void onFilteredResponse(T reference, VolleyError error) {
		error.printStackTrace();
		
		if (error instanceof AuthFailureError) {
			Authenticator auth = new Authenticator();
			auth.logout();
			
			Context context = getContext(reference);
			Toast.makeText(context, "인증 정보가 유효하지 않습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show();
			
			if (context instanceof Activity) {
				((Activity) context).finish();
			}
		} else {
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
	
	private Context getContext(T reference) {
		if (reference instanceof Fragment) {
			return ((Fragment) reference).getActivity();
		}
		
		if (reference instanceof Dialog) {
			return ((Dialog) reference).getContext();
		}
		
		if (reference instanceof AutoLoadAdapter<?>) {
			return ((AutoLoadAdapter<?>) reference).getContext();
		}
		
		return (Context) reference;
	}

}
