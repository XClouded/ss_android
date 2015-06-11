package com.myandb.singsong.net;

import java.lang.reflect.Method;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.myandb.singsong.R;
import com.myandb.singsong.secure.Authenticator;

public class JSONErrorListener implements ErrorListener {
	
	private Object receiver;
	private Method method;
	
	public JSONErrorListener() {
		this(null, null);
	}
	
	public JSONErrorListener(Object receiver, String methodName) {
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
			new Authenticator().logout(getContext());
			
			if (getContext() != null) {
				Toast.makeText(getContext(), getContext().getString(R.string.t_critical_invalid_token), Toast.LENGTH_SHORT).show();
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
		
		if (receiver instanceof Context) {
			return (Context) receiver;
		}
		
		if (receiver instanceof View) {
			return ((View) receiver).getContext();
		}
		
		return null;
	}

}
