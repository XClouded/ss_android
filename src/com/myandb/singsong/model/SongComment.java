package com.myandb.singsong.model;

import java.util.Date;

public class SongComment extends Comment<Song> {

	public SongComment(User writer, String content, Date createdAt) {
		super(writer, content, createdAt);
	}

}
