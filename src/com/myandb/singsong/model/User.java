package com.myandb.singsong.model;

import java.util.Date;

import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.fragment.UserHomeFragment;
import com.myandb.singsong.secure.Authenticator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class User extends Model {
	
	private String username;
	private String nickname;
	private String main_photo_url;
	private Date main_photo_updated_at;
	private Profile profile;
	private String facebook_id;
	private FacebookUser facebookUser;
	private int is_activated;
	private int is_following;
	
	public String getUsername() {
		return safeString(username);
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
		return safeString(nickname);
	}
	
	public String getPhotoUrl() {
		return safeString(main_photo_url);
	}
	
	public FacebookUser getFacebookUser() {
		return facebookUser;
	}
	
	public void setFacebookUser(FacebookUser facebookUser) {
		this.facebookUser = facebookUser;
	}
	
	public void setPhotoUrl(String url, Date updatedAt) {
		this.main_photo_url = url;
		this.main_photo_updated_at = updatedAt;
	}
	
	public boolean hasPhoto() {
		return !safeString(main_photo_url).isEmpty();
	}
	
	public Date getPhotoUpdatedAt() {
		return main_photo_updated_at;
	}
	
	public Profile getProfile() {
		return profile;
	}
	
	public String getFacebookId() {
		return safeString(facebook_id);
	}
	
	public boolean isFacebookActivated() {
		return getUsername().matches("FB_.*") || !getFacebookId().isEmpty();
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
	
	public boolean isLoggedInUser() {
		return Authenticator.getUser().equals(getId());
	}
	
	public OnClickListener getProfileClickListener() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (isLoggedInUser()) {
					return;
				}
				
				BaseActivity activity = (BaseActivity) v.getContext();
				Bundle bundle = new Bundle();
				bundle.putString(UserHomeFragment.EXTRA_THIS_USER, User.this.toString());
				Intent intent = new Intent(activity, RootActivity.class);
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, UserHomeFragment.class.getName());
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
				activity.changePage(intent);
			}
		};
	}
	
}
