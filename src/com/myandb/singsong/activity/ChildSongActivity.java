package com.myandb.singsong.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.myandb.singsong.R;
import com.myandb.singsong.adapter.ChildrenSongAdapter;
import com.myandb.singsong.event.Listeners;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.Utility;

public class ChildSongActivity extends OldBaseActivity {
	
	public static final String INTENT_PARENT_SONG = "_parent_song_";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Gson gson = Utility.getGsonInstance();
		String parentSongInJson = getIntent().getStringExtra(INTENT_PARENT_SONG);
		Song parentSong = gson.fromJson(parentSongInJson, Song.class);
		
		if (parentSong != null) {
			Music music = parentSong.getMusic();
			User user = parentSong.getCreator();
			
			TextView tvMusicInfo = (TextView) findViewById(R.id.tv_music_info);
			TextView tvCreatorNickname = (TextView) findViewById(R.id.tv_parent_user_nickname);
			TextView tvCreatorMessage = (TextView) findViewById(R.id.tv_parent_song_message);
			ImageView ivCreatorImage = (ImageView) findViewById(R.id.iv_parent_song_image);
			ImageView ivCreatorPhoto = (ImageView) findViewById(R.id.iv_parent_user_photo);
			ListView listView = (ListView) findViewById(R.id.lv_full_width);
			
			tvCreatorNickname.setText(user.getNickname());
			tvCreatorMessage.setText(parentSong.getCroppedMessage());
			
			Spannable titleSpannable = new SpannableString(music.getWorkedTitle());
			Utility.getRelativeSizeSpan(titleSpannable, 1.3f);
			Utility.getStyleSpan(titleSpannable, Typeface.BOLD);
			
			tvMusicInfo.setText(music.getSingerName());
			tvMusicInfo.append("\n");
			tvMusicInfo.append(titleSpannable);
			tvMusicInfo.append("\t");
			tvMusicInfo.append("(" + parentSong.getWorkedDuration() + ")");
			
			ImageHelper.displayPhoto(user, ivCreatorPhoto);
			ImageHelper.displayPhoto(parentSong.getPhotoUrl(), ivCreatorImage);
			
			parentSong.setMusic(music);
			ivCreatorImage.setOnClickListener(Listeners.getPlayClickListener(this, parentSong));
			
			ChildrenSongAdapter adapter = new ChildrenSongAdapter();
			listView.setAdapter(adapter);
			
			UrlBuilder builder = new UrlBuilder();
			builder.s("songs").s(parentSong.getId()).s("children");
//			adapter.resetRequest(builder);
		} else {
			finish();
		}
	}

	@Override
	protected int getChildLayoutResourceId() {
		return R.layout.activity_child_song;
	}

	@Override
	protected boolean isRootActivity() {
		return false;
	}

	@Override
	protected boolean enablePlayingThumb() {
		return true;
	}
	
}
