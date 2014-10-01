package com.myandb.singsong.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Notification extends Model {

	private Activity activity;
	
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
					result += "회원님의 노래에 댓글을 달았습니다.\n";
					result += "\"";
					result += metadata.getString("comment_content");
					result += "\"";
					break;
					
				case Activity.TYPE_CREATE_LIKING:
					result += "회원님의 노래를 좋아합니다.";
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

	public int getSourceId() {
		try {
			switch(activity.getSourceType()) {
			case Activity.TYPE_CREATE_FRIENDSHIP:
				return activity.getUserId();
				
			case Activity.TYPE_CREATE_COMMENT:
				return activity.getMetadata().getInt("commentable_id");
				
			case Activity.TYPE_CREATE_LIKING:
				return activity.getMetadata().getInt("likeable_id");
				
			default:
				return activity.getSourceId();
			}
		} catch (JSONException e) {
			return activity.getSourceId();
		}
	}
	
}
