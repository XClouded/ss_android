package com.myandb.singsong.activity;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.myandb.singsong.R;
import com.myandb.singsong.fragment.DrawerFragment;
import com.myandb.singsong.service.PlayerService;
import com.myandb.singsong.widget.SlidingPlayerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

public class RootActivity extends BaseActivity {
	
	public static final String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";
	
	private SlidingMenu drawer;
	private SlidingPlayerLayout slidingPlayerLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_root);
		
		changeActionBarHomeMode();
		
		configureSlidingPlayer();
		
		configureDrawer();
		
		startPlayerService();
		
		replaceContentFragmentFromIntent(getIntent());
	}
	
	private void changeActionBarHomeMode() {
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}
	
	private void configureSlidingPlayer() {
		slidingPlayerLayout = (SlidingPlayerLayout) findViewById(R.id.sliding_layout);
		slidingPlayerLayout.hideActionBarWhenSliding(true);
		slidingPlayerLayout.setSlidingContainer(R.id.fl_sliding_container);
	}
	
	private void configureDrawer() {
		instantiateDrawer();
		replaceDrawerFragment();
	}
	
	private void instantiateDrawer() {
		drawer = new SlidingMenu(this, SlidingMenu.SLIDING_WINDOW);
		drawer.setMenu(R.layout.drawer_frame);
		drawer.setMode(SlidingMenu.LEFT);
		drawer.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		drawer.setShadowWidthRes(R.dimen.margin_tiny);
		drawer.setBehindOffsetRes(R.dimen.photo_profile);
		drawer.setFadeDegree(0.35f);
	}
	
	private void replaceDrawerFragment() {
		Fragment fragment = new DrawerFragment();
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.fl_drawer_fragment_container, fragment);
		transaction.commit();
	}
	
	private void startPlayerService() {
		Intent playerIntent = new Intent(this, PlayerService.class);
		startService(playerIntent);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			drawer.toggle();
			return true;
			
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public void onContentInvisible() {
		drawer.setSlidingEnabled(false);
	}
	
	public void onContentVisible() {
		drawer.setSlidingEnabled(true);
	}

	@Override
	public void onBackPressed() {
		if (drawer.isMenuShowing()) {
			drawer.toggle();
			return;
		}
		
		if (slidingPlayerLayout.isPanelExpanded()) {
			slidingPlayerLayout.collapsePanel();
			return;
		}
		
		super.onBackPressed();
	}

	@Override
	public void onPlayerServiceConnected(PlayerService service) {
		slidingPlayerLayout.setPlayerService(service);
	}

	@Override
	public void onPageChanged(Intent intent) {
		if (drawer.isMenuShowing()) {
			drawer.toggle();
		}
		
		if (slidingPlayerLayout.isPanelExpanded()) {
			slidingPlayerLayout.collapsePanel();
		}
		
		if (isComponentOf(intent, UpActivity.class)) {
			startActivity(intent);
		} else if (isComponentOf(intent, RootActivity.class)) {
			replaceContentFragmentFromIntent(intent);
		}
	}

}
