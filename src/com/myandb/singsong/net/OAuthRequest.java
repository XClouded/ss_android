package com.myandb.singsong.net;

import java.util.Map;

import org.apache.http.HttpStatus;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;

public abstract class OAuthRequest<T> extends Request<T> {
	
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
		HttpScheme melonHttpHeaderScheme = new MelonHttpScheme();
		Map<String, String> headers = melonHttpHeaderScheme.getHeaders();
		
		if (requireAccessToken) {
			HttpScheme singSongHttpHeaderScheme = new SingSongHttpScheme();
			headers = singSongHttpHeaderScheme.getHeaders(headers);
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
