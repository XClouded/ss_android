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

public class ChildrenSongAdapter extends HolderAdapter<Song, ChildrenSongAdapter.SongHolder> {
	
	public ChildrenSongAdapter() {
		super(Song.class);
	}

	@Override
	public SongHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.row_song_children, parent, false);
		return new SongHolder(view);
	}

	@Override
	public void onBindViewHolder(Context context, SongHolder viewHolder, int position) {
		final Song song = getItem(position);
		final User creator = song.getCreator();
		
		viewHolder.tvCreatorNickname.setText(creator.getNickname());
		viewHolder.tvCreatorPart.setText(song.getPartName());
		if (song.getLyricPart() == Music.PART_MALE) {
			viewHolder.tvCreatorPart.setTextColor(Color.parseColor("#6ecde1"));
		} else {
			viewHolder.tvCreatorPart.setTextColor(Color.parseColor("#f98e5f"));
		}
		viewHolder.tvCreatorMessage.setText(song.getCroppedMessage() + "\n");
		viewHolder.tvLikeNum.setText(song.getWorkedLikeNum());
		viewHolder.tvCommentNum.setText(song.getWorkedCommentNum());
		viewHolder.tvCollaboNum.setText(song.getWorkedCollaboNum());
		viewHolder.view.setOnClickListener(song.getPlayClickListener());
		viewHolder.vPrelistenControl.setOnClickListener(song.getSampleClickListener());
		
		ImageHelper.displayPhoto(creator, viewHolder.ivCreatorPhoto);
	}
	
	public static final class SongHolder extends ViewHolder {
		
		public TextView tvCreatorNickname;
		public TextView tvCreatorPart;
		public TextView tvCreatorMessage;
		public TextView tvLikeNum;
		public TextView tvCommentNum;
		public TextView tvCollaboNum;
		public ImageView ivCreatorPhoto;
		public View vPrelistenControl;
		
		public SongHolder(View view) {
			super(view);
			
			ivCreatorPhoto = (ImageView) view.findViewById(R.id.iv_parent_user_photo);
			tvCreatorNickname = (TextView) view.findViewById(R.id.tv_parent_user_nickname);
			tvCreatorPart = (TextView) view.findViewById(R.id.tv_parent_user_part);
			tvCreatorMessage = (TextView) view.findViewById(R.id.tv_parent_song_message);
			tvLikeNum = (TextView) view.findViewById(R.id.tv_song_like_num);
			tvCommentNum = (TextView) view.findViewById(R.id.tv_song_comment_num);
			tvCollaboNum = (TextView) view.findViewById(R.id.tv_song_collabo_num);
			vPrelistenControl = view.findViewById(R.id.ll_prelisten_control);
		}
		
	}
	
}
