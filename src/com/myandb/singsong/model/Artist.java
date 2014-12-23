package com.myandb.singsong.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Artist extends Model {
	
	private User user;
	private String nickname;
	private String introduction;
	private String qnas;
	private List<Song> songs;
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
			JSONArray qnaJson = new JSONArray(qnas);
			List<Qna> qnaList = new ArrayList<Artist.Qna>();
			for (int i = 0, l = qnaJson.length(); i < l; i++) {
				JSONObject qna = qnaJson.getJSONObject(i);
				qnaList.add(new Qna(user, qna.getString("q"), qna.getString("a")));
			}
			return qnaList;
		} catch (JSONException e) {
			e.printStackTrace();
			return new ArrayList<Artist.Qna>();
		}
	}
	
	public List<Song> getSongs() {
		return songs;
	}
	
	public String getCommentNum() {
		return safeString(comment_num);
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
