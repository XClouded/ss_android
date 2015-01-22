package com.myandb.singsong.model;

public class Image extends Model {
	
	private String url;
	
	public String getUrl() {
		return safeString(url);
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public static String generateName(User user) {
		String result = "";
		
		result += user.getUsername();
		result += "_";
		result += String.valueOf(System.currentTimeMillis());
		result += Model.SUFFIX_JPG;
		
		return result;
	}

}
