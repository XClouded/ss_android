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
				
				result += "��";
				
				if (activity.getSourceType() == Activity.TYPE_CREATE_COMMENT) {
					result += "�� ȸ������ �뷡�� ";
					
					if (count > 0) {
						result += String.valueOf(count);
						result += "���� ";
					}
				} else {
					if (count > 0) {
						result += " �� ";
						result += String.valueOf(count);
						result += "���� ";
					} else {
						result += "�� ";
					}
				}
				
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
					result += "����� �޾ҽ��ϴ�.\n";
					result += "\"";
					result += metadata.getString("comment_content");
					result += "\"";
					break;
					
				case Activity.TYPE_CREATE_LIKING:
					result += "ȸ������ �뷡�� �����մϴ�.";
					break;
					
				case Activity.TYPE_RECOMMEND_ARTIST:
					result += "ȸ������ �ݶ� ��Ƽ��Ʈ�� ��õ�߽��ϴ�.";
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
