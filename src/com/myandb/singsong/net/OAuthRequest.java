package com.myandb.singsong.net;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.util.Logger;

public abstract class OAuthRequest<T> extends Request<T> {
	
	private boolean requireAccessToken;
	
	public OAuthRequest(int method, String url, ErrorListener listener) {
		super(method, url, listener);
		Logger.log(url);
		requireAccessToken = true;
	}
	
	public void setRequireAccessToken(boolean require) {
		this.requireAccessToken = require;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = new HashMap<String, String>();
		
		if (requireAccessToken) {
			headers.put("oauth-token", Authenticator.getAccessToken());
		}
		
		return headers;
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
