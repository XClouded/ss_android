package com.myandb.singsong.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.event.Listeners;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.ImageHelper;

public class MusicBasicAdapter extends AutoLoadAdapter<Music> {
	
	private boolean showNum;
	
	public MusicBasicAdapter(Context context) {
		super(context, Music.class, true);
	}
	
	public MusicBasicAdapter(Context context, UrlBuilder urlBuilder) {
		this(context);
		
		resetRequest(urlBuilder);
	}
	
	public void showListNum(boolean showNum) {
		this.showNum = showNum;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final MusicHolder musicHolder;
		final Music music = (Music) getItem(position);
		
		if (view == null) {
			view = View.inflate(getContext(), R.layout.row_music, null);
			
			musicHolder = new MusicHolder();
			musicHolder.tvSingerName = (TextView) view.findViewById(R.id.tv_singer_name);
			musicHolder.tvMusicTitle = (TextView) view.findViewById(R.id.tv_music_title);
			musicHolder.tvSingNum = (TextView) view.findViewById(R.id.tv_sing_num);
			musicHolder.tvListNum = (TextView) view.findViewById(R.id.tv_list_num);
			musicHolder.ivAlbumPhoto = (ImageView) view.findViewById(R.id.iv_album_photo);
			
			view.setTag(musicHolder);
		} else {
			musicHolder = (MusicHolder) view.getTag();
		}
		
		musicHolder.tvSingerName.setSelected(true);
		musicHolder.tvMusicTitle.setSelected(true);
		
		musicHolder.tvSingerName.setText(music.getSingerName());
		musicHolder.tvMusicTitle.setText(music.getTitle());
		musicHolder.tvSingNum.setText(String.valueOf(music.getWorkedSingNum()));
		
		ImageHelper.displayPhoto(music.getAlbumPhotoUrl(), musicHolder.ivAlbumPhoto);
		
		if (showNum) {
			musicHolder.tvListNum.setVisibility(View.VISIBLE);
			if (position < 99) {
				musicHolder.tvListNum.setText(String.valueOf(position + 1));
			} else {
				musicHolder.tvListNum.setText("-");
			}
		}
		
		view.setOnClickListener(Listeners.getRecordClickListener(getContext(), music));
		
		return view;
	}
	
	private static class MusicHolder {
		
		public TextView tvSingerName,
						tvMusicTitle,
						tvSingNum,
						tvListNum;
		public ImageView ivAlbumPhoto;
		
	}

}
