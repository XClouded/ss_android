package com.myandb.singsong;

import android.net.Uri;

public class NaverStore extends StoreBase {

	public NaverStore(String packageName) {
		super(packageName);
	}
	
	@Override
	public Uri getDetailViewUri() {
		final String scheme = "appstore";
		final String authority = "store";
		final String key = "packageName";
		
		Uri.Builder builder = new Uri.Builder();
		return builder.scheme(scheme)
				.authority(authority)
				.appendQueryParameter(key, getAppPackageName())
				.build();
	}

	@Override
	public String getStorePackageName() {
		return "com.nhn.android.nstore";
	}

}
