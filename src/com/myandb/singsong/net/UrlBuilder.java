package com.myandb.singsong.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.myandb.singsong.App;
import com.myandb.singsong.ServerConfig;

import android.net.Uri;
import android.os.Bundle;

public class UrlBuilder {
	
	private List<String> segments;
	private Map<String, String> parameters;
	private ServerConfig config;
	
	public UrlBuilder() {
		this(App.SERVER_CONFIG);
	}
	
	public UrlBuilder(ServerConfig config) {
		this.config = config != null ? config : App.SERVER_CONFIG;
		this.segments = new ArrayList<String>();
		this.parameters = new HashMap<String, String>();
	}
	
	public UrlBuilder s(String segment) {
		segments.add(segment);
		return this;
	}
	
	public UrlBuilder s(int segment) {
		s(String.valueOf(segment));
		return this;
	}
	
	public UrlBuilder p(String key, String value) {
		parameters.put(key, value);
		return this;
	}
	
	public UrlBuilder p(String key, int value) {
		p(key, String.valueOf(value));
		return this;
	}
	
	public UrlBuilder p(Bundle map) {
		if (map != null && map.size() > 0) {
			for (String key : map.keySet()) {
				p(key, map.getString(key));
			}
		}
		return this;
	}
	
	public String getParam(String key) {
		return parameters.get(key);
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
		Uri.Builder builder = new Uri.Builder();
		builder.scheme(config.getScheme());
		builder.encodedAuthority(config.getDocumentRoot());
		
		for (String segment : segments) {
			builder.appendEncodedPath(segment);
		}
		
		for (String key : parameters.keySet()) {
			builder.appendQueryParameter(key, parameters.get(key));
		}
		
		return builder.build();
	}
	
	@Override
	public String toString() {
		return build().toString();
	}
	
}
