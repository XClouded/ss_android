package com.myandb.singsong.activity;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.ExceptionParser;
import com.google.analytics.tracking.android.ExceptionReporter;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.event.WeakRunnable;
import com.myandb.singsong.service.PlayerService;
import com.myandb.singsong.service.PlayerServiceConnection;
import com.sromku.simple.fb.SimpleFacebook;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.Fragment.InstantiationException;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;

public abstract class BaseActivity extends ActionBarActivity {
	
	public static final String EXTRA_URI_QUERY = "uri_query";
	public static final String EXTRA_FRAGMENT_NAME = "fragment_name";
	public static final String EXTRA_FRAGMENT_BUNDLE = "fragment_bundle";
	public static final String EXTRA_FRAGMENT_ROOT = "fragment_root";
	public static final String PARAM_FRAGMENT_ROOT = "_root";
	
	private PlayerServiceConnection serviceConnection;
	private PlayerService service;
	private Fragment contentFragment;
	private Handler handler;
	private SimpleFacebook simpleFacebook;
	private Drawable backButtonDrawable;
	private Drawable homeButtonDrawable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler(Looper.getMainLooper());
	}

	public void changePage(Intent intent) {
		onPageChanged(intent);
	}
	
	protected void replaceContentFragmentFromIntent(Intent intent) {
		try {
			Fragment fragment = instantiateFragmentFromIntent(intent);
			boolean isRootFragment = intent.getBooleanExtra(EXTRA_FRAGMENT_ROOT, false);
			if (fragment != null) {
				replaceContentFragment(fragment, isRootFragment);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			handler.postDelayed(new WeakRunnable<BaseActivity>(this, "setActionBarIconNoDelay"), 2000);
		}
	}
	
	protected Fragment instantiateFragmentFromIntent(Intent intent) 
			throws InstantiationException, UnsupportedEncodingException {
		String fragmentName = intent.getStringExtra(EXTRA_FRAGMENT_NAME);
		Bundle fragmentBundle = intent.getBundleExtra(EXTRA_FRAGMENT_BUNDLE);
		
		return Fragment.instantiate(this, fragmentName, fragmentBundle);
	}
	
	protected void replaceContentFragment(Fragment fragment, boolean isRootFragment) {
		final int containerId = R.id.fl_content_fragment_container;
		
		FragmentManager manager = getSupportFragmentManager();
		if (isRootFragment) {
			manager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(containerId, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
		
		contentFragment = fragment;
	}
	
	protected Fragment getContentFragment() {
		return contentFragment;
	}
	
	public void setActionBarIconNoDelay() {
		View actionBarView = getActionBarView();
		recursiveSetOnTouchListener(actionBarView);				
	}
	
	private void recursiveSetOnTouchListener(View view) {
		if (view != null) {
			view.setOnTouchListener(noDelayOnTouchListener);
			
			if (view instanceof ViewGroup) {
				ViewGroup viewGroup = (ViewGroup) view;
				for (int i = 0, l = viewGroup.getChildCount(); i < l; i++) {
					View child = viewGroup.getChildAt(i);
					recursiveSetOnTouchListener(child);
				}
			}
		}
	}
	
	@SuppressLint("ClickableViewAccessibility")
	private OnTouchListener noDelayOnTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				view.setPressed(true);
			}
			return false;
		}
	};
	
	public View getActionBarView() {
		return findViewById(getActionViewResId());
	}
	
	private int getActionViewResId() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			return getResources().getIdentifier("abc__action_bar_container", "id", getPackageName());
		} else {
			return Resources.getSystem().getIdentifier("action_bar_container", "id", "android");
		}
	}

	@Override
	public void onBackPressed() {
		if (hasNoBackStackedFragment()) {
			finish();
		} else {
			super.onBackPressed();
		}
	}
	
	public boolean hasNoBackStackedFragment() {
		return getSupportFragmentManager().getBackStackEntryCount() < 2;
	}

	protected boolean isComponentOf(Intent intent, Class<?> clazz) {
		ComponentName component = intent.getComponent();
		if (component != null) {
			return component.getClassName().equals(clazz.getName());
		}
		return false;
	}
	
	public Drawable getHomeButtonDrawable() {
		if (homeButtonDrawable == null) {
			homeButtonDrawable = getResources().getDrawable(R.drawable.ic_action_drawer);
		}
		return homeButtonDrawable;
	}
	
	public Drawable getBackButtonDrawable() {
		if (backButtonDrawable == null) {
			backButtonDrawable = getResources().getDrawable(R.drawable.ic_action_back);
		}
		backButtonDrawable.setAlpha(255);
		return backButtonDrawable;
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
		simpleFacebook = SimpleFacebook.getInstance(this);
	}
	
	private void bindPlayerService() {
		if (serviceConnection == null || !serviceConnection.bind) {
			Intent playerIntent = new Intent(this, PlayerService.class);
			serviceConnection = new PlayerServiceConnection(this);
			getApplicationContext().bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		getSimpleFacebook().onActivityResult(this, requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public SimpleFacebook getSimpleFacebook() {
		if (simpleFacebook == null) {
			simpleFacebook = SimpleFacebook.getInstance(this);
		}
		return simpleFacebook;
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
		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
			handler = null;
		}
		cancelRequests();
		super.onDestroy();
	}
	
	public <T> void addRequest(com.android.volley.Request<T> request) {
		((App) getApplicationContext()).addShortLivedRequest(this, request);
	}
	
	private void cancelRequests() {
		((App) getApplicationContext()).cancelRequests(this);
	}
	
	public void onPlayerServiceConnected(PlayerService service) {
		this.service = service;
	}
	
	public PlayerService getPlayerService() {
		return service;
	}
	
	public Handler getHandler() {
		return handler;
	}
	
	public abstract void onPageChanged(Intent intent);
	
}
