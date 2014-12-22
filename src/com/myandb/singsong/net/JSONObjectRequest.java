package com.myandb.singsong.net;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

public class JSONObjectRequest extends OAuthJSONRequest<JSONObject> {

	public JSONObjectRequest(int method, String segment, JSONObject jsonRequest,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		super(
			method,
			new UrlBuilder().s(segment).toString(),
			jsonRequest == null ? null : jsonRequest.toString(),
			listener,
			errorListener
		);
	}
	
	public JSONObjectRequest(int method, Uri uri, JSONObject jsonRequest,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		super(
			method,
			uri.toString(),
			jsonRequest == null ? null : jsonRequest.toString(),
			listener,
			errorListener
		);
	}
	
    public JSONObjectRequest(String segment, JSONObject jsonRequest,
    		Listener<JSONObject> listener, ErrorListener errorListener) {
        this(
        	jsonRequest == null ? Method.GET : Method.POST,
        	segment,
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
