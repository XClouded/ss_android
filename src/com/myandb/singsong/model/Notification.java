package com.myandb.singsong.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.Spannable;

import com.myandb.singsong.util.StringFormatter;
import com.myandb.singsong.util.Utility;

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
	
	public List<CharSequence> getContent(User currentUser) {
		List<CharSequence> contents = new ArrayList<CharSequence>();
		
		if (activity != null) {
			try {
				final JSONObject metadata = activity.getMetadata();
				final User activityCreator = activity.getCreator();
				Spannable boldSpan = Utility.getBoldSpan(activityCreator.getNickname());
				contents.add(boldSpan);
				contents.add("��");
				
				if (activity.getSourceType() == Activity.TYPE_CREATE_COMMENT) {
					contents.add("�� ȸ������ �뷡�� ");
					
					if (count > 0) {
						contents.add(String.valueOf(count + 1) + "����");
					}
				} else {
					if (count > 0) {
						contents.add(" �� " + String.valueOf(count) + "����");
					} else {
						contents.add("�� ");
					}
				}
				
				switch (activity.getSourceType()) {
				case Activity.TYPE_CREATE_FRIENDSHIP:
					contents.add("ȸ������ �ȷο� �߽��ϴ�.");
					break;
					
				case Activity.TYPE_CREATE_ROOT_SONG:
					boldSpan = Utility.getBoldSpan(metadata.getString("music_singer"));
					contents.add(boldSpan);
					contents.add("�� ");
					boldSpan = Utility.getBoldSpan(metadata.getString("music_title"));
					contents.add(boldSpan);
					contents.add("�� �ҷ����ϴ�.");
					break;
					
				case Activity.TYPE_CREATE_LEAF_SONG:
					if (metadata.getInt("parent_user_id") == currentUser.getId()) {
						contents.add("ȸ������ �θ� ");
					}
					boldSpan = Utility.getBoldSpan(metadata.getString("music_singer"));
					contents.add(boldSpan);
					contents.add("�� ");
					boldSpan = Utility.getBoldSpan(metadata.getString("music_title"));
					contents.add(boldSpan);
					contents.add("�� �ݶ� �߽��ϴ�.");
					break;
					
				case Activity.TYPE_CREATE_COMMENT:
					contents.add("����� �޾ҽ��ϴ�.\n\"");
					contents.add(metadata.getString("comment_content") + "\"");
					break;
					
				case Activity.TYPE_CREATE_LIKING:
					contents.add("ȸ������ �뷡�� �����մϴ�.");
					break;
					
				case Activity.TYPE_RECOMMEND_ARTIST:
					contents.add("ȸ������ �ݶ� ��Ƽ��Ʈ�� ��õ�߽��ϴ�.");
					break;
					
				case Activity.TYPE_ADMIN_MESSAGE:
					contents.add(metadata.getString("body"));
					break;
					
				default:
					break;
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return contents;
	}
	
	public String getWorkedCreatedTime(Date currentDate) {
		return StringFormatter.getTimeLag(currentDate, updated_at);
	}
	
}
