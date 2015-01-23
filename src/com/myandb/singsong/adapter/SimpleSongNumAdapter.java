package com.myandb.singsong.adapter;

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

public class SimpleSongNumAdapter extends HolderAdapter<Song, SimpleSongNumAdapter.SongHolder> {
	
	private int numOffset;

	public SimpleSongNumAdapter() {
		this(0);
	}
	
	public SimpleSongNumAdapter(int numOffset) {
		super(Song.class);
		this.numOffset = numOffset;
	}

	@Override
	public SongHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.row_song_simple_num, parent, false);
		return new SongHolder(view);
	}

	@Override
	public void onBindViewHolder(Context context, SongHolder viewHolder, Song thisSong, int position) {
		final Music music = thisSong.getMusic();
		
		if (music == null) {
			return;
		}
		
		viewHolder.tvSongLikeNum.setText(thisSong.getWorkedLikeNum());
		viewHolder.tvSongNum.setText(String.valueOf(position + 1 + numOffset));
		
		viewHolder.tvMusicInfo.setText(music.getTitle());
		viewHolder.tvMusicInfo.append(" - ");
		viewHolder.tvMusicInfo.append(music.getSingerName());
		
		viewHolder.view.setOnClickListener(thisSong.getPlayClickListener());
		
		ImageHelper.displayPhoto(music.getAlbumPhotoUrl(), viewHolder.ivAlbumPhoto);
		
		viewHolder.tvUserInfo.setText(thisSong.getParentUser().getNickname());
		if (!thisSong.isRoot()) {
			viewHolder.tvUserInfo.append(" X ");
			viewHolder.tvUserInfo.append(thisSong.getCreator().getNickname());
		}
	}
	
	public static final class SongHolder extends ViewHolder {
		
		public TextView tvSongLikeNum;
		public TextView tvMusicInfo;
		public TextView tvUserInfo;
		public TextView tvSongNum;
		public ImageView ivAlbumPhoto;
		
		public SongHolder(View view) {
			super(view);
			
			tvSongLikeNum = (TextView) view.findViewById(R.id.tv_song_like_num);
			tvMusicInfo = (TextView) view.findViewById(R.id.tv_music_info);
			tvUserInfo = (TextView) view.findViewById(R.id.tv_user_info);
			tvSongNum = (TextView) view.findViewById(R.id.tv_song_num);
			ivAlbumPhoto = (ImageView) view.findViewById(R.id.iv_album_photo);
		}
		
	}

}
