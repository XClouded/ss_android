package com.myandb.singsong.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

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
		isFullScreen = getIntent().getBooleanExtra(EXTRA_FULL_SCREEN, false);
		shouldStop = getIntent().getBooleanExtra(EXTRA_SHOULD_STOP, false);
		if (isFullScreen) {
			setWindowFullScreen();
		}
		
		super.onCreate(savedInstanceState);
		
		try {
			ActionBar actionBar = getSupportActionBar();
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
			
			Drawable drawable = getResources().getDrawable(R.drawable.ic_action_back);
			drawable.setAlpha(255);
			actionBar.setHomeAsUpIndicator(drawable);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		setContentView(R.layout.content);
		
		replaceContentFragmentFromIntent(getIntent());
	}
	
	private void setWindowFullScreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN
		);
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onPageChanged(Intent intent) {
		if (isComponentOf(intent, RootActivity.class)) {
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
		} else if (isComponentOf(intent, UpActivity.class)) {
			startActivity(intent);
			overridePendingTransition(R.anim.slide_right_in, R.anim.hold);
		} else {
			replaceContentFragmentFromIntent(intent);
		}
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_right_out);
	}

}
