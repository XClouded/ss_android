package com.myandb.singsong;

import android.net.Uri;

public class GoogleStore implements Store {
	
	public Uri getDetailViewUri(String packageName) {
		final String scheme = "market";
		final String authority = "details";
		final String key = "id";
		
		Uri.Builder builder = new Uri.Builder();
		return builder.scheme(scheme).authority(authority).appendQueryParameter(key, packageName).build();
	}

	@Override
	public String getPackageName() {
		return "com.android.vending";
	}
	
}
