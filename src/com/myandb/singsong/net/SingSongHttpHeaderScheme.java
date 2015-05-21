package com.myandb.singsong.net;

import java.util.Map;

import com.myandb.singsong.secure.Authenticator;

public class SingSongHttpHeaderScheme extends HttpHeaderScheme {

	@Override
	public Map<String, String> getHeaders(Map<String, String> headers) {
		headers.put("oauth-token", Authenticator.getAccessToken());
		return headers;
	}

}
