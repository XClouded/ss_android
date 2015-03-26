package com.myandb.singsong.adapter;

import android.content.Context;

import com.myandb.singsong.model.Music;

public class TeamCollaboMusicAdapter extends MusicAdapter {

	@Override
	public void onBindViewHolder(Context context, MusicHolder viewHolder, Music music, int position) {
		super.onBindViewHolder(context, viewHolder, music, position);
		viewHolder.view.setOnClickListener(music.getTeamCollaboClickListener());
	}

}
