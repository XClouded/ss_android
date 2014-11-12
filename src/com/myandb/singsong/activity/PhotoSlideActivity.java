package com.myandb.singsong.activity;

import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.myandb.singsong.R;
import com.myandb.singsong.image.ImageHelper;

public class PhotoSlideActivity extends OldBaseActivity {
	
	public static final String INTENT_PHOTO_URL = "_photo_";
	
	private ImageView ivFullSizePhoto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_photo_slide);
		
		ivFullSizePhoto = (ImageView) findViewById(R.id.iv_full_size_photo);
		
		String photoUrl = getIntent().getStringExtra(INTENT_PHOTO_URL);
		if (photoUrl != null) {
			ImageHelper.displayPhoto(photoUrl, ivFullSizePhoto);
		} else {
			finish();
		}
	}

	@Override
	protected int getChildLayoutResourceId() {
		return NOT_USE_ACTION_BAR;
	}

	@Override
	protected boolean isRootActivity() {
		return false;
	}

	@Override
	protected boolean enablePlayingThumb() {
		return false;
	}

}
