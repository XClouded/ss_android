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

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class ChildrenSongFragment extends ListFragment {
	
	public static final String EXTRA_ROOT_SONG = "root_song";
	
	private ImageView ivUserPhoto;
	private TextView tvUserNickname;
	private TextView tvUserPart;
	private TextView tvSongMessage;
	private TextView tvCollaboNum;
	private Song thisSong;

	@Override
	protected int getFixedHeaderViewResId() {
		return R.layout.fragment_children_song_fixed_header;
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
		
		view = getFixedHeaderView();
		ivUserPhoto = (ImageView) view.findViewById(R.id.iv_user_photo);
		tvUserNickname = (TextView) view.findViewById(R.id.tv_user_nickname);
		tvUserPart = (TextView) view.findViewById(R.id.tv_user_part);
		tvSongMessage = (TextView) view.findViewById(R.id.tv_song_message);
		tvCollaboNum = (TextView) view.findViewById(R.id.tv_collabo_num);
	}

	@Override
	protected ListAdapter instantiateAdapter(Activity activity) {
		return new ChildrenSongAdapter();
	}

	@Override
	protected UrlBuilder instantiateUrlBuilder(Activity activity) {
		return new UrlBuilder().s("songs").s(thisSong.getId()).s("children");
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		super.setupViews(savedInstanceState);
		
		final User thisUser = thisSong.getCreator();
		tvUserNickname.setText(thisUser.getNickname());
		tvUserPart.setText(thisSong.getPartName());
		tvSongMessage.setText(thisSong.getCroppedMessage());
		tvCollaboNum.setText(String.valueOf(thisSong.getCollaboNum()));
		if (thisSong.getLyricPart() == Music.PART_MALE) {
			tvUserPart.setTextColor(getResources().getColor(R.color.primary));
		} else {
			tvUserPart.setTextColor(getResources().getColor(R.color.sub));
		}
		
		ImageHelper.displayPhoto(thisUser, ivUserPhoto);
		ivUserPhoto.setOnClickListener(thisUser.getProfileClickListener());
	}

	@Override
	public void onResume() {
		super.onResume();
		final Music music = thisSong.getMusic();
		if (music != null) {
			setActionBarTitle(music.getTitle() + " - " + music.getSingerName());
		}
	}

}
