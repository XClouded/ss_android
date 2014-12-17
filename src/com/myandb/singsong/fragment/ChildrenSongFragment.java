package com.myandb.singsong.fragment;

import com.google.gson.Gson;
import com.myandb.singsong.R;
import com.myandb.singsong.adapter.ChildrenSongAdapter;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.Utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ChildrenSongFragment extends ListFragment {
	
	public static final String EXTRA_ROOT_SONG = "root_song";
	
	private ImageView ivSongImage;
	private ImageView ivUserPhoto;
	private TextView tvMusicInfo;
	private TextView tvUserNickname;
	private TextView tvUserPart;
	private TextView tvSongMessage;
	private Song thisSong;

	@SuppressLint("InflateParams")
	@Override
	protected View inflateListHeaderView(LayoutInflater inflater) {
		return inflater.inflate(R.layout.fragment_children_song_header, null);
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		String songInJson = bundle.getString(EXTRA_ROOT_SONG);
		Gson gson = Utility.getGsonInstance();
		thisSong = gson.fromJson(songInJson, Song.class);
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		super.onViewInflated(view, inflater);
		
		ivSongImage = (ImageView) view.findViewById(R.id.iv_song_image);
		ivUserPhoto = (ImageView) view.findViewById(R.id.iv_user_photo);
		tvMusicInfo = (TextView) view.findViewById(R.id.tv_music_info);
		tvUserNickname = (TextView) view.findViewById(R.id.tv_user_nickname);
		tvUserPart = (TextView) view.findViewById(R.id.tv_user_part);
		tvSongMessage = (TextView) view.findViewById(R.id.tv_song_message);
	}

	@Override
	protected void initialize(Activity activity) {
		super.initialize(activity);
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.s("songs").s(thisSong.getId()).s("children");
		setUrlBuilder(urlBuilder);
		setInternalAdapter(new ChildrenSongAdapter());
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		super.setupViews(savedInstanceState);
		
		final User thisUser = thisSong.getCreator();
		final Music music = thisSong.getMusic();
		
		tvUserNickname.setText(thisUser.getNickname());
		tvUserPart.setText(thisSong.getPartName());
		tvSongMessage.setText(thisSong.getCroppedMessage());
		tvMusicInfo.setText(music.getSingerName());
		tvMusicInfo.append("\n");
		tvMusicInfo.append(music.getWorkedTitle());
		tvMusicInfo.append("\t");
		tvMusicInfo.append("(" + thisSong.getWorkedDuration() + ")");
		
		ImageHelper.displayPhoto(thisUser, ivUserPhoto);
		ImageHelper.displayPhoto(thisSong.getPhotoUrl(), ivSongImage);
		
		ivUserPhoto.setOnClickListener(thisUser.getProfileClickListener());
	}

	@Override
	public void onResume() {
		super.onResume();
		setFadingActionBarTitle(getString(R.string.other_collabo));
	}

}
