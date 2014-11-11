package com.myandb.singsong.pager;

import java.util.List;

import com.myandb.singsong.R;
import com.myandb.singsong.event.Listeners;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Music;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class PopularMusicAdapter extends ViewPagerAdapter {

	private static final String MUSIC_REQUEST_URL = "https://m.facebook.com/photo.php?fbid=566644816750696&set=a.550264828388695.1073741830.503254239756421";
	
	private List<Music> musics;

	public PopularMusicAdapter(Context context, List<Music> musics) {
		super(context);
		
		this.musics = musics;
	}

	@Override
	public int getCount() {
		return musics.size() + 1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view;
		
		if (position < musics.size()) {
			view = getInflater().inflate(R.layout.page_popular_music, null);
			
			ImageView ivAlbumPhoto = (ImageView) view.findViewById(R.id.iv_album_photo);
			TextView tvMusicInfo = (TextView) view.findViewById(R.id.tv_music_info);
			TextView tvMusicSingNum = (TextView) view.findViewById(R.id.tv_music_sing_num);
			
			Music music = musics.get(position);
			
			tvMusicSingNum.setText(music.getWorkedSingNum());
			tvMusicInfo.setText(music.getSingerName());
			tvMusicInfo.append("\n");
			tvMusicInfo.append(music.getWorkedTitle());
			
			ImageHelper.displayPhoto(music.getAlbumPhotoUrl(), ivAlbumPhoto);
			
			view.setOnClickListener(Listeners.getRecordClickListener(getContext(), music));
		} else {
			view = getInflater().inflate(R.layout.page_request_music, null);
			
			view.setOnClickListener(requestClickListener);
		}
		
		container.addView(view, 0);
		
		return view;
	}
	
	private OnClickListener requestClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(MUSIC_REQUEST_URL));
			getContext().startActivity(intent);
		}
	};

}
