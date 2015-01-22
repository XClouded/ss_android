package com.myandb.singsong;

import android.net.Uri;

public class TStore extends StoreBase {
	
	private String id;

	public TStore(String id) {
		super(id);
	}

	@Override
	public Uri getDetailViewUri() {
		final String scheme = "tstore";
		final String authority = "PRODUCT_VIEW";
		final String ageGroup = "0";
		
		Uri.Builder builder = new Uri.Builder();
		return builder.scheme(scheme)
				.authority(authority)
				.appendPath(id)
				.appendPath(ageGroup)
				.build();
	}

	@Override
	public String getStorePackageName() {
		return "com.skt.skaf.A000Z00040";
	}
	
}
