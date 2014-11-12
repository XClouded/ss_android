package com.myandb.singsong.adapter;

import com.myandb.singsong.R;
import com.myandb.singsong.event.Listeners;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.User;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ChildrenSongAdapter extends HolderAdapter<Song, ChildrenSongAdapter.SongHolder> {
	
	public ChildrenSongAdapter() {
		super(Song.class);
	}

	@Override
	public SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = View.inflate(parent.getContext(), R.layout.row_child_song, null);
		return new SongHolder(view);
	}

	@Override
	public void onBindViewHolder(SongHolder viewHolder, int position) {
		Song song = (Song) getItem(position);
		User creator = song.getCreator();
		Context context = viewHolder.view.getContext();
		
		viewHolder.tvCreatorNickname.setText(creator.getNickname());
		viewHolder.tvCreatorMessage.setText(song.getCroppedMessage());
		viewHolder.tvCreatedTime.setText(song.getWorkedCreatedTime(getCurrentDate()));
		viewHolder.tvLikeNum.setText(song.getWorkedLikeNum());
		viewHolder.tvCommentNum.setText(song.getWorkedCommentNum());
		
		ImageHelper.displayPhoto(creator, viewHolder.ivCreatorPhoto);
		
		viewHolder.view.setOnClickListener(Listeners.getPlayClickListener(context, song));
	}
	
	public static final class SongHolder extends ViewHolder {
		
		public TextView tvCreatorNickname;
		public TextView tvCreatorMessage;
		public TextView tvLikeNum;
		public TextView tvCommentNum;
		public TextView tvCreatedTime;
		public ImageView ivCreatorPhoto;
		
		public SongHolder(View view) {
			super(view);
			
			ivCreatorPhoto = (ImageView) view.findViewById(R.id.iv_parent_user_photo);
			tvCreatorNickname = (TextView) view.findViewById(R.id.tv_parent_user_nickname);
			tvCreatorMessage = (TextView) view.findViewById(R.id.tv_parent_song_message);
			tvLikeNum = (TextView) view.findViewById(R.id.tv_song_like_num);
			tvCommentNum = (TextView) view.findViewById(R.id.tv_song_comment_num);
			tvCreatedTime = (TextView) view.findViewById(R.id.tv_song_created_time);
		}
		
	}
	
}
