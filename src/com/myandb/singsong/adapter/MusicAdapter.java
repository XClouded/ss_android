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

public class MusicAdapter extends HolderAdapter<Music, MusicAdapter.MusicHolder> {
	
	public enum LayoutType {
		
		POPULAR(R.layout.row_music_popular, true),
		
		RECENT(R.layout.row_music_recent, true),
		
		NORMAL(R.layout.row_music_normal, false);
		
		private int layoutResId;
		private boolean horizontal;
		
		LayoutType(int resId) {
			this(resId, false);
		}
		
		LayoutType(int resId, boolean horizontal) {
			this.layoutResId = resId;
			this.horizontal = horizontal;
		}
		
		public int getLayoutResourceId() {
			return layoutResId;
		}
		
		public boolean isHorizontalView() {
			return horizontal;
		}
	}
	
	private LayoutType layoutType;
	private int padding;
	
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
		padding = view.getResources().getDimensionPixelSize(R.dimen.margin);
		return new MusicHolder(view, layoutType);
	}

	@Override
	public void onBindViewHolder(Context context, MusicHolder viewHolder, int position) {
		final Music music = getItem(position);
		
		viewHolder.tvSingerName.setSelected(true);
		viewHolder.tvMusicTitle.setSelected(true);
		viewHolder.tvSingerName.setText(music.getSingerName());
		viewHolder.tvMusicTitle.setText(music.getTitle());
		viewHolder.tvSingNum.setText(String.valueOf(music.getWorkedSingNum()));
		ImageHelper.displayPhoto(music.getAlbumPhotoUrl(), viewHolder.ivAlbumPhoto);
		
		if (layoutType.isHorizontalView()) {
			if (isFirstItem(position)) {
				setFirstItemLeftPadding(viewHolder.view);
			}
			if (isLastItem(position)) {
				setLastItemRightPadding(viewHolder.view);
			}
		} else {
			viewHolder.view.setOnClickListener(music.getMusicClickListener());
		}
	}
	
	private void setFirstItemLeftPadding(View view) {
		view.setPadding(padding, 0, 0, 0);
	}
	
	private void setLastItemRightPadding(View view) {
		view.setPadding(0, 0, padding, 0);
	}
	
	private boolean isFirstItem(int position) {
		return position == 0;
	}
	
	private boolean isLastItem(int position) {
		return position == getCount() - 1;
	}

	public static final class MusicHolder extends ViewHolder {
		
		public TextView tvSingerName;
		public TextView tvMusicTitle;
		public TextView tvSingNum;
		public ImageView ivAlbumPhoto;

		public MusicHolder(View view, LayoutType type) {
			super(view);
			
			tvSingerName = (TextView) view.findViewById(R.id.tv_singer_name);
			tvMusicTitle = (TextView) view.findViewById(R.id.tv_music_title);
			tvSingNum = (TextView) view.findViewById(R.id.tv_sing_num);
			ivAlbumPhoto = (ImageView) view.findViewById(R.id.iv_album_photo);
		}
		
	}

}
