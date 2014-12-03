package com.myandb.singsong.activity;

import com.google.android.gcm.GCMRegistrar;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.myandb.singsong.GCMIntentService;
import com.myandb.singsong.R;
import com.myandb.singsong.dialog.LoginDialog;
import com.myandb.singsong.fragment.DrawerFragment;
import com.myandb.singsong.fragment.HomeFragment;
import com.myandb.singsong.service.PlayerService;
import com.myandb.singsong.widget.SlidingPlayerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

public class RootActivity extends BaseActivity {
	
	public static final String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";
	public static final String EXTRA_NOTICE_ID = "notice_id";
	
	private SlidingMenu drawer;
	private SlidingPlayerLayout slidingPlayerLayout;
	private LoginDialog loginDialog;
	private TextView tvNotificationCount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_root);
		
		initializePreference();
		
		configureActionBar();
		
		configureSlidingPlayer();
		
		configureDrawer();
		
		startPlayerService();
		
		registerGcm();
		
		showUnreadLatestNotice(getIntent());
		
		replaceContentFragmentFromIntent(getIntent());
	}

	private void initializePreference() {
		final String newPreferenceKey = getNewPreferenceKey();
		boolean readAgain = mustReadPreferenceXmlAgain(newPreferenceKey);
		PreferenceManager.setDefaultValues(this, R.xml.preferences, readAgain);
	}
	
	private String getNewPreferenceKey() {
		return getString(R.string.key_notification);
	}
	
	private boolean mustReadPreferenceXmlAgain(String newPreferenceKey) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		return !preferences.contains(newPreferenceKey);
	}
	
	private void configureActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		setHomeButtonRightMargin(0);
		
		FragmentManager manager = getSupportFragmentManager();
		manager.addOnBackStackChangedListener(onBackStackChangedListener);
	}
	
	@SuppressLint("InlinedApi")
	private void setHomeButtonRightMargin(int pixel) {
		View home = findViewById(android.R.id.home);
		if (home != null) {
			android.view.ViewGroup.LayoutParams layoutParams = home.getLayoutParams();
			if (layoutParams instanceof FrameLayout.LayoutParams) {
				FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) layoutParams;
				lp.rightMargin = pixel;
				home.setLayoutParams(lp);
			}
		}
	}
	
	private OnBackStackChangedListener onBackStackChangedListener = new OnBackStackChangedListener() {
		
		@Override
		public void onBackStackChanged() {
			ActionBar actionBar = getSupportActionBar();
			FragmentManager manager = getSupportFragmentManager();
			if (manager.getBackStackEntryCount() > 1) {
				actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
			} else {
				actionBar.setHomeAsUpIndicator(R.drawable.ic_action_drawer);
			}
		}
	};
	
	private void configureSlidingPlayer() {
		final int panelHeight = getResources().getDimensionPixelSize(R.dimen.player_drag_panel_height);
		slidingPlayerLayout = (SlidingPlayerLayout) findViewById(R.id.sliding_layout);
		slidingPlayerLayout.hideActionBarWhenSliding(true);
		slidingPlayerLayout.setSlidingContainer(R.id.fl_sliding_container);
		slidingPlayerLayout.setPanelHeight(panelHeight);
		slidingPlayerLayout.hidePanel();
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
		drawer.setShadowDrawable(R.drawable.drawer_shadow);
		drawer.setBehindWidthRes(R.dimen.drawer_width);
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
	
	private void registerGcm() {
		try {
			GCMRegistrar.checkDevice(this);
			GCMRegistrar.checkManifest(this);
			String registrationId = GCMRegistrar.getRegistrationId(this);
			
			if ("".equals(registrationId)) {
				GCMRegistrar.register(this, GCMIntentService.PROJECT_ID);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Device does not have package com.google.android.gsf
			// This will not happened
		}
	}
	
	private void showUnreadLatestNotice(Intent intent) {
		int latestNoticeId = intent.getIntExtra(EXTRA_NOTICE_ID, 0);
		if (isExistUnreadLatestNotice(latestNoticeId)) {
			// Show notice
		}
	}
	
	private boolean isExistUnreadLatestNotice(int latestNoticeId) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String key = getString(R.string.key_read_notice_id);
		int readNoticeId = preferences.getInt(key, 0);
		return latestNoticeId > readNoticeId;
	}

	@Override
	protected void onDestroy() {
		if (loginDialog != null) {
			loginDialog.dismiss();
			loginDialog = null;
		}
		slidingPlayerLayout.onDestroy();
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
		
		View notificationView = getActionViewCompat(menu, R.id.action_notification);
		if (notificationView != null) {
			notificationView.setOnClickListener(notificationClickListener);
			tvNotificationCount = (TextView) notificationView.findViewById(R.id.tv_action_notification_count);
			updateNotificationCount();
			registerNotificationCountListener();
		}
		
		return super.onCreateOptionsMenu(menu);
	}
	
	private OnClickListener notificationClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
		}
	};
	
	private View getActionViewCompat(Menu menu, int id) {
		return MenuItemCompat.getActionView(menu.findItem(id));
	}
	
	private void updateNotificationCount() {
		int count = getCurrentNotificationCount();
		setNotificationNum(count, tvNotificationCount);
	}
	
	private int getCurrentNotificationCount() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String key = getString(R.string.key_notification_count);
		return preferences.getInt(key, 0);
	}
	
	private void setNotificationNum(int count, TextView textView) {
		if (textView != null) {
			if (count > 0) {
				count = Math.min(count, 99);
				textView.setVisibility(View.VISIBLE);
				textView.setText(String.valueOf(count));
			} else {
				textView.setVisibility(View.GONE);
			}
		}
	}
	
	private void registerNotificationCountListener() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		preferences.registerOnSharedPreferenceChangeListener(notificationCountChangeListener);
	}
	
	private OnSharedPreferenceChangeListener notificationCountChangeListener = new OnSharedPreferenceChangeListener() {
		
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			String countkey = getString(R.string.key_notification_count);
			if (key.equals(countkey)) {
				updateNotificationCount();
			}
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			FragmentManager manager = getSupportFragmentManager();
			if (manager.getBackStackEntryCount() > 1) {
				onBackPressed();
			} else {
				drawer.toggle();
			}
			return true;
			
		case R.id.action_sing:
			return true;
		
		default:
			return super.onOptionsItemSelected(item);
		}
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
			drawer.showContent();;
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
		super.onPlayerServiceConnected(service);
		slidingPlayerLayout.setPlayerService(service);
	}

	@Override
	public void onPageChanged(Intent intent) {
		if (drawer.isMenuShowing()) {
			drawer.showContent();;
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
	
	public void showLoginDialog() {
		if (loginDialog == null) {
			loginDialog = new LoginDialog(this);
		}
		loginDialog.show();
	}
	
	public void restartActivity() {
		Intent intent = new Intent(this, getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, HomeFragment.class.getName());
		startActivity(intent);
	}

}
