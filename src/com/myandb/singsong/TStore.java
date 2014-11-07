package com.myandb.singsong;

import android.net.Uri;

public class TStore implements Store {

	@Override
	public Uri getDetailViewUri(String packageName) {
		final String scheme = "tstore";
		final String authority = "PRODUCT_VIEW";
		final String id = "10000429";
		final String ageGroup = "0";
		
		Uri.Builder builder = new Uri.Builder();
		return builder.scheme(scheme).authority(authority).appendPath(id).appendPath(ageGroup).build();
	}

	@Override
	public String getPackageName() {
		return "com.skt.skaf.A000Z00040";
	}
	
}
