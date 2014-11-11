package com.myandb.singsong.adapter;

import com.myandb.singsong.R;
import com.myandb.singsong.event.Listeners;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.net.UrlBuilder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicSquareAdapter extends AutoLoadAdapter<Music> {
	
	public MusicSquareAdapter(Context context, UrlBuilder urlBuilder) {
		super(context, Music.class, false);
		
		resetRequest(urlBuilder);
	}

	@Override
	public int getCount() {
		return Math.round(datas.size() / 2f);
	}

	@Override
	public Music getItem(int position) {
		return datas.get(position * 2);
	}
	
	private Music getNextItem(int position) {
		int next = position * 2 + 1;
		if (next < datas.size()) {
			return datas.get(next);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final MusicHolder musicHolder;
		final Music music = (Music) getItem(position);
		
		if (view == null) {
			view = View.inflate(getContext(), R.layout.row_update, null);
			
			musicHolder = new MusicHolder();
			musicHolder.tvSingerLeft = (TextView) view.findViewById(R.id.tv_singer_l);
			musicHolder.tvTitleLeft = (TextView) view.findViewById(R.id.tv_title_l);
			musicHolder.ivAlbumPhotoLeft = (ImageView) view.findViewById(R.id.iv_album_photo_l);
			musicHolder.tvSingerRight = (TextView) view.findViewById(R.id.tv_singer_r);
			musicHolder.tvTitleRight = (TextView) view.findViewById(R.id.tv_title_r);
			musicHolder.ivAlbumPhotoRight = (ImageView) view.findViewById(R.id.iv_album_photo_r);
			
			musicHolder.vLeft = view.findViewById(R.id.ll_l);
			musicHolder.vRight = view.findViewById(R.id.ll_r);
			
			view.setTag(musicHolder);
		} else {
			musicHolder = (MusicHolder) view.getTag();
		}
		
		musicHolder.tvSingerLeft.setText(music.getSingerName());
		musicHolder.tvTitleLeft.setText(music.getTitle());
		ImageHelper.displayPhoto(music.getAlbumPhotoUrl(), musicHolder.ivAlbumPhotoLeft);
		musicHolder.vLeft.setOnClickListener(Listeners.getRecordClickListener(getContext(), music));
		
		final Music musicRight = getNextItem(position);
		if (musicRight != null) {
			musicHolder.vRight.setVisibility(View.VISIBLE);
			musicHolder.tvSingerRight.setText(musicRight.getSingerName());
			musicHolder.tvTitleRight.setText(musicRight.getTitle());
			ImageHelper.displayPhoto(musicRight.getAlbumPhotoUrl(), musicHolder.ivAlbumPhotoRight);
			musicHolder.vRight.setOnClickListener(Listeners.getRecordClickListener(getContext(), musicRight));
		} else {
			musicHolder.vRight.setVisibility(View.INVISIBLE);
		}
		
		return view;
	}
	
	private static class MusicHolder {
		
		public ImageView ivAlbumPhotoLeft;
		public ImageView ivAlbumPhotoRight;
		public TextView tvSingerLeft;
		public TextView tvSingerRight;
		public TextView tvTitleLeft;
		public TextView tvTitleRight;
		public View vRight;
		public View vLeft;
		
	}

}
