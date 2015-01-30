package com.myandb.singsong.adapter;

import com.myandb.singsong.R;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.User;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SimpleChildrenSongAdapter extends HolderAdapter<Song, SimpleChildrenSongAdapter.SongHolder> {
	
	public SimpleChildrenSongAdapter() {
		super(Song.class);
	}

	@Override
	public SongHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.row_song_simple_children, parent, false);
		return new SongHolder(view);
	}

	@Override
	public void onBindViewHolder(Context context, SongHolder viewHolder, Song song, int position) {
		final User creator = song.getCreator();
		final Music music = song.getMusic();
		if (creator == null || music == null) {
			return;
		}
		
		viewHolder.tvMusicTitle.setText(music.getTitle());
		viewHolder.tvSingerName.setText(music.getSingerName());
		viewHolder.tvCreatorNickname.setText(creator.getNickname());
		viewHolder.tvCreatorPart.setText(song.getPartName());
		if (song.getLyricPart() == Music.PART_MALE) {
			viewHolder.tvCreatorPart.setTextColor(Color.parseColor("#6ecde1"));
		} else {
			viewHolder.tvCreatorPart.setTextColor(Color.parseColor("#f98e5f"));
		}
		viewHolder.vPrelistenControl.setOnClickListener(song.getSampleClickListener());
		
		ImageHelper.displayPhoto(creator, viewHolder.ivCreatorPhoto);
	}
	
	public static final class SongHolder extends ViewHolder {
		
		public TextView tvMusicTitle;
		public TextView tvSingerName;
		public TextView tvCreatorNickname;
		public TextView tvCreatorPart;
		public ImageView ivCreatorPhoto;
		public View vPrelistenControl;
		
		public SongHolder(View view) {
			super(view);
			
			ivCreatorPhoto = (ImageView) view.findViewById(R.id.iv_parent_user_photo);
			tvCreatorNickname = (TextView) view.findViewById(R.id.tv_parent_user_nickname);
			tvCreatorPart = (TextView) view.findViewById(R.id.tv_parent_user_part);
			tvMusicTitle = (TextView) view.findViewById(R.id.tv_music_title);
			tvSingerName = (TextView) view.findViewById(R.id.tv_singer_name);
			vPrelistenControl = view.findViewById(R.id.ll_prelisten_control);
		}
		
	}
	
}
