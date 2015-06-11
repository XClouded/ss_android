package com.myandb.singsong.activity;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.myandb.singsong.R;
import com.myandb.singsong.dialog.BaseDialog;
import com.myandb.singsong.dialog.FrontNoticeDialog;
import com.myandb.singsong.fragment.DrawerFragment;
import com.myandb.singsong.fragment.HomeFragment;
import com.myandb.singsong.fragment.NotificationFragment;
import com.myandb.singsong.model.Notice;
import com.myandb.singsong.service.PlayerService;
import com.myandb.singsong.util.GsonUtils;
import com.myandb.singsong.widget.SlidingPlayerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

public class RootActivity extends BaseActivity implements OnBackStackChangedListener {
	
	public static final String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";
	public static final String EXTRA_NOTICE = "notice";
	public static final String EXTRA_SHOW_PLAYER = "show_player";
	public static final String EXTRA_SHOW_NOTIFICATION = "show_notification";
	
	private SlidingMenu drawer;
	private SlidingPlayerLayout slidingPlayerLayout;
	private DrawerFragment drawerFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_root);
		
		initializePreference();
		
		addOnBackStackChangedListener();
		
		configureSlidingPlayer();
		
		configureDrawer();
		
		startPlayerService();
		
		showUnreadLatestNotice(getIntent());
		
		setHomeFragment(getIntent());
	}

	private void initializePreference() {
		final String newPreferenceKey = getNewPreferenceKey();
		boolean readAgain = mustReadPreferenceXmlAgain(newPreferenceKey);
		PreferenceManager.setDefaultValues(this, R.xml.preferences, readAgain);
	}
	
	private String getNewPreferenceKey() {
		return getString(R.string.key_publish_facebook);
	}
	
	private boolean mustReadPreferenceXmlAgain(String newPreferenceKey) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		return !preferences.contains(newPreferenceKey);
	}
	
	private void addOnBackStackChangedListener() {
		getSupportFragmentManager().addOnBackStackChangedListener(this);
	}

	@Override
	public void onBackStackChanged() {
		ActionBar actionBar = getSupportActionBar();
		if (hasNoBackStackedFragment()) {
			actionBar.setHomeAsUpIndicator(getHomeButtonDrawable());
		} else {
			actionBar.setHomeAsUpIndicator(getBackButtonDrawable());
		}
	}
	
	private void configureSlidingPlayer() {
		final int panelHeight = getResources().getDimensionPixelSize(R.dimen.player_drag_panel_height);
		slidingPlayerLayout = (SlidingPlayerLayout) findViewById(R.id.sliding_layout);
		slidingPlayerLayout.setSlidingContainer(R.id.fl_sliding_container);
		slidingPlayerLayout.setPanelHeight(panelHeight);
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
		drawer.setShadowDrawable(R.drawable.shadow_drawer);
		drawer.setBehindWidthRes(R.dimen.drawer_width);
		drawer.setFadeDegree(0.35f);
	}
	
	private void replaceDrawerFragment() {
		drawerFragment = new DrawerFragment();
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.fl_drawer_fragment_container, drawerFragment);
		transaction.commit();
	}
	
	private void startPlayerService() {
		Intent playerIntent = new Intent(this, PlayerService.class);
		startService(playerIntent);
	}
	
	private void showUnreadLatestNotice(Intent intent) {
		String noticeInJson = intent.getStringExtra(EXTRA_NOTICE);
		Notice notice = GsonUtils.fromJson(noticeInJson, Notice.class);
		if (notice != null && isUnreadLatestNotice(notice.getId())) {
			saveLatestNoticeId(notice.getId());
			if (isNoticePoppable(notice)) {
				Bundle bundle = new Bundle();
				bundle.putString(FrontNoticeDialog.EXTRA_NOTICE, noticeInJson);
				BaseDialog dialog = new FrontNoticeDialog();
				dialog.setArguments(bundle);
				dialog.show(getSupportFragmentManager(), "");
			}
		}
	}
	
	private boolean isUnreadLatestNotice(int latestNoticeId) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		final String key = getString(R.string.key_read_notice_id);
		final int readNoticeId = preferences.getInt(key, 0);
		return latestNoticeId > readNoticeId;
	}
	
	private boolean isNoticePoppable(Notice notice) {
		Calendar today = getDefaultCalendar();
		return isDateBeforeToday(today, notice.getStartedTime())
				&& isDateAfterToday(today, notice.getFinishTime()); 
	}
	
	private boolean isDateBeforeToday(Calendar today, Date date) {
		return getDefaultCalendar(date).before(today);
	}
	
	private boolean isDateAfterToday(Calendar today, Date date) {
		return getDefaultCalendar(date).after(today);
	}
	
	private Calendar getDefaultCalendar() {
		return Calendar.getInstance(Locale.KOREA);
	}
	
	private Calendar getDefaultCalendar(Date date) {
		Calendar calendar = getDefaultCalendar();
		calendar.setTime(date);
		return calendar;
	}
	
	private void saveLatestNoticeId(int latestNoticeId) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		final String key = getString(R.string.key_read_notice_id);
		preferences.edit().putInt(key, latestNoticeId).commit();
	}
	
	private void setHomeFragment(Intent intent) {
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, HomeFragment.class.getName());
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_ROOT, true);
		changePage(intent);
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		updateDrawer();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		changePage(intent);
		updateDrawer();
	}

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
		
		boolean showPlayer = intent.getBooleanExtra(EXTRA_SHOW_PLAYER, false);
		if (showPlayer) {
			if (!slidingPlayerLayout.isPanelExpanded()) {
				slidingPlayerLayout.expandPanel();
			}
		} else {
			if (slidingPlayerLayout.isPanelExpanded()) {
				slidingPlayerLayout.collapsePanel();
			}
		}
		
		if (isComponentOf(intent, UpActivity.class)) {
			startActivity(intent);
			overridePendingTransition(R.anim.slide_right_in, R.anim.hold);
		} else if (isComponentOf(intent, RootActivity.class)) {
			replaceContentFragmentFromIntent(intent);
		}
		
		boolean showNotification = intent.getBooleanExtra(EXTRA_SHOW_NOTIFICATION, false);
		if (showNotification) {
			showNotificationFragment();
		}
	}
	
	private void showNotificationFragment() {
		getHandler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Intent intent = new Intent(RootActivity.this, UpActivity.class);
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, NotificationFragment.class.getName());
				changePage(intent);
			}
		}, 1000);
	}
	
	public void updateDrawer() {
		if (drawerFragment != null && drawerFragment.isAdded()) {
			drawerFragment.notifyDataChanged();
		}
	}

	@Override
	protected void onDestroy() {
		slidingPlayerLayout.onDestroy();
		super.onDestroy();
	}

}
