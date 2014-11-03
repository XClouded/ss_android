package com.myandb.singsong.activity;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.ExceptionParser;
import com.google.analytics.tracking.android.ExceptionReporter;
import com.myandb.singsong.R;
import com.myandb.singsong.fragment.BaseFragment;
import com.myandb.singsong.service.PlayerService;
import com.myandb.singsong.service.PlayerServiceConnection;

import android.app.Service;
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
	
	public static final String EXTRA_URI_QUERY = "query";
	
	protected Intent playerIntent = new Intent(this, PlayerService.class);
	
	private PlayerServiceConnection serviceConnection;
	private boolean shouldLastestFragmentAddToBackStack = false;
	
	protected Fragment getFragmentFromIntent(Intent intent) {
		if (intent != null) {
			if (Intent.ACTION_VIEW.equals(intent.getAction())) {
				Uri uri = intent.getData();
				if (uri != null) {
					try {
						return instantiateFragmentFromUri(uri);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return null;
	}
	
	private Fragment instantiateFragmentFromUri(Uri uri) throws InstantiationException {
		String name = getFragmentNameFromUri(uri);
		Bundle bundle = getQueryBundleFromUri(uri);
		
		return Fragment.instantiate(this, name, bundle);
	}
	
	private String getFragmentNameFromUri(Uri uri) {
		final String FRAGMENT_PACKAGE = "fragment";
		
		String fullName = getPackageName();
		fullName += ".";
		fullName += FRAGMENT_PACKAGE;
		fullName += ".";
		fullName += uri.getFragment();
		
		return fullName;
	}
	
	private Bundle getQueryBundleFromUri(Uri uri) {
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_URI_QUERY, uri.getQuery());
		
		return bundle;
	}
	
	protected void setFragment(Fragment fragment) {
		final int containerId = R.id.fl_fragment_container;
		final int transition = FragmentTransaction.TRANSIT_FRAGMENT_FADE;
		
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(containerId, fragment);
		transaction.setTransition(transition);
		
		if (shouldLastestFragmentAddToBackStack) {
			transaction.addToBackStack(null);
		}
		
		transaction.commit();
		
		if (fragment instanceof BaseFragment) {
			shouldLastestFragmentAddToBackStack = ((BaseFragment) fragment).addToBackStack();
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (getFragmentBackStackEntryCount() > 0) {
			popFragmentBackStack();
		} else {
			finish();
		}
	}

	protected boolean popFragmentBackStack() {
		return getSupportFragmentManager().popBackStackImmediate();
	}
	
	protected int getFragmentBackStackEntryCount() {
		return getSupportFragmentManager().getBackStackEntryCount();
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
		// dismissProgressDialog
		// recursiveRecycle
	}
	
	protected abstract void onPlayerServiceConnected(Service service);
	
}
