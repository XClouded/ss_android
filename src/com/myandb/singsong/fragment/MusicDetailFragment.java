package com.myandb.singsong.fragment;

import com.google.gson.Gson;
import com.myandb.singsong.R;
import com.myandb.singsong.adapter.ChildrenSongAdapter;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.Utility;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicDetailFragment extends ListFragment {
	
	public static final String EXTRA_MUSIC = "music";
	
	private ImageView ivAlbumPhoto;
	private TextView tvSingerName;
	private TextView tvMusicTitle;
	private TextView tvSingNum;
	private TextView tvPopular;
	private TextView tvRecent;
	private TextView tvFriend;
	private Music music;

	@Override
	protected int getListHeaderViewResId() {
		return R.layout.fragment_music_detail_header;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		Gson gson = Utility.getGsonInstance();
		String musicInJson = bundle.getString(EXTRA_MUSIC);
		music = gson.fromJson(musicInJson, Music.class);
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		super.onViewInflated(view, inflater);
		
		view = getListHeaderView();
		ivAlbumPhoto = (ImageView) view.findViewById(R.id.iv_album_photo);
		tvSingerName = (TextView) view.findViewById(R.id.tv_singer_name);
		tvMusicTitle = (TextView) view.findViewById(R.id.tv_music_title);
		tvSingNum = (TextView) view.findViewById(R.id.tv_sing_num);
		tvPopular = (TextView) view.findViewById(R.id.tv_popular);
		tvRecent = (TextView) view.findViewById(R.id.tv_recent);
		tvFriend = (TextView) view.findViewById(R.id.tv_friend);
	}

	@Override
	protected void initialize(Activity activity) {
		super.initialize(activity);
		
		if (getAdapter() == null) {
			UrlBuilder urlBuilder = new UrlBuilder();
			urlBuilder.s("musics").s(music.getId()).s("songs").s("root");
			setUrlBuilder(urlBuilder);
			setAdapter(new ChildrenSongAdapter());
		}
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		super.setupViews(savedInstanceState);
		
		ImageHelper.displayPhoto(music.getAlbumPhotoUrl(), ivAlbumPhoto);
		tvSingerName.setText(music.getSingerName());
		tvMusicTitle.setText(music.getTitle());
		tvSingNum.setText(music.getWorkedSingNum());
	}

	@Override
	public void onResume() {
		super.onResume();
		
		setFadingActionBarTitle(music.getSingerName() + "-" + music.getTitle());
	}

}
