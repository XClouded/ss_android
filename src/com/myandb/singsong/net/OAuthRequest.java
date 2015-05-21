package com.myandb.singsong.net;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;

import android.os.Build;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.myandb.singsong.App;
import com.myandb.singsong.secure.Authenticator;

public abstract class OAuthRequest<T> extends Request<T> {
	
	private static final String POC_CODE = "AS45";
	private static final String BUILD_VERSION = "Android " + android.os.Build.VERSION.RELEASE;
	
	private boolean requireAccessToken;
	
	public OAuthRequest(int method, String url, ErrorListener listener) {
		super(method, url, listener);
		requireAccessToken = true;
	}
	
	public void setRequireAccessToken(boolean require) {
		this.requireAccessToken = require;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("User-Agent", getUserAgent());
		headers.put("PCID", Authenticator.getDeviceUuid());
		
		if (requireAccessToken) {
			headers.put("oauth-token", Authenticator.getAccessToken());
		}
		
		return headers;
	}
	
	private String getUserAgent() {
		return POC_CODE + ";"
				+ BUILD_VERSION + ";"
				+ App.APP_VERSION + ";"
				+ Build.MODEL;
	}

	@Override
	protected VolleyError parseNetworkError(VolleyError volleyError) {
		NetworkResponse response = volleyError.networkResponse;
		
		if (response != null && response.statusCode == HttpStatus.SC_FORBIDDEN) {
			return new AuthFailureError();
		}
		
		return super.parseNetworkError(volleyError);
	}

}
