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

public class MusicAdapter extends HolderAdapter<Music, MusicAdapter.MusicHolder> {
	
	public MusicAdapter() {
		super(Music.class);
	}

	@Override
	public MusicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = View.inflate(parent.getContext(), R.layout.row_music, null);
		return new MusicHolder(view);
	}

	@Override
	public void onBindViewHolder(MusicHolder viewHolder, int position) {
		Music music = getItem(position);
		Context context = viewHolder.view.getContext(); 
		
		viewHolder.tvSingerName.setSelected(true);
		viewHolder.tvMusicTitle.setSelected(true);
		viewHolder.tvSingerName.setText(music.getSingerName());
		viewHolder.tvMusicTitle.setText(music.getTitle());
		viewHolder.tvSingNum.setText(String.valueOf(music.getWorkedSingNum()));
		ImageHelper.displayPhoto(music.getAlbumPhotoUrl(), viewHolder.ivAlbumPhoto);
		
		viewHolder.view.setOnClickListener(Listeners.getRecordClickListener(context, music));
	}

	public static final class MusicHolder extends ViewHolder {
		
		public TextView tvSingerName;
		public TextView tvMusicTitle;
		public TextView tvSingNum;
		public TextView tvListNum;
		public ImageView ivAlbumPhoto;

		public MusicHolder(View view) {
			super(view);
			
			tvSingerName = (TextView) view.findViewById(R.id.tv_singer_name);
			tvMusicTitle = (TextView) view.findViewById(R.id.tv_music_title);
			tvSingNum = (TextView) view.findViewById(R.id.tv_sing_num);
			tvListNum = (TextView) view.findViewById(R.id.tv_list_num);
			ivAlbumPhoto = (ImageView) view.findViewById(R.id.iv_album_photo);
		}
		
	}

}
