package com.myandb.singsong.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Team extends Model {

	private User leader;
	private String name;
	private String description;
	private String main_photo_url;
	private String emblem_url;
	private Date main_photo_updated_at;
	private Date emblem_updated_at;
	private Category category;
	private List<Member> members;
	private int leader_id;
	private int max_member_num;
	private int member_num;
	private int published_song_num;
	private int song_in_progress_num;
	private int follower_num;
	private int genre_id;
	private int gender_type;
	
	public User getLeader() {
		return leader;
	}
	
	public String getName() {
		return safeString(name);
	}
	
	public String getDescription() {
		return safeString(description);
	}
	
	public String getBackgroundPhotoUrl() {
		return safeString(main_photo_url);
	}
	
	public String getEmblemPhotoUrl() {
		return safeString(emblem_url);
	}
	
	public Date getBackgroundPhotoUpdatedAt() {
		return main_photo_updated_at;
	}
	
	public Date getEmblemPhotoUpdatedAt() {
		return emblem_updated_at;
	}
	
	public boolean isLeader(User user) {
		if (leader != null) {
			return user.getId() == leader.getId();
		}
		return user.getId() == leader_id;
	}
	
	public int getMaxMemberNum() {
		return max_member_num;
	}
	
	public String getWorkedMaxMemberNum() {
		return safeString(max_member_num);
	}
	
	public int getMemberNum() {
		return member_num;
	}
	
	public String getWorkedMemberNum() {
		return safeString(member_num);
	}
	
	public int getPublishedSongNum() {
		return published_song_num;
	}
	
	public String getWorkedPublishedSongNum() {
		return safeString(published_song_num);
	}
	
	public int getSongInProgressNum() {
		return song_in_progress_num;
	}
	
	public String getWorkedSongInProgressNum() {
		return safeString(song_in_progress_num);
	}
	
	public int getFollowersNum() {
		return follower_num;
	}
	
	public String getWorkedFollowersNum() {
		return safeString(follower_num);
	}
	
	public Category getCategory() {
		if (category == null) {
			category = new Category(genre_id);
		}
		return category;
	}
	
	public Gender getGender() {
		return Gender.BOYS;
	}
	
	public List<Member> getMembers() {
		if (members == null) {
			members = new ArrayList<Member>();
		}
		return members;
	}
}
