package com.myandb.singsong.net;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Bundle;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

public class JSONObjectRequest extends OAuthJSONRequest<JSONObject> {

	public JSONObjectRequest(int method, String segment, Bundle params, JSONObject jsonRequest,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		super(
			method,
			new UrlBuilder().s(segment).p(params).toString(),
			jsonRequest,
			listener,
			errorListener
		);
	}
	
	public JSONObjectRequest(int method, Uri uri, JSONObject jsonRequest,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		super(
			method,
			uri.toString(),
			jsonRequest,
			listener,
			errorListener
		);
	}
	
    public JSONObjectRequest(String segment, Bundle params, JSONObject jsonRequest,
    		Listener<JSONObject> listener, ErrorListener errorListener) {
        this(
        	jsonRequest == null ? Method.GET : Method.POST,
        	segment,
        	params,
        	jsonRequest,
        	listener,
        	errorListener
        );
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString =
                new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
}
