package com.myandb.singsong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.myandb.singsong.R;
import com.myandb.singsong.fragment.BaseFragment;
import com.myandb.singsong.service.PlayerService;

public class UpActivity extends BaseActivity {
	
	public static final String EXTRA_FULL_SCREEN = "full_screen";
	public static final String EXTRA_SHOULD_STOP = "should_stop";
	
	private boolean shouldStop;
	private boolean isFullScreen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		isFullScreen = getIntent().getBooleanExtra(EXTRA_FULL_SCREEN, false);
		shouldStop = getIntent().getBooleanExtra(EXTRA_SHOULD_STOP, false);
		if (isFullScreen) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		
		setContentView(R.layout.activity_up);
		
		replaceContentFragmentFromIntent(getIntent());
	}

	@Override
	public void onBackPressed() {
		if (getContentFragment() instanceof BaseFragment) {
			((BaseFragment) getContentFragment()).onBackPressed();
		}
	}

	@Override
	public void onPlayerServiceConnected(PlayerService service) {
		super.onPlayerServiceConnected(service);
		if (shouldStop) {
			service.pause();
		}
	}

	@Override
	public void onPageChanged(Intent intent) {
		if (isComponentOf(intent, RootActivity.class)) {
			// finish all UpActivity
			finish();
			startActivity(intent);
		} else if (isComponentOf(intent, UpActivity.class)) {
			startActivity(intent);
		}
	}

}
