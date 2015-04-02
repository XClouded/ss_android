package com.myandb.singsong;

public enum ApplicationMode {
	
	MASTER("https", "www.myandb.com"),
	
	DEBUGGING("http", "14.63.164.15");
	
	private String scheme;
	private String domain;
	
	ApplicationMode(String scheme, String domain) {
		this.scheme = scheme;
		this.domain = domain;
	}
	
	public String getScheme() {
		return scheme;
	}
	
	public String getDomain() {
		return domain;
	}

}
