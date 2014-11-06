package com.myandb.singsong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.myandb.singsong.R;
import com.myandb.singsong.fragment.BaseFragment;
import com.myandb.singsong.service.PlayerService;

public class UpActivity extends BaseActivity {
	
	public static final String EXTRA_FULL_SCREEN = "full_screen";
	public static final String EXTRA_STOP_PLAYER = "stop_player";
	
	private boolean shouldStop;
	private boolean isFullScreen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		shouldStop = getIntent().getBooleanExtra(EXTRA_STOP_PLAYER, false);
		isFullScreen = getIntent().getBooleanExtra(EXTRA_FULL_SCREEN, false);
		if (isFullScreen) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		
		setContentView(R.layout.activity_up);
		
		replaceContentFragmentFromIntent(getIntent());
	}

	@Override
	public void onPlayerServiceConnected(PlayerService service) {
		if (shouldStop) {
			service.stopPlaying(true);
		}
	}

	@Override
	public void onBackPressed() {
		if (getContentFragment() instanceof BaseFragment) {
			((BaseFragment) getContentFragment()).onBackPressed();
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
