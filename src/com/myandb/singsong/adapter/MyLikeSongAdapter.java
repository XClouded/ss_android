package com.myandb.singsong.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Category;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.SongLiking;
import com.myandb.singsong.model.User;

public class MyLikeSongAdapter extends HolderAdapter<SongLiking, MyLikeSongAdapter.SongHolder> {
	
	public MyLikeSongAdapter() {
		super(SongLiking.class);
	}

	@Override
	public SongHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.row_simple_song, parent, false);
		return new SongHolder(view);
	}

	@Override
	public void onBindViewHolder(Context context, SongHolder viewHolder, int position) {
		final SongLiking liking = getItem(position);
		final Song thisSong = liking.getLikeable();
		final User thisUser = thisSong.getCreator();
		final Music music = thisSong.getMusic();
		final Category category = thisSong.getCategory();
		
		viewHolder.tvParentUserNickname.setText(thisUser.getNickname());
		viewHolder.tvParentSongMessage.setText(thisSong.getCroppedMessage());
		viewHolder.tvSongLikeNum.setText(thisSong.getWorkedLikeNum());
		viewHolder.tvSongCommentNum.setText(thisSong.getWorkedCommentNum());
		viewHolder.tvSongCreatedTime.setText(thisSong.getWorkedCreatedTime(getCurrentDate()));
		viewHolder.tvCategoryTitle.setText(category.getTitle());
		
		viewHolder.tvMusicInfo.append(music.getTitle());
		viewHolder.tvMusicInfo.append(" - ");
		viewHolder.tvMusicInfo.append(music.getSingerName());
		
		viewHolder.ivParentUserPhoto.setOnClickListener(thisUser.getProfileClickListener());
		viewHolder.view.setOnClickListener(thisSong.getPlayClickListener());
		
		ImageHelper.displayPhoto(music.getAlbumPhotoUrl(), viewHolder.ivAlbumPhoto);
		ImageHelper.displayPhoto(thisUser, viewHolder.ivParentUserPhoto);
		
		if (!thisSong.isRoot()) {
			final Song parentSong = thisSong.getParentSong();
			final User parentUser = thisSong.getParentUser();
			
			viewHolder.vPartnerWrapper.setVisibility(View.VISIBLE);
			viewHolder.tvThisUserNickname.setText(parentUser.getNickname());
			viewHolder.tvThisSongMessage.setText(parentSong.getCroppedMessage());
			viewHolder.ivThisUserPhoto.setOnClickListener(parentUser.getProfileClickListener());
			
			ImageHelper.displayPhoto(parentUser, viewHolder.ivThisUserPhoto);
		} else {
			viewHolder.vPartnerWrapper.setVisibility(View.GONE);
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
		public TextView tvCategoryTitle;
		public ImageView ivParentUserPhoto;
		public ImageView ivThisUserPhoto;
		public ImageView ivAlbumPhoto;
		public View vPartnerWrapper;
		
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
			tvCategoryTitle = (TextView) view.findViewById(R.id.tv_category_title);
			ivParentUserPhoto = (ImageView) view.findViewById(R.id.iv_parent_user_photo);
			ivThisUserPhoto = (ImageView) view.findViewById(R.id.iv_this_user_photo);
			ivAlbumPhoto = (ImageView) view.findViewById(R.id.iv_album_photo);
			vPartnerWrapper = view.findViewById(R.id.rl_partner_wrapper);
		}
		
	}

}
