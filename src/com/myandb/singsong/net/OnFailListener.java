package com.myandb.singsong.net;

import java.lang.reflect.Method;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.facebook.Session;
import com.myandb.singsong.secure.Authenticator;

public class OnFailListener implements ErrorListener {
	
	private Object receiver;
	private Method method;
	
	public OnFailListener() {
		this(null, null);
	}
	
	public OnFailListener(Object receiver, String methodName) {
		try {
			this.receiver = receiver;
			
			if (methodName != null) {
				method = receiver.getClass().getMethod(methodName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onErrorResponse(VolleyError error) {
		error.printStackTrace();
		
		if (error instanceof AuthFailureError) {
			Authenticator auth = new Authenticator();
			auth.logout();
			Session session = Session.getActiveSession();
			if (session != null) {
				session.closeAndClearTokenInformation();
			}
			
			if (getContext() != null) {
				Toast.makeText(getContext(), "인증 정보가 유효하지 않습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show();
			}
		}
		
		try {
			if (method != null) {
				method.invoke(receiver);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Context getContext() {
		if (receiver instanceof Fragment) {
			return ((Fragment) receiver).getActivity();
		}
		
		if (receiver instanceof Dialog) {
			return ((Dialog) receiver).getContext();
		}
		
		if (receiver instanceof Context) {
			return (Context) receiver;
		}
		
		return null;
	}

}
