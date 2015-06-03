package com.myandb.singsong.net;

import java.util.Map;

import android.os.Build;

import com.myandb.singsong.App;
import com.myandb.singsong.secure.Authenticator;

public class MelonHttpHeaderScheme extends HttpHeaderScheme {
	
	public static final String POC_CODE = "AS45";
	
	private static final String BUILD_VERSION = "Android " + android.os.Build.VERSION.RELEASE;

	@Override
	public Map<String, String> getHeaders(Map<String, String> headers) {
		headers.put("User-Agent", getUserAgent());
		headers.put("Accept-Charset", "utf-8");
		headers.put("Accept-Encoding", "gzip,deflate");
		headers.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		headers.put("cookie", "PCID=" + Authenticator.getDeviceUuid() + ";");
		return headers;
	}
	
	private String getUserAgent() {
		return POC_CODE + "; "
				+ BUILD_VERSION + "; "
				+ App.APP_VERSION + "; "
				+ Build.MODEL;
	}


}
