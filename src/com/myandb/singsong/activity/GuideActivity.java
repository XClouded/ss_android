package com.myandb.singsong.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.myandb.singsong.R;
import com.myandb.singsong.fragment.TutorialFragment;

public class GuideActivity extends BaseActivity {
	
	private static final String TAG = "tutorial";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.common_fragment_container);
		
		TutorialFragment fragment = new TutorialFragment();
		replaceFragment(fragment, TAG);
	}

	@Override
	public void onBackPressed() {
		Fragment fragment = getFragment(TAG);
		
		if (fragment != null && fragment instanceof TutorialFragment) {
			((TutorialFragment) fragment).onBackPressed();
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
