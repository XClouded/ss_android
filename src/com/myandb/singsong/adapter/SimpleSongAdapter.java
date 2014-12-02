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

public class SimpleSongAdapter extends HolderAdapter<Song, SimpleSongAdapter.SongHolder> {

	public SimpleSongAdapter() {
		super(Song.class);
	}

	@Override
	public SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = View.inflate(parent.getContext(), R.layout.row_simple_song, null);
		return new SongHolder(view);
	}

	@Override
	public void onBindViewHolder(SongHolder viewHolder, int position) {
		final Song thisSong = getItem(position);
		final User thisUser = thisSong.getCreator();
		final Music music = thisSong.getMusic();
		final Context context = viewHolder.view.getContext();

		viewHolder.tvParentUserNickname.setText(thisUser.getNickname());
		viewHolder.tvParentSongMessage.setText(thisSong.getCroppedMessage());
		viewHolder.tvSongLikeNum.setText(thisSong.getWorkedLikeNum());
		viewHolder.tvSongCommentNum.setText(thisSong.getWorkedCommentNum());
		viewHolder.tvSongCreatedTime.setText(thisSong.getWorkedCreatedTime(getCurrentDate()));
		
		viewHolder.tvMusicInfo.setText(music.getSingerName());
		viewHolder.tvMusicInfo.append(" - ");
		viewHolder.tvMusicInfo.append(music.getTitle());
		
		ImageHelper.displayPhoto(music.getAlbumPhotoUrl(), viewHolder.ivAlbumPhoto);
		ImageHelper.displayPhoto(thisUser, viewHolder.ivParentUserPhoto);
		viewHolder.ivParentUserPhoto.setOnClickListener(Listeners.getProfileClickListener(context, thisUser));
		
		if (!thisSong.isRoot()) {
			viewHolder.vPartnerWrapper.setVisibility(View.VISIBLE);
			
			final Song parentSong = thisSong.getParentSong();
			final User parentUser = thisSong.getParentUser();
			
			viewHolder.tvThisUserNickname.setText(parentUser.getNickname());
			viewHolder.tvThisSongMessage.setText(parentSong.getCroppedMessage());
			
			ImageHelper.displayPhoto(parentUser, viewHolder.ivThisUserPhoto);
			viewHolder.ivThisUserPhoto.setOnClickListener(Listeners.getProfileClickListener(context, parentUser));
		} else {
			viewHolder.vPartnerWrapper.setVisibility(View.GONE);
		}
		
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
			ivParentUserPhoto = (ImageView) view.findViewById(R.id.iv_parent_user_photo);
			ivThisUserPhoto = (ImageView) view.findViewById(R.id.iv_this_user_photo);
			ivAlbumPhoto = (ImageView) view.findViewById(R.id.iv_album_photo);
			vPartnerWrapper = view.findViewById(R.id.rl_partner_wrapper);
		}
		
	}

}
