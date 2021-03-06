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

public class MusicAdapter extends HolderAdapter<Music, MusicAdapter.MusicHolder> {
	
	public enum LayoutType {
		
		POPULAR_HOME(R.layout.row_music_popular_home),
		
		POPULAR(R.layout.row_music_popular),
		
		RECENT(R.layout.row_music_recent, true),
		
		NORMAL(R.layout.row_music_normal),
		
		NORMAL_POPULAR(R.layout.row_music_normal);
		
		private int layoutResId;
		private boolean horizontalListView;
		
		LayoutType(int resId) {
			this(resId, false);
		}
		
		LayoutType(int resId, boolean horizontal) {
			this.layoutResId = resId;
			this.horizontalListView = horizontal;
		}
		
		public int getLayoutResourceId() {
			return layoutResId;
		}
		
		public boolean isHorizontalListView() {
			return horizontalListView;
		}
	}
	
	private LayoutType layoutType;
	
	public MusicAdapter() {
		this(LayoutType.NORMAL);
	}
	
	public MusicAdapter(LayoutType type) {
		super(Music.class);
		this.layoutType = type;
	}

	@Override
	public MusicHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View view = inflater.inflate(layoutType.getLayoutResourceId(), parent, false);
		return new MusicHolder(view);
	}

	@Override
	public void onBindViewHolder(Context context, MusicHolder viewHolder, Music music, int position) {
		viewHolder.tvSingerName.setSelected(true);
		viewHolder.tvMusicTitle.setSelected(true);
		viewHolder.tvSingerName.setText(music.getSingerName());
		viewHolder.tvMusicTitle.setText(music.getTitle());
		viewHolder.tvSingNum.setText(String.valueOf(music.getWorkedSingNum()));
		ImageHelper.displayPhoto(music.getAlbumPhotoUrl(), viewHolder.ivAlbumPhoto);
		
		if (!layoutType.isHorizontalListView()) {
			viewHolder.view.setOnClickListener(music.getMusicClickListener());
		}
		
		if (layoutType.equals(LayoutType.POPULAR_HOME)) {
			final List<Song> songs = music.getSongs();
			displayUsers(viewHolder, songs);
		}
		
		if (layoutType.equals(LayoutType.NORMAL)) {
			viewHolder.tvMusicNum.setVisibility(View.GONE);
		}
		
		if (layoutType.equals(LayoutType.NORMAL_POPULAR)) {
			viewHolder.tvMusicNum.setVisibility(View.VISIBLE);
			if (position < 99) {
				viewHolder.tvMusicNum.setText(String.valueOf(position + 1));
			} else {
				viewHolder.tvMusicNum.setText(String.valueOf("-"));
			}
		}
	}
	
	private void displayUsers(MusicHolder holder, List<Song> songs) {
		for (int i = 0, l = holder.tvUserNicknames.size(); i < l; i++) {
			if (i < songs.size()) {
				final Song song = songs.get(i);
				final User user = song.getCreator();
				holder.tvUserNicknames.get(i).setText(user.getNickname());
				holder.ivUserPhotos.get(i).setOnClickListener(song.getPlayClickListener());
				ImageHelper.displayPhoto(user, holder.ivUserPhotos.get(i));
			}
		}
	}

	public static final class MusicHolder extends ViewHolder {
		
		public TextView tvMusicNum;
		public TextView tvSingerName;
		public TextView tvMusicTitle;
		public TextView tvSingNum;
		public List<TextView> tvUserNicknames;
		public ImageView ivAlbumPhoto;
		public List<ImageView> ivUserPhotos;

		public MusicHolder(View view) {
			super(view);
			
			tvMusicNum = (TextView) view.findViewById(R.id.tv_music_num);
			tvSingerName = (TextView) view.findViewById(R.id.tv_singer_name);
			tvMusicTitle = (TextView) view.findViewById(R.id.tv_music_title);
			tvSingNum = (TextView) view.findViewById(R.id.tv_sing_num);
			ivAlbumPhoto = (ImageView) view.findViewById(R.id.iv_album_photo);
			
			tvUserNicknames = new ArrayList<TextView>();
			tvUserNicknames.add((TextView) view.findViewById(R.id.tv_first_user_nickname));
			tvUserNicknames.add((TextView) view.findViewById(R.id.tv_second_user_nickname));
			tvUserNicknames.add((TextView) view.findViewById(R.id.tv_third_user_nickname));
			
			ivUserPhotos = new ArrayList<ImageView>();
			ivUserPhotos.add((ImageView) view.findViewById(R.id.iv_first_user_photo));
			ivUserPhotos.add((ImageView) view.findViewById(R.id.iv_second_user_photo));
			ivUserPhotos.add((ImageView) view.findViewById(R.id.iv_third_user_photo));
		}
		
	}

}
