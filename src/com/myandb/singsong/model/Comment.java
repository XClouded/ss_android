package com.myandb.singsong.model;

import java.util.Date;

public class Comment<T> extends Model {
	
	private String content;
	private User user;
	private T commentable;
	
	public Comment(User writer, String content, Date createdAt) {
		this.user = writer;
		this.content = content;
		this.created_at = createdAt;
	}
	
	public String getContent() {
		return safeString(content);
	}
	
	public User getWriter() {
		return user;
	}
	
	public T getCommentable() {
		return commentable;
	}
	
}
