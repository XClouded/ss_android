package com.myandb.singsong.activity;

import com.myandb.singsong.R;
import com.myandb.singsong.service.PlayerService;
import com.myandb.singsong.widget.NavigationDrawer;
import com.myandb.singsong.widget.SlidingPlayerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class RootActivity extends BaseActivity {
	
	public static final String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";
	
	private NavigationDrawer navigationDrawer;
	private SlidingPlayerLayout slidingPlayerLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_root);
		
		setActionBarHomeMode();
		
		configurateSlidingPlayer();
		
		navigationDrawer = NavigationDrawer.instantiateAndAttach(this);
		
		startPlayerService();
	}
	
	private void setActionBarHomeMode() {
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}
	
	private void configurateSlidingPlayer() {
		slidingPlayerLayout = (SlidingPlayerLayout) findViewById(R.id.sliding_layout);
		slidingPlayerLayout.hideActionBarWhenSliding(true);
		slidingPlayerLayout.setSlidingContainer(R.id.fl_sliding_container);
	}
	
	private void startPlayerService() {
		Intent playerIntent = new Intent(this, PlayerService.class);
		startService(playerIntent);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			navigationDrawer.toggle();
			return true;
			
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public void onContentInvisible() {
		navigationDrawer.setSlidingEnabled(false);
	}
	
	public void onContentVisible() {
		navigationDrawer.setSlidingEnabled(true);
	}

	@Override
	public void onBackPressed() {
		if (navigationDrawer.isMenuShowing()) {
			navigationDrawer.toggle();
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

}
