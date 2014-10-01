package com.myandb.singsong.model;

public class Image extends Model {
	
	private String url;
	
	public String getUrl() {
		return toString(url);
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

}
