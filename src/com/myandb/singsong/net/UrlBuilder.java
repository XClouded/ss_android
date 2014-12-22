package com.myandb.singsong.net;

import com.myandb.singsong.App;

import android.net.Uri;
import android.os.Bundle;

public class UrlBuilder {

	private static final String API_SCHEME = "http";
	private static final String API_DOMAIN = "14.63.171.91:8880";
	private static final String API_DOMAIN_TEST = "14.63.164.15";
	private static final String API_PATH = "/ss_api/public";
	private static final String API_AUTHORITY = (App.TESTING ? API_DOMAIN_TEST : API_DOMAIN) + API_PATH;
	
	private Uri.Builder builder;
	
	public UrlBuilder() {
		builder = new Uri.Builder();
		builder.scheme(API_SCHEME);
		builder.encodedAuthority(API_AUTHORITY);
	}
	
	public UrlBuilder s(String segment) {
		builder.appendEncodedPath(segment);
		return this;
	}
	
	public UrlBuilder s(int segment) {
		s(String.valueOf(segment));
		return this;
	}
	
	public UrlBuilder p(String key, String value) {
		builder.appendQueryParameter(key, value);
		return this;
	}
	
	public UrlBuilder p(String key, int value) {
		p(key, String.valueOf(value));
		return this;
	}
	
	public UrlBuilder p(Bundle map) {
		for (String key : map.keySet()) {
			p(key, map.getString(key));
		}
		return this;
	}
	
	public String getParam(String key) {
		Uri uri = builder.build();
		return uri.getQueryParameter(key);
	}
	
	public UrlBuilder skip(int amount) {
		p("skip", String.valueOf(amount));
		return this;
	}
	
	public UrlBuilder take(int amount) {
		p("take", String.valueOf(amount));
		return this;
	}
	
	public UrlBuilder keyword(String keyword) {
		p("q", keyword);
		return this;
	}
	
	public UrlBuilder start(String date) {
		p("start", date);
		return this;
	}
	
	public UrlBuilder end(String date) {
		p("end", date);
		return this;
	}
	
	public Uri build() {
		return builder.build();
	}
	
	@Override
	public String toString() {
		return builder.build().toString();
	}
	
}
