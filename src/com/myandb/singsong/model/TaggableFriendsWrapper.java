package com.myandb.singsong.model;

import java.util.List;

public class TaggableFriendsWrapper {
	
	private List<TaggableFriend> data;
	private Paging paging;

	public List<TaggableFriend> getData() {
	    return data;
	}

	public void setData(List<TaggableFriend> data) {
	    this.data = data;
	}

	public Paging getPaging() {
	    return paging;
	}

	public void setPaging(Paging paging) {
	    this.paging = paging;
	}

	public class Paging {

	    private Cursors cursors;
	    
	    public Cursors getCursors() {
	        return cursors;
	    }

	    public void setCursors(Cursors cursors) {
	        this.cursors = cursors;
	    }
	}

	public class Cursors {
		
	    private String after;
	    private String before;

	    public String getAfter() {
	        return after;
	    }
	    
	    public void setAfter(String after) {
	        this.after = after;
	    }
	    
	    public String getBefore() {
	        return before;
	    }
	    
	    public void setBefore(String before) {
	        this.before = before;
	    }

	}
	
}
