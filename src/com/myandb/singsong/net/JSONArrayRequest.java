package com.myandb.singsong.net;

import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONException;

import android.net.Uri;
import android.os.Bundle;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

public class JSONArrayRequest extends OAuthJSONRequest<JSONArray> {

    /**
     * Creates a new request.
     * @param segment URL to fetch the JSON from
     * @param listener Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public JSONArrayRequest(String segment, Bundle params, Listener<JSONArray> listener, ErrorListener errorListener) {
        super(
        	Method.GET,
        	new UrlBuilder().s(segment).p(params).toString(),
        	null,
        	listener,
        	errorListener
    	);
    }
    
    public JSONArrayRequest(Uri uri, Listener<JSONArray> listener, ErrorListener errorListener) {
    	super(
    		Method.GET,
    		uri.toString(),
    		null,
    		listener,
    		errorListener
    	);
	}

    @Override
    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString =
                new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONArray(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
}
