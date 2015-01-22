package com.myandb.singsong.net;

import com.android.volley.Request;
import com.android.volley.RequestQueue.RequestFilter;

public class SelectAllRequestFilter implements RequestFilter {

	@Override
	public boolean apply(Request<?> request) {
		return true;
	}

}
