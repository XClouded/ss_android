package com.myandb.singsong.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.fragment.ArtistDetailFragment;
import com.myandb.singsong.fragment.BaseFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class Artist extends Model {
	
	private User user;
	private String nickname;
	private String introduction;
	private String to_users;
	private int comment_num;
	
	public User getUser() {
		return user;
	}
	
	public String getNickname() {
		return safeString(nickname);
	}

	public String getIntroduction() {
		return safeString(introduction);
	}
	
	public List<Qna> getQna() {
		try {
			JSONArray qnaJson = new JSONArray(to_users);
			List<Qna> qnaList = new ArrayList<Artist.Qna>();
			for (int i = 0, l = qnaJson.length(); i < l; i++) {
				JSONObject qna = qnaJson.getJSONObject(i);
				qnaList.add(new Qna(user, qna.getString("q"), qna.getString("a")));
			}
			return qnaList;
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Artist.Qna>();
		}
	}
	
	public String getCommentNum() {
		return safeString(comment_num);
	}
	
	public OnClickListener getArtistClickListener() {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				BaseActivity activity = (BaseActivity) v.getContext();
				Bundle bundle = new Bundle();
				bundle.putString(ArtistDetailFragment.EXTRA_ARTIST, Artist.this.toString());
				bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, getNickname());
				Intent intent = new Intent(activity, RootActivity.class);
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, ArtistDetailFragment.class.getName());
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
				activity.changePage(intent);
			}
		};
	}
	
	public static final class Qna {
		
		private String question;
		private String answer;
		private User user;
		
		public Qna(User user, String question, String answer) {
			this.user = user;
			this.question = question;
			this.answer = answer;
		}
		
		public String getQuestion() {
			return question;
		}
		
		public String getAnswer() {
			return answer;
		}
		
		public User getUser() {
			return user;
		}
		
	}
	
}
