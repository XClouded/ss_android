package com.myandb.singsong.net;

import java.util.HashMap;
import java.util.Map;

public abstract class HttpScheme {

	public final Map<String, String> getHeaders() {
		Map<String, String> headers = new HashMap<String, String>();
		return getHeaders(headers);
	}
	
	public abstract Map<String, String> getHeaders(Map<String, String> headers);
}
