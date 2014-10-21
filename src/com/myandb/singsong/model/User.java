package com.myandb.singsong.model;

import java.util.Date;

public class User extends Model {
	
	private String username;
	private String nickname;
	private String main_photo_url;
	private Date main_photo_updated_at;
	private Profile profile;
	private int is_activated;
	private int is_following;
	
	public String getUsername() {
		return toString(username);
	}
	
	public String getCroppedUsername() {
		return username.split("@")[0] + "@";
	}
	
	public void setNickname(String nickname) {
		if (!"".equals(nickname)) {
			this.nickname = nickname;
		}
	}
	
	public String getNickname() {
		return toString(nickname);
	}
	
	public String getPhotoUrl() {
		return toString(main_photo_url);
	}
	
	public void setPhotoUrl(String url, Date updatedAt) {
		this.main_photo_url = url;
		this.main_photo_updated_at = updatedAt;
	}
	
	public boolean hasPhoto() {
		return main_photo_url != null && !main_photo_url.isEmpty();
	}
	
	public Date getPhotoUpdatedAt() {
		return main_photo_updated_at;
	}
	
	public Profile getProfile() {
		return profile;
	}
	
	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	
	public boolean isActivated() {
		return is_activated == 1;
	}
	
	public void setActivated() {
		is_activated = 1;
	}
	
	public boolean isFollowing() {
		return is_following == 1;
	}
	
}
