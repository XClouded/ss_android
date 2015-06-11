package com.myandb.singsong.net;

import java.io.UnsupportedEncodingException;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

public class StringRequest extends OAuthJSONRequest<String> {

	public StringRequest(int method, String url,
			Listener<String> listener, ErrorListener errorListener) {
		super(method, url, null, listener, errorListener);
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		try {
			String result = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
	}

}
