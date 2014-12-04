package com.myandb.singsong.net;

import org.json.JSONObject;

import android.net.Uri;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

public class JustRequest extends OAuthJSONRequest<String> {
	
	public JustRequest(int method, String segment, JSONObject jsonRequest) {
		super(
			method,
			new UrlBuilder().s(segment).toString(),
			jsonRequest == null ? null : jsonRequest.toString(),
			null,
			null
		);
	}
	
	public JustRequest(int method, Uri uri, JSONObject jsonRequest) {
		super(
			method,
			uri.toString(),
			jsonRequest == null ? null : jsonRequest.toString(),
			null,
			null
		);
	}
	
    public JustRequest(String segment, JSONObject jsonRequest) {
        this(
        	jsonRequest == null ? Method.GET : Method.POST,
        	segment,
        	jsonRequest
        );
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
