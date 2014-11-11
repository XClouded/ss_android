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

public class ChildrenSongAdapter extends AutoLoadAdapter<Song> {
	
	public ChildrenSongAdapter(Context context) {
		super(context, Song.class, true);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final SongHolder songHolder;
		final Song song = (Song) getItem(position);
		final User creator = song.getCreator();
		
		if (view == null) {
			view = View.inflate(getContext(), R.layout.row_child_song, null);
			
			songHolder = new SongHolder();
			songHolder.ivCreatorPhoto = (ImageView) view.findViewById(R.id.iv_parent_user_photo);
			songHolder.tvCreatorNickname = (TextView) view.findViewById(R.id.tv_parent_user_nickname);
			songHolder.tvCreatorMessage = (TextView) view.findViewById(R.id.tv_parent_song_message);
			songHolder.tvLikeNum = (TextView) view.findViewById(R.id.tv_song_like_num);
			songHolder.tvCommentNum = (TextView) view.findViewById(R.id.tv_song_comment_num);
			songHolder.tvCreatedTime = (TextView) view.findViewById(R.id.tv_song_created_time);
			
			view.setTag(songHolder);
		} else {
			songHolder = (SongHolder) view.getTag();
		}
		
		if (creator != null) {
			songHolder.tvCreatorNickname.setText(creator.getNickname());
			songHolder.tvCreatorMessage.setText(song.getCroppedMessage());
			songHolder.tvCreatedTime.setText(song.getWorkedCreatedTime(getCurrentDate()));
			songHolder.tvLikeNum.setText(song.getWorkedLikeNum());
			songHolder.tvCommentNum.setText(song.getWorkedCommentNum());
			
			ImageHelper.displayPhoto(creator, songHolder.ivCreatorPhoto);
			
			view.setOnClickListener(Listeners.getPlayClickListener(getContext(), song));
		}
		
		return view;
	}
	
	private static class SongHolder {
		
		public TextView tvCreatorNickname,
						tvCreatorMessage,
						tvLikeNum,
						tvCommentNum,
						tvCreatedTime;
		
		public ImageView ivCreatorPhoto;
		
	}
	
}
