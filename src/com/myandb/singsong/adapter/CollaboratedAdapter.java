package com.myandb.singsong.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.event.Listeners;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.User;

public class CollaboratedAdapter extends HolderAdapter<Song, CollaboratedAdapter.SongHolder> {

	public CollaboratedAdapter() {
		super(Song.class);
	}

	@Override
	public SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = View.inflate(parent.getContext(), R.layout.row_collaborated, null);
		return new SongHolder(view);
	}

	@Override
	public void onBindViewHolder(SongHolder viewHolder, int position) {
		final Song thisSong = getItem(position);
		final Song parentSong = thisSong.getParentSong();
		final User thisUser = thisSong.getCreator();
		final User parentUser = thisSong.getParentUser();
		final Music music = thisSong.getMusic();
		final Context context = viewHolder.view.getContext();
		
		viewHolder.tvParentUserNickname.setText(parentUser.getNickname());
		viewHolder.tvThisUserNickname.setText(thisUser.getNickname());
		viewHolder.tvParentSongMessage.setText(parentSong.getCroppedMessage());
		viewHolder.tvThisSongMessage.setText(thisSong.getCroppedMessage());
		viewHolder.tvSongLikeNum.setText(thisSong.getWorkedLikeNum());
		viewHolder.tvSongCommentNum.setText(thisSong.getWorkedCommentNum());
		viewHolder.tvSongCreatedTime.setText(thisSong.getWorkedCreatedTime(getCurrentDate()));
		
		viewHolder.tvMusicInfo.setText(music.getSingerName());
		viewHolder.tvMusicInfo.append("\n");
		viewHolder.tvMusicInfo.append(music.getWorkedTitle());
		viewHolder.tvMusicInfo.append("\t");
		viewHolder.tvMusicInfo.append("(" + thisSong.getWorkedDuration() + ")");
		
		ImageHelper.displayPhoto(parentUser, viewHolder.ivParentUserPhoto);
		ImageHelper.displayPhoto(thisUser, viewHolder.ivThisUserPhoto);
		ImageHelper.displayPhoto(parentSong.getPhotoUrl(), viewHolder.ivParentSongImage);
		ImageHelper.displayPhoto(thisSong.getPhotoUrl(), viewHolder.ivThisSongImage);
		
		viewHolder.ivParentUserPhoto.setOnClickListener(Listeners.getProfileClickListener(context, parentUser));
		viewHolder.ivThisUserPhoto.setOnClickListener(Listeners.getProfileClickListener(context, thisUser));
		viewHolder.view.setOnClickListener(Listeners.getPlayClickListener(context, thisSong));
	}
	
	public static final class SongHolder extends ViewHolder {
		
		public TextView tvSongLikeNum;
		public TextView tvSongCommentNum;
		public TextView tvSongCreatedTime;
		public TextView tvParentUserNickname;
		public TextView tvParentSongMessage;
		public TextView tvThisUserNickname;
		public TextView tvThisSongMessage;
		public TextView tvMusicInfo;
		public ImageView ivParentUserPhoto;						
		public ImageView ivParentSongImage;
		public ImageView ivThisUserPhoto;
		public ImageView ivThisSongImage;
		
		public SongHolder(View view) {
			super(view);
			
			tvParentUserNickname = (TextView) view.findViewById(R.id.tv_parent_user_nickname);
			tvThisUserNickname = (TextView) view.findViewById(R.id.tv_this_user_nickname);
			tvParentSongMessage = (TextView) view.findViewById(R.id.tv_parent_song_message);
			tvThisSongMessage = (TextView) view.findViewById(R.id.tv_this_song_message);
			tvMusicInfo = (TextView) view.findViewById(R.id.tv_music_info);
			tvSongLikeNum = (TextView) view.findViewById(R.id.tv_song_like_num);
			tvSongCommentNum = (TextView) view.findViewById(R.id.tv_song_comment_num);
			tvSongCreatedTime = (TextView) view.findViewById(R.id.tv_song_created_time);
			ivParentUserPhoto = (ImageView) view.findViewById(R.id.iv_parent_user_photo);
			ivThisUserPhoto = (ImageView) view.findViewById(R.id.iv_this_user_photo);
			ivParentSongImage = (ImageView) view.findViewById(R.id.iv_parent_song_image);
			ivThisSongImage = (ImageView) view.findViewById(R.id.iv_this_song_image);
		}
		
	}

}
