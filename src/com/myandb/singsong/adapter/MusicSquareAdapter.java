package com.myandb.singsong.adapter;

import com.myandb.singsong.R;
import com.myandb.singsong.event.Listeners;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Music;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicSquareAdapter extends HolderAdapter<Music, MusicSquareAdapter.MusicHolder> {
	
	public MusicSquareAdapter() {
		super(Music.class);
	}

	@Override
	public MusicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = View.inflate(parent.getContext(), R.layout.row_update, null);
		return new MusicHolder(view);
	}

	@Override
	public void onBindViewHolder(MusicHolder viewHolder, int position) {
		final Music music = getItem(position);
		final Context context = viewHolder.view.getContext();
		
		viewHolder.tvSingerLeft.setText(music.getSingerName());
		viewHolder.tvTitleLeft.setText(music.getTitle());
		ImageHelper.displayPhoto(music.getAlbumPhotoUrl(), viewHolder.ivAlbumPhotoLeft);
		viewHolder.vLeft.setOnClickListener(Listeners.getRecordClickListener(context, music));
		
		final Music musicRight = getNextItem(position);
		if (musicRight != null) {
			viewHolder.vRight.setVisibility(View.VISIBLE);
			viewHolder.tvSingerRight.setText(musicRight.getSingerName());
			viewHolder.tvTitleRight.setText(musicRight.getTitle());
			ImageHelper.displayPhoto(musicRight.getAlbumPhotoUrl(), viewHolder.ivAlbumPhotoRight);
			viewHolder.vRight.setOnClickListener(Listeners.getRecordClickListener(context, musicRight));
		} else {
			viewHolder.vRight.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public int getCount() {
		return Math.round(items.size() / 2f);
	}

	@Override
	public Music getItem(int position) {
		return items.get(position * 2);
	}
	
	private Music getNextItem(int position) {
		int next = position * 2 + 1;
		if (next < items.size()) {
			return items.get(next);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public static final class MusicHolder extends ViewHolder {
		
		public ImageView ivAlbumPhotoLeft;
		public ImageView ivAlbumPhotoRight;
		public TextView tvSingerLeft;
		public TextView tvSingerRight;
		public TextView tvTitleLeft;
		public TextView tvTitleRight;
		public View vRight;
		public View vLeft;
		
		public MusicHolder(View view) {
			super(view);
			
			tvSingerLeft = (TextView) view.findViewById(R.id.tv_singer_l);
			tvTitleLeft = (TextView) view.findViewById(R.id.tv_title_l);
			ivAlbumPhotoLeft = (ImageView) view.findViewById(R.id.iv_album_photo_l);
			tvSingerRight = (TextView) view.findViewById(R.id.tv_singer_r);
			tvTitleRight = (TextView) view.findViewById(R.id.tv_title_r);
			ivAlbumPhotoRight = (ImageView) view.findViewById(R.id.iv_album_photo_r);
			vLeft = view.findViewById(R.id.ll_l);
			vRight = view.findViewById(R.id.ll_r);
		}
		
	}

}
