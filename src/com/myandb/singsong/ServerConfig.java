package com.myandb.singsong;

public enum ServerConfig {
	
	PRODUCT_HTTP("http", "www.myandb.com:8880", "/ss_api/public"),
	
	PRODUCT_HTTPS("https", "www.myandb.com", "/ss_api/public"),
	
	TEST("http", "14.63.164.15", "/ss_api/public");
	
	private String scheme;
	private String domain;
	private String relativeDocumentRoot;
	
	ServerConfig(String scheme, String domain) {
		this(scheme, domain, "");
	}
	
	ServerConfig(String scheme, String domain, String root) {
		this.scheme = scheme;
		this.domain = domain;
		this.relativeDocumentRoot = root;
	}
	
	public String getScheme() {
		return scheme;
	}
	
	public String getDomain() {
		return domain;
	}
	
	public String getRelativeDocumentRoot() {
		return relativeDocumentRoot;
	}
	
	public String getDocumentRoot() {
		return domain + relativeDocumentRoot;
	}

}
