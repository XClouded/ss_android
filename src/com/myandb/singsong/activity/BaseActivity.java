package com.myandb.singsong.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.ExceptionParser;
import com.google.analytics.tracking.android.ExceptionReporter;
import com.myandb.singsong.R;
import com.myandb.singsong.service.PlayerService;
import com.myandb.singsong.service.PlayerServiceConnection;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.Fragment.InstantiationException;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

public abstract class BaseActivity extends ActionBarActivity {
	
	public static final String EXTRA_URI_QUERY = "uri_query";
	public static final String EXTRA_FRAGMENT_NAME = "fragment_name";
	public static final String EXTRA_FRAGMENT_BUNDLE = "fragment_bundle";
	public static final String EXTRA_FRAGMENT_ROOT = "fragment_root";
	
	private PlayerServiceConnection serviceConnection;
	private PlayerService service;
	private Fragment contentFragment;

	public void changePage(Intent intent) {
		onPageChanged(intent);
	}
	
	protected void replaceContentFragmentFromIntent(Intent intent) {
		try {
			Fragment fragment = instantiateFragmentFromIntent(intent);
			boolean isRootFragment = intent.getBooleanExtra(EXTRA_FRAGMENT_ROOT, false);
			replaceContentFragment(fragment, isRootFragment);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected Fragment instantiateFragmentFromIntent(Intent intent) 
			throws InstantiationException, UnsupportedEncodingException, NullPointerException {
		String fragmentName = null;
		Bundle fragmentBundle = null;
		
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			Uri uri = intent.getData();
			fragmentName = uri.getFragment();
			fragmentBundle = getBundleFromQuery(uri.getQuery());
		} else {
			fragmentName = intent.getStringExtra(EXTRA_FRAGMENT_NAME);
			fragmentBundle = intent.getBundleExtra(EXTRA_FRAGMENT_BUNDLE);
		}
		
		return Fragment.instantiate(this, fragmentName, fragmentBundle);
	}
	
	private Bundle getBundleFromQuery(String query) throws UnsupportedEncodingException {
		final String charset = "UTF-8";
		
		Bundle bundle = new Bundle();
		String[] pairs = query.split("&");
		for (String pair : pairs) {
			final int index = pair.indexOf("=");
			final String key = URLDecoder.decode(pair.substring(0, index), charset);
			final String value = URLDecoder.decode(pair.substring(index + 1), charset);
			bundle.putString(key, value);
		}
		return bundle;
	}
	
	protected void replaceContentFragment(Fragment fragment, boolean isRootFragment) {
		final int containerId = R.id.fl_content_fragment_container;
		final int transition = FragmentTransaction.TRANSIT_FRAGMENT_FADE;
		
		FragmentManager manager = getSupportFragmentManager();
		if (isRootFragment) {
			manager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(containerId, fragment);
		transaction.setTransition(transition);
		transaction.addToBackStack(null);
		transaction.commit();
		
		contentFragment = fragment;
	}
	
	protected Fragment getContentFragment() {
		return contentFragment;
	}

	@Override
	public void onBackPressed() {
		FragmentManager manager = getSupportFragmentManager();
		int backStackEntryCount = manager.getBackStackEntryCount();
		boolean isRootFragment = backStackEntryCount < 2;
		if (isRootFragment) {
			finish();
		} else {
			super.onBackPressed();
		}
	}

	protected boolean isComponentOf(Intent intent, Class<?> clazz) {
		ComponentName component = intent.getComponent();
		if (component == null) {
			return false;
		}
		return clazz.getName().equals(component.getClassName());
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		trackUncaughtExceptionUsingEasyTracker();
	}
	
	private void trackUncaughtExceptionUsingEasyTracker() {
		EasyTracker.getInstance(this).activityStart(this);
		
		Thread.UncaughtExceptionHandler uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		if (uncaughtExceptionHandler instanceof ExceptionReporter) {
			ExceptionReporter exceptionReporter = (ExceptionReporter) uncaughtExceptionHandler;
			exceptionReporter.setExceptionParser(new AnalyticsExceptionParser());
		}
	}
	
	private static class AnalyticsExceptionParser implements ExceptionParser {

		@Override
		public String getDescription(String thread, Throwable throwable) {
			return "Thread: " + thread + ", Exception: " + ExceptionUtils.getStackTrace(throwable);
		}
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		bindPlayerService();
	}
	
	private void bindPlayerService() {
		if (serviceConnection == null || !serviceConnection.bind) {
			Intent playerIntent = new Intent(this, PlayerService.class);
			serviceConnection = new PlayerServiceConnection(this);
			getApplicationContext().bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unbindPlayerService();
	}
	
	private void unbindPlayerService() {
		if (serviceConnection != null && serviceConnection.bind) {
			getApplicationContext().unbindService(serviceConnection);
			serviceConnection.bind = false;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		stopEasyTracking();
	}
	
	private void stopEasyTracking() {
		EasyTracker.getInstance(this).activityStop(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// recursiveRecycle
	}
	
	public void onPlayerServiceConnected(PlayerService service) {
		this.service = service;
	}
	
	public PlayerService getPlayerService() {
		return service;
	}
	
	public abstract void onPageChanged(Intent intent);
	
}
