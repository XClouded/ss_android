package com.myandb.singsong.net;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class UrlBuilder {
	
	public static final String BASE_URL = "http://14.63.171.91:8880/ss_api/public/";
//	public static final String BASE_URL = "http://1.235.248.120:5556/ss/public/index.php/";
	
	private static UrlBuilder singleton;
	private ArrayList<String> locations;
	private HashMap<String, String> queryPararmeters;
	
	private UrlBuilder() {
		initialize();
	}
	
	public static UrlBuilder getInstance() {
		if (singleton == null) {
			singleton = UrlBuilder.create();
		} else {
			singleton.initialize();
		}
		
		return singleton;
	}
	
	public static UrlBuilder create() {
		return new UrlBuilder();
	}
	
	private void initialize() {
		if (locations == null) {
			locations = new ArrayList<String>();
		} else {
			locations.clear();
		}
		
		if (queryPararmeters == null) {
			queryPararmeters = new HashMap<String, String>();
		} else {
			queryPararmeters.clear();
		}
	}
	
	public UrlBuilder l(String location) {
		locations.add(location);
		
		return this;
	}
	
	public UrlBuilder l(int location) {
		locations.add(String.valueOf(location));
		
		return this;
	}
	
	public UrlBuilder q(String key, String value) {
		queryPararmeters.put(key, value);
		
		return this;
	}
	
	public UrlBuilder q(String key, int value) {
		queryPararmeters.put(key, String.valueOf(value));
		
		return this;
	}
	
	public UrlBuilder removeParam(String key) {
		queryPararmeters.remove(key);
		
		return this;
	}
	
	public boolean hasParam(String key) {
		return queryPararmeters.containsKey(key);
	}
	
	public String getParam(String key) {
		return queryPararmeters.get(key);
	}
	
	public UrlBuilder skip(int amount) {
		q("skip", String.valueOf(amount));
		
		return this;
	}
	
	public UrlBuilder take(int amount) {
		q("take", String.valueOf(amount));
		
		return this;
	}
	
	public UrlBuilder keyword(String keyword) {
		q("q", keyword);
		
		return this;
	}
	
	public UrlBuilder start(String date) {
		q("start", date);
		
		return this;
	}
	
	public UrlBuilder end(String date) {
		q("end", date);
		
		return this;
	}
	
	public String build() {
		return build(false);
	}
	
	public String build(boolean isReusable) {
		String result = BASE_URL;
		
		if (!isEndedWithSlash(result)) {
			result += "/";
		}
		
		for (int i = 0, l = locations.size(); i < l; i++) {
			result += locations.get(i);
			if (i < l - 1) {
				result += "/";
			}
		}
		
		int length = queryPararmeters.size();
		if (length > 0) {
			result += "?";
		}
		
		int i = 0;
		for (Entry<String, String> queryParameter : queryPararmeters.entrySet()) {
			result += urlEncode(queryParameter.getKey());
			result += "=";
			result += urlEncode(queryParameter.getValue());
			
			if (i < length - 1) {
				result += "&";
			}
			
			i++;
		}
		
		if (!isReusable) {
			initialize();
		}
		
		return result;
	}
	
	private boolean isEndedWithSlash(String string) {
		return string.charAt(string.length() - 1) == '/';
	}
	
	private String urlEncode(String string) {
		try {
			return URLEncoder.encode(string, "UTF-8").replace("+", "%20").replace("[", "%5B").replace("]", "%5D");
		} catch (UnsupportedEncodingException e) {
			return string;
		}
	}
	
}
