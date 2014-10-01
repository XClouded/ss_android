package com.myandb.singsong.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.event.Listeners;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.ImageHelper;

public class CollaboratedAdapter extends AutoLoadAdapter<Song> {

	public CollaboratedAdapter(Context context) {
		super(context, Song.class, true);
	}
	
	public CollaboratedAdapter(Context context, UrlBuilder urlBuilder) {
		this(context);
		
		resetRequest(urlBuilder);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final SongHolder songHolder;
		final Song thisSong = (Song) getItem(position);
		final Song parentSong = thisSong.getParentSong();
		final User thisUser = thisSong.getCreator();
		final User parentUser = thisSong.getParentUser();
		final Music music = thisSong.getMusic();
		
		if (view == null) {
			view = View.inflate(getContext(), R.layout.row_collaborated, null);
			
			songHolder = new SongHolder();
			songHolder.tvParentUserNickname = (TextView) view.findViewById(R.id.tv_parent_user_nickname);
			songHolder.tvThisUserNickname = (TextView) view.findViewById(R.id.tv_this_user_nickname);
			songHolder.tvParentSongMessage = (TextView) view.findViewById(R.id.tv_parent_song_message);
			songHolder.tvThisSongMessage = (TextView) view.findViewById(R.id.tv_this_song_message);
			songHolder.tvMusicInfo = (TextView) view.findViewById(R.id.tv_music_info);
			songHolder.tvSongLikeNum = (TextView) view.findViewById(R.id.tv_song_like_num);
			songHolder.tvSongCommentNum = (TextView) view.findViewById(R.id.tv_song_comment_num);
			songHolder.tvSongCreatedTime = (TextView) view.findViewById(R.id.tv_song_created_time);
			
			songHolder.ivParentUserPhoto = (ImageView) view.findViewById(R.id.iv_parent_user_photo);
			songHolder.ivThisUserPhoto = (ImageView) view.findViewById(R.id.iv_this_user_photo);
			songHolder.ivParentSongImage = (ImageView) view.findViewById(R.id.iv_parent_song_image);
			songHolder.ivThisSongImage = (ImageView) view.findViewById(R.id.iv_this_song_image);
			
			view.setTag(songHolder);
		} else {
			songHolder = (SongHolder) view.getTag();
		}
		
		if (music != null && thisUser != null && parentUser != null && parentSong != null) {
			songHolder.tvParentUserNickname.setText(parentUser.getNickname());
			songHolder.tvThisUserNickname.setText(thisUser.getNickname());
			songHolder.tvParentSongMessage.setText(parentSong.getCroppedMessage());
			songHolder.tvThisSongMessage.setText(thisSong.getCroppedMessage());
			songHolder.tvSongLikeNum.setText(thisSong.getWorkedLikeNum());
			songHolder.tvSongCommentNum.setText(thisSong.getWorkedCommentNum());
			songHolder.tvSongCreatedTime.setText(thisSong.getWorkedCreatedTime(getCurrentDate()));
			
			songHolder.tvMusicInfo.setText(music.getSingerName());
			songHolder.tvMusicInfo.append("\n");
			songHolder.tvMusicInfo.append(music.getWorkedTitle());
			songHolder.tvMusicInfo.append("\t");
			songHolder.tvMusicInfo.append("(" + thisSong.getWorkedDuration() + ")");
			
			ImageHelper.displayPhoto(parentUser, songHolder.ivParentUserPhoto);
			ImageHelper.displayPhoto(thisUser, songHolder.ivThisUserPhoto);
			ImageHelper.displayPhoto(parentSong.getPhotoUrl(), songHolder.ivParentSongImage);
			ImageHelper.displayPhoto(thisSong.getPhotoUrl(), songHolder.ivThisSongImage);
			
			songHolder.ivParentUserPhoto.setOnClickListener(Listeners.getProfileClickListener(getContext(), parentUser));
			songHolder.ivThisUserPhoto.setOnClickListener(Listeners.getProfileClickListener(getContext(), thisUser));
			view.setOnClickListener(Listeners.getPlayClickListener(getContext(), thisSong));
		}
		
		return view;
	}
	
	private static class SongHolder {
		
		public TextView tvSongLikeNum,
						tvSongCommentNum,
						tvSongCreatedTime,
						tvParentUserNickname,
						tvParentSongMessage,
						tvThisUserNickname,
						tvThisSongMessage,
						tvMusicInfo;
		
		public ImageView ivParentUserPhoto,						
						 ivParentSongImage,
						 ivThisUserPhoto,
						 ivThisSongImage;
	}

}
