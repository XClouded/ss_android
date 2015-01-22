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
				contents.add("님");
				
				if (activity.getSourceType() == Activity.TYPE_CREATE_COMMENT) {
					contents.add("이 회원님의 노래에 ");
					
					if (count > 0) {
						contents.add(String.valueOf(count + 1) + "개의");
					}
				} else {
					if (count > 0) {
						contents.add(" 외 " + String.valueOf(count) + "명이");
					} else {
						contents.add("이 ");
					}
				}
				
				switch (activity.getSourceType()) {
				case Activity.TYPE_CREATE_FRIENDSHIP:
					contents.add("회원님을 팔로우 했습니다.");
					break;
					
				case Activity.TYPE_CREATE_ROOT_SONG:
					boldSpan = Utility.getBoldSpan(metadata.getString("music_singer"));
					contents.add(boldSpan);
					contents.add("의 ");
					boldSpan = Utility.getBoldSpan(metadata.getString("music_title"));
					contents.add(boldSpan);
					contents.add("를 불렀습니다.");
					break;
					
				case Activity.TYPE_CREATE_LEAF_SONG:
					if (metadata.getInt("parent_user_id") == currentUser.getId()) {
						contents.add("회원님이 부른 ");
					}
					boldSpan = Utility.getBoldSpan(metadata.getString("music_singer"));
					contents.add(boldSpan);
					contents.add("의 ");
					boldSpan = Utility.getBoldSpan(metadata.getString("music_title"));
					contents.add(boldSpan);
					contents.add("에 콜라보 했습니다.");
					break;
					
				case Activity.TYPE_CREATE_COMMENT:
					contents.add("댓글을 달았습니다.\n\"");
					contents.add(metadata.getString("comment_content") + "\"");
					break;
					
				case Activity.TYPE_CREATE_LIKING:
					contents.add("회원님의 노래를 좋아합니다.");
					break;
					
				case Activity.TYPE_RECOMMEND_ARTIST:
					contents.add("회원님을 콜라보 아티스트에 추천했습니다.");
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
