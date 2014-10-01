package com.myandb.singsong.net;

import org.json.JSONObject;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

public class OAuthJustRequest extends OAuthJsonRequest<String> {
	
	/**
	 * Constructor which defaults to <code>POST</code>
	 * or <code>GET</code> if {@link JSONObject} is null.
	 * 
	 * @see #SimpleJsonRequest(int, String, JSONObject, Listener, ErrorListener)
	 * 
	 */
	public OAuthJustRequest(String url, JSONObject jsonRequest) {
		this((jsonRequest == null) ? Method.GET : Method.POST, url, jsonRequest);
	}
	
	/**
     * Creates a new request.
     * 
     * @param method the HTTP method to use
     * @param url URL to fetch the JSON from
     * @param jsonRequest A {@link JSONObject} to post with the request. Null is allowed and
     *   indicates no parameters will be posted along with request.
     */
	public OAuthJustRequest(int method, String url, JSONObject jsonRequest) {
		super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), null, null);
	}

	/**
	 * there is no response listener since we
	 * assign it to null at super constructor
	 */
	@Override
	protected void deliverResponse(String response) {}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		return Response.success(new String(""),
                HttpHeaderParser.parseCacheHeaders(response));
	}

}
