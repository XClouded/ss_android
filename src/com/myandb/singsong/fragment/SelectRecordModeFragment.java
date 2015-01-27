package com.myandb.singsong.fragment;

import com.google.gson.Gson;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class SelectRecordModeFragment extends BaseFragment {
	
	public static final String EXTRA_MUSIC = "music";
	
	private ImageView ivAlbumPhoto;
	private TextView tvSingerName;
	private TextView tvMusicTitle;
	private Button btnSingDuet;
	private Button btnPartMale;
	private Button btnPartFemale;
	private Music music;

	@Override
	protected int getResourceId() {
		return R.layout.fragment_select_record_mode;
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
		btnSingDuet = (Button) view.findViewById(R.id.btn_sing_duet);
		btnPartMale = (Button) view.findViewById(R.id.btn_part_male);
		btnPartFemale = (Button) view.findViewById(R.id.btn_part_female);
	}

	@Override
	protected void initialize(Activity activity) {
		if (music == null) {
			getActivity().finish();
			return;
		}
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		tvSingerName.setText(music.getSingerName());
		tvMusicTitle.setText(music.getTitle());
		btnPartMale.setText(music.getMalePart());
		btnPartFemale.setText(music.getFemalePart());
		equalizeOnHigherWidth(btnPartMale, btnPartFemale);
		btnSingDuet.setOnClickListener(waitingClickListner);
		btnPartMale.setOnClickListener(music.getMaleRecordClickListener());
		btnPartFemale.setOnClickListener(music.getFemaleRecordClickListener());
		ImageHelper.displayPhoto(music.getAlbumPhotoUrl(), ivAlbumPhoto);
	}
	
	private void equalizeOnHigherWidth(View view1, View view2) {
		view1.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		view2.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		int view1Width = view1.getMeasuredWidth();
		int view2Width = view2.getMeasuredWidth();
		int higherWidth = 0;
		View lowerView = null;
		if (view1Width > view2Width) {
			higherWidth = view1Width;
			lowerView = view2;
		} else if (view1Width < view2Width) {
			higherWidth = view2Width;
			lowerView = view1;
		} else {
			return;
		}
		
		LayoutParams lp = (LayoutParams) lowerView.getLayoutParams();
		lp.width = higherWidth;
		lowerView.requestLayout();
	}

	private OnClickListener waitingClickListner = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Gson gson = Utility.getGsonInstance();
			Bundle bundle = new Bundle();
			bundle.putString(MusicDetailFragment.EXTRA_MUSIC, gson.toJson(music));
			Intent intent = new Intent(v.getContext(), RootActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, MusicDetailFragment.class.getName());
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			startFragment(intent);
		}
		
	};

	@Override
	protected void onDataChanged() {}

	@Override
	public boolean isActionBarEnabled() {
		return false;
	}
}
