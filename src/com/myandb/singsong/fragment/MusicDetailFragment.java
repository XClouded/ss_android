package com.myandb.singsong.fragment;

import com.myandb.singsong.R;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.pager.MusicDetailPagerAdapter;
import com.myandb.singsong.util.GsonUtils;

import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicDetailFragment extends FragmentPagerFragment {
	
	public static final String EXTRA_MUSIC = "music";
	
	private ImageView ivAlbumPhoto;
	private TextView tvSingerName;
	private TextView tvMusicTitle;
	private TextView tvSingNum;
	private Music music;

	@Override
	protected int getHeaderViewResId() {
		return R.layout.fragment_music_detail_header;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		
		String musicInJson = bundle.getString(EXTRA_MUSIC);
		music = GsonUtils.fromJson(musicInJson, Music.class);
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		super.onViewInflated(view, inflater);
		
		view = getHeaderView();
		ivAlbumPhoto = (ImageView) view.findViewById(R.id.iv_album_photo);
		tvSingerName = (TextView) view.findViewById(R.id.tv_singer_name);
		tvMusicTitle = (TextView) view.findViewById(R.id.tv_music_title);
		tvSingNum = (TextView) view.findViewById(R.id.tv_sing_num);
	}

	@Override
	protected FragmentPagerAdapter instantiatePagerAdapter() {
		MusicDetailPagerAdapter adapter = new MusicDetailPagerAdapter(getChildFragmentManager());
		adapter.setMusicId(music.getId());
		return adapter;
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
		setActionBarTitle(getString(R.string.fragment_music_detail_action_title));
	}
	
}
