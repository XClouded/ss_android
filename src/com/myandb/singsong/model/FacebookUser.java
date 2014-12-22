package com.myandb.singsong.model;

import com.facebook.model.GraphUser;

public class FacebookUser extends Model {
	
	private String facebookId;
	private String name;
	private String photoUrl;
	
	public FacebookUser(GraphUser graphUser) {
		this.facebookId = graphUser.getId();
		this.name = graphUser.getName();
		this.photoUrl = "http://graph.facebook.com/" + facebookId + "/picture?type=small";
	}
	
	public String getFacebookId() {
		return safeString(facebookId);
	}
	
	public String getName() {
		return safeString(name);
	}
	
	public String getPhotoUrl() {
		return safeString(photoUrl);
	}

}
