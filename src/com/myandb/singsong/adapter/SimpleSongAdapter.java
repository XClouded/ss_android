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

public class SimpleSongAdapter extends AutoLoadAdapter<Song> {

	public SimpleSongAdapter(Context context) {
		super(context, Song.class, true);
	}
	
	public SimpleSongAdapter(Context context, UrlBuilder urlBuilder) {
		this(context);
		
		resetRequest(urlBuilder);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final SongHolder songHolder;
		final Song thisSong = (Song) getItem(position);
		final User thisUser = thisSong.getCreator();
		final Music music = thisSong.getMusic();
		
		if (view == null) {
			view = View.inflate(getContext(), R.layout.row_simple_song, null);
			
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
			songHolder.ivAlbumPhoto = (ImageView) view.findViewById(R.id.iv_album_photo);
			
			songHolder.vPartnerWrapper = view.findViewById(R.id.rl_partner_wrapper);
			
			view.setTag(songHolder);
		} else {
			songHolder = (SongHolder) view.getTag();
		}
		
		if (music != null && thisUser != null) {
			songHolder.tvParentUserNickname.setText(thisUser.getNickname());
			songHolder.tvParentSongMessage.setText(thisSong.getCroppedMessage());
			songHolder.tvSongLikeNum.setText(thisSong.getWorkedLikeNum());
			songHolder.tvSongCommentNum.setText(thisSong.getWorkedCommentNum());
			songHolder.tvSongCreatedTime.setText(thisSong.getWorkedCreatedTime(getCurrentDate()));
			
			songHolder.tvMusicInfo.setText(music.getSingerName());
			songHolder.tvMusicInfo.append(" - ");
			songHolder.tvMusicInfo.append(music.getTitle());
			
			ImageHelper.displayPhoto(music.getAlbumPhotoUrl(), songHolder.ivAlbumPhoto);
			ImageHelper.displayPhoto(thisUser, songHolder.ivParentUserPhoto);
			songHolder.ivParentUserPhoto.setOnClickListener(Listeners.getProfileClickListener(getContext(), thisUser));
			
			if (!thisSong.isRoot()) {
				songHolder.vPartnerWrapper.setVisibility(View.VISIBLE);
				
				final Song parentSong = thisSong.getParentSong();
				final User parentUser = thisSong.getParentUser();
				
				songHolder.tvThisUserNickname.setText(parentUser.getNickname());
				songHolder.tvThisSongMessage.setText(parentSong.getCroppedMessage());
				
				ImageHelper.displayPhoto(parentUser, songHolder.ivThisUserPhoto);
				songHolder.ivThisUserPhoto.setOnClickListener(Listeners.getProfileClickListener(getContext(), parentUser));
			} else {
				songHolder.vPartnerWrapper.setVisibility(View.GONE);
			}
			
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
						 ivThisUserPhoto,
						 ivAlbumPhoto;
		
		public View vPartnerWrapper;
	}

}
