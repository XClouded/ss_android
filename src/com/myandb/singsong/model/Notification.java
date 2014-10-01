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
					result += "ȸ������ �ȷο� �߽��ϴ�.";
					break;
					
				case Activity.TYPE_CREATE_ROOT_SONG:
					result += metadata.getString("music_singer");
					result += "�� ";
					result += metadata.getString("music_title");
					result += "�� �ҷ����ϴ�.";
					break;
					
				case Activity.TYPE_CREATE_LEAF_SONG:
					if (metadata.getInt("parent_user_id") == currentUser.getId()) {
						result += "ȸ������ �θ� ";
					}
					result += metadata.getString("music_singer");
					result += "�� ";
					result += metadata.getString("music_title");
					result += "�� �ݶ� �߽��ϴ�.";
					break;
					
				case Activity.TYPE_CREATE_COMMENT:
					result += "ȸ������ �뷡�� ����� �޾ҽ��ϴ�.\n";
					result += "\"";
					result += metadata.getString("comment_content");
					result += "\"";
					break;
					
				case Activity.TYPE_CREATE_LIKING:
					result += "ȸ������ �뷡�� �����մϴ�.";
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
