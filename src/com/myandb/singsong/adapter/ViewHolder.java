package com.myandb.singsong.adapter;

import android.view.View;

public abstract class ViewHolder {
	
	public View view;
	
	public ViewHolder(View view) {
		this.view = view;
		view.setTag(this);
	}
	
}