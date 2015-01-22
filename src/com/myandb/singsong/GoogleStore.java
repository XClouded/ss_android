package com.myandb.singsong;

import android.net.Uri;

public class GoogleStore extends StoreBase {
	
	public GoogleStore(String packageName) {
		super(packageName);
	}
	
	@Override
	public Uri getDetailViewUri() {
		final String scheme = "market";
		final String authority = "details";
		final String key = "id";
		
		Uri.Builder builder = new Uri.Builder();
		return builder.scheme(scheme)
				.authority(authority)
				.appendQueryParameter(key, getAppPackageName())
				.build();
	}

	@Override
	public String getStorePackageName() {
		return "com.android.vending";
	}
	
}
