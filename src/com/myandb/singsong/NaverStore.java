package com.myandb.singsong;

import android.net.Uri;

public class NaverStore implements Store {

	@Override
	public Uri getDetailViewUri(String packageName) {
		final String scheme = "appstore";
		final String authority = "store";
		final String key = "packageName";
		
		Uri.Builder builder = new Uri.Builder();
		return builder.scheme(scheme).authority(authority).appendQueryParameter(key, packageName).build();
	}

	@Override
	public String getPackageName() {
		return "com.nhn.android.nstore";
	}

}
