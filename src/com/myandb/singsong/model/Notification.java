package com.myandb.singsong.model;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.myandb.singsong.util.TimeHelper;

public class Notification extends Model {

	private Activity activity;
	private int count;
	private Date updated_at;
	
	public Notification(Activity activity) {
		this.activity = activity;
	}
	
	public Activity getActivity() {
		return activity;
	}
	
	public String getContent(User currentUser) {
		String result = "";
		
		if (activity != null) {
			try {
				JSONObject metadata = activity.getMetadata();
				
				result += "님";
				
				if (activity.getSourceType() == Activity.TYPE_CREATE_COMMENT) {
					result += "이 회원님의 노래에 ";
					
					if (count > 0) {
						result += String.valueOf(count);
						result += "개의 ";
					}
				} else {
					if (count > 0) {
						result += " 외 ";
						result += String.valueOf(count);
						result += "명이 ";
					} else {
						result += "이 ";
					}
				}
				
				switch (activity.getSourceType()) {
				case Activity.TYPE_CREATE_FRIENDSHIP:
					result += "회원님을 팔로우 했습니다.";
					break;
					
				case Activity.TYPE_CREATE_ROOT_SONG:
					result += metadata.getString("music_singer");
					result += "의 ";
					result += metadata.getString("music_title");
					result += "를 불렀습니다.";
					break;
					
				case Activity.TYPE_CREATE_LEAF_SONG:
					if (metadata.getInt("parent_user_id") == currentUser.getId()) {
						result += "회원님이 부른 ";
					}
					result += metadata.getString("music_singer");
					result += "의 ";
					result += metadata.getString("music_title");
					result += "에 콜라보 했습니다.";
					break;
					
				case Activity.TYPE_CREATE_COMMENT:
					result += "댓글을 달았습니다.\n";
					result += "\"";
					result += metadata.getString("comment_content");
					result += "\"";
					break;
					
				case Activity.TYPE_CREATE_LIKING:
					result += "회원님의 노래를 좋아합니다.";
					break;
					
				case Activity.TYPE_RECOMMEND_ARTIST:
					result += "회원님을 콜라보 아티스트에 추천했습니다.";
					break;
					
				case Activity.TYPE_ADMIN_MESSAGE:
					result += metadata.getString("body");
					break;
					
				default:
					break;
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public String getWorkedCreatedTime(Date currentDate) {
		return TimeHelper.getTimeLag(currentDate, updated_at);
	}
	
}
