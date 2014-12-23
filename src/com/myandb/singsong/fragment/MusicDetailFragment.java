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
import android.view.View.OnClickListener;
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
	private View currentTab;
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
		
		tvPopular.setOnClickListener(tabClickListener);
		tvRecent.setOnClickListener(tabClickListener);
		tvFriend.setOnClickListener(tabClickListener);
	}

	@Override
	protected void onDataChanged() {
		super.onDataChanged();
		if (currentTab == null) {
			tvPopular.performClick();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		setFadingActionBarTitle(music.getSingerName() + "-" + music.getTitle());
	}
	
	private OnClickListener tabClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (currentTab == null || currentTab.getId() != v.getId()) {
				changeContent(v);
				changeTab(v);
			}
		}
	};
	
	private void changeContent(View v) {
		((ChildrenSongAdapter) getAdapter()).clear();
		UrlBuilder urlBuilder = new UrlBuilder();
		
		switch (v.getId()) {
		case R.id.tv_popular:
			urlBuilder.s("musics").s(music.getId()).s("songs").s("root").p("order", "liking_num");
			break;
			
		case R.id.tv_recent:
			urlBuilder.s("musics").s(music.getId()).s("songs").s("root").p("order", "created_at");
			break;
			
		case R.id.tv_friend:
			urlBuilder.s("followings").s("musics").s(music.getId()).s("songs").s("root");
			break;

		default:
			return;
		}
		
		setUrlBuilder(urlBuilder);
		load();
	}
	
	private void changeTab(View v) {
		View previousTab = currentTab;
		dehighlightTab(previousTab);
		highlightTab(v);
		currentTab = v;
	}
	
	private void highlightTab(View v) {
		if (v != null) {
			int paddingTop = v.getPaddingTop();
			int paddingBottom = v.getPaddingBottom();
			v.setBackgroundResource(R.drawable.tab_selected);
			v.setPadding(0, paddingTop, 0, paddingBottom);
			((TextView) v).setTextColor(getResources().getColor(R.color.font_highlight));
		}
	}
	
	private void dehighlightTab(View v) {
		if (v != null) {
			int paddingTop = v.getPaddingTop();
			int paddingBottom = v.getPaddingBottom();
			v.setBackgroundResource(R.drawable.tab_selector);
			v.setPadding(0, paddingTop, 0, paddingBottom);
			((TextView) v).setTextColor(getResources().getColor(R.color.white));
		}
	}
	
}
