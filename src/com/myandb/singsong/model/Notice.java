package com.myandb.singsong.model;

import java.util.Date;

public class Notice extends Model {

	private String title;
	private String content;
	private String front_img;
	private Date started_at;
	private Date finish_at;
	
	public String getTitle() {
		return toString(title);
	}
	
	public String getContent() {
		return toString(content);
	}
	
	public String getFrontImageUrl() {
		return toString(front_img);
	}
	
	public Date getStartedTime() {
		return started_at;
	}
	
	public Date getFinishTime() {
		return finish_at;
	}
	
}
