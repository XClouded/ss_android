package com.myandb.singsong.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.User;
import com.myandb.singsong.widget.RoundedImageView;

public class SongAdapter extends HolderAdapter<Song, SongAdapter.SongHolder> {
	
	private List<Boolean> cornerRadius;

	public SongAdapter() {
		super(Song.class);
		
		cornerRadius = new ArrayList<Boolean>();
		cornerRadius.add(true);
		cornerRadius.add(true);
		cornerRadius.add(true);
		cornerRadius.add(true);
	}

	@Override
	public SongHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.row_song, parent, false);
		return new SongHolder(view);
	}

	@Override
	public void onBindViewHolder(Context context, SongHolder viewHolder, Song song, int position) {
		final User thisUser = song.getCreator();
		final Music music = song.getMusic();
		
		if (music == null) {
			return;
		}
		
		viewHolder.tvSongLikeNum.setText(song.getWorkedLikeNum());
		viewHolder.tvSongCommentNum.setText(song.getWorkedCommentNum());
		viewHolder.tvSongCreatedTime.setText(song.getWorkedCreatedTime(getCurrentDate()));
		
		viewHolder.tvMusicInfo.setText(music.getWorkedTitle());
		viewHolder.tvMusicInfo.append("\t");
		viewHolder.tvMusicInfo.append("(" + song.getWorkedDuration() + ")");
		viewHolder.tvMusicInfo.append("\n");
		viewHolder.tvMusicInfo.append(music.getSingerName());
		
		viewHolder.view.setOnClickListener(song.getPlayClickListener());
		viewHolder.vPrelistenControl.setOnClickListener(song.getSampleClickListener());
		
		if (!song.isRoot()) {
			final Song parentSong = song.getParentSong();
			final User parentUser = song.getParentUser();
			if (parentSong == null || parentUser == null) {
				return;
			}
			
			viewHolder.vThisPhotoWrapper.setVisibility(View.VISIBLE);
			viewHolder.ivThisSongImage.setVisibility(View.VISIBLE);
			setCornerRadius(viewHolder.ivParentSongImage, false);
			
			viewHolder.tvParentUserNickname.setText(parentUser.getNickname());
			viewHolder.tvThisUserNickname.setText(thisUser.getNickname());
			viewHolder.tvParentSongMessage.setText(parentSong.getCroppedMessage());
			viewHolder.tvThisSongMessage.setText(song.getCroppedMessage());
			
			ImageHelper.displayPhoto(parentUser, viewHolder.ivParentUserPhoto);
			ImageHelper.displayPhoto(thisUser, viewHolder.ivThisUserPhoto);
			ImageHelper.displayPhoto(parentSong.getPhotoUrl(), viewHolder.ivParentSongImage);
			ImageHelper.displayPhoto(song.getPhotoUrl(), viewHolder.ivThisSongImage);
			
			viewHolder.ivParentUserPhoto.setOnClickListener(parentUser.getProfileClickListener());
			viewHolder.ivThisUserPhoto.setOnClickListener(thisUser.getProfileClickListener());
		} else {
			viewHolder.vThisPhotoWrapper.setVisibility(View.GONE);
			viewHolder.ivThisSongImage.setVisibility(View.GONE);
			setCornerRadius(viewHolder.ivParentSongImage, true);
			
			viewHolder.tvParentUserNickname.setText(thisUser.getNickname());
			viewHolder.tvParentSongMessage.setText(song.getCroppedMessage());
			
			ImageHelper.displayPhoto(thisUser, viewHolder.ivParentUserPhoto);
			ImageHelper.displayPhoto(song.getPhotoUrl(), viewHolder.ivParentSongImage);
			
			viewHolder.ivParentUserPhoto.setOnClickListener(thisUser.getProfileClickListener());
		}
	}
	
	private void setCornerRadius(ImageView imageView, boolean topRight) {
		if (imageView instanceof RoundedImageView) {
			if (cornerRadius != null && cornerRadius.size() == 4) {
				cornerRadius.set(1, topRight);
				((RoundedImageView) imageView).setCornerDirection(cornerRadius);
			}
		}
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
		public View vPrelistenControl;
		public View vThisPhotoWrapper;
		
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
			vPrelistenControl = view.findViewById(R.id.ll_prelisten_control);
			vThisPhotoWrapper = view.findViewById(R.id.ll_this_photo_wrapper);
		}
		
	}

}
