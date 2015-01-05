package com.myandb.singsong.dialog;

import com.google.gson.Gson;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.fragment.MusicDetailFragment;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.util.Utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectRecordModeDialog extends BaseDialog {
	
	public static final String EXTRA_MUSIC = "music";
	
	private ImageView ivAlbumPhoto;
	private TextView tvSingerName;
	private TextView tvMusicTitle;
	private Button btnSingAlone;
	private Button btnSingDuet;
	private Music music;

	@Override
	protected int getResourceId() {
		return R.layout.dialog_select_record_mode;
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
		ivAlbumPhoto = (ImageView) view.findViewById(R.id.iv_album_photo);
		tvSingerName = (TextView) view.findViewById(R.id.tv_singer_name);
		tvMusicTitle = (TextView) view.findViewById(R.id.tv_music_title);
		btnSingAlone = (Button) view.findViewById(R.id.btn_sing_alone);
		btnSingDuet = (Button) view.findViewById(R.id.btn_sing_duet);
	}

	@Override
	protected void styleDialog(Dialog dialog) {
		super.styleDialog(dialog);
		dialog.getWindow().getAttributes().dimAmount = 0.8f;
	}

	@Override
	protected void initialize(Activity activity) {}

	@Override
	protected void setupViews() {
		if (music != null) {
			tvSingerName.setText(music.getSingerName());
			tvMusicTitle.setText(music.getTitle());
			if (Authenticator.isLoggedIn()) {
				btnSingAlone.setOnClickListener(music.getRecordClickListener());
			} else {
				btnSingAlone.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						LoginDialog dialog = new LoginDialog();
						dialog.show(getChildFragmentManager(), "login");
					}
				});
			}
			btnSingDuet.setOnClickListener(waitingClickListner);
			
			ImageHelper.displayPhoto(music.getAlbumPhotoUrl(), ivAlbumPhoto); 
		} else {
			dismiss();
		}
	}
	
	private OnClickListener waitingClickListner = new OnClickListener() {

		@Override
		public void onClick(View v) {
			dismiss();
			Gson gson = Utility.getGsonInstance();
			Bundle bundle = new Bundle();
			bundle.putString(MusicDetailFragment.EXTRA_MUSIC, gson.toJson(music));
			Intent intent = new Intent(v.getContext(), RootActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, MusicDetailFragment.class.getName());
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			((BaseActivity) getActivity()).changePage(intent);
		}
		
	};

}
