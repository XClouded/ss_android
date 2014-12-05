package com.myandb.singsong.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Session.StatusCallback;
import com.facebook.model.GraphUser;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.ExceptionParser;
import com.google.analytics.tracking.android.ExceptionReporter;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.event.WeakRunnable;
import com.myandb.singsong.model.User;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.service.PlayerService;
import com.myandb.singsong.service.PlayerServiceConnection;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
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
import android.widget.Toast;

public abstract class BaseActivity extends ActionBarActivity {
	
	public static final String EXTRA_URI_QUERY = "uri_query";
	public static final String EXTRA_FRAGMENT_NAME = "fragment_name";
	public static final String EXTRA_FRAGMENT_BUNDLE = "fragment_bundle";
	public static final String EXTRA_FRAGMENT_ROOT = "fragment_root";
	
	private PlayerServiceConnection serviceConnection;
	private PlayerService service;
	private Fragment contentFragment;
	private Handler handler;
	private UiLifecycleHelper uiHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler(Looper.getMainLooper());
		uiHelper = new UiLifecycleHelper(this, sessionStatusCallback);
		uiHelper.onCreate(savedInstanceState);
	}

	private StatusCallback sessionStatusCallback = new StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChanged(session, state, exception);
		}
	};
	
	private void onSessionStateChanged(Session session, SessionState state, Exception exception) {
		if (isLoginUserFacebookActivated() && isFacebookSessionOpened(session)) {
			requestFacebookMe(session);
		}
	}
	
	private boolean isLoginUserFacebookActivated() {
		User user = Authenticator.getUser();
		return user != null && user.isFacebookActivated();
	}
	
	private boolean isFacebookSessionOpened(Session session) {
		return session != null && session.isOpened();
	}
	
	private void requestFacebookMe(Session session) {
		Request.newMeRequest(session, new GraphUserCallback() {
			
			@Override
			public void onCompleted(GraphUser user, Response response) {
				if (user == null) {
					Toast.makeText(getApplicationContext(), "페이스북 세션이 만료되었습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show();
					new Authenticator().logout();
					finish();
				}
			}
		}).executeAsync();
	}

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
		
		handler.postDelayed(new WeakRunnable<BaseActivity>(this, "setActionBarIconNoDelay"), 500);
	}
	
	public void setActionBarIconNoDelay() {
		View actionBarView = getActionBarView();
		recursiveSetOnTouchListener(actionBarView);				
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
	
	@SuppressLint("ClickableViewAccessibility")
	private OnTouchListener noDelayOnTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.setPressed(true);
			}
			return false;
		}
	};
	
	private void recursiveSetOnTouchListener(View v) {
		if (v != null) {
			v.setOnTouchListener(noDelayOnTouchListener);
			
			if (v instanceof ViewGroup) {
				ViewGroup vg = (ViewGroup) v;
				for (int i = 0, l = vg.getChildCount(); i < l; i++) {
					View c = vg.getChildAt(i);
					recursiveSetOnTouchListener(c);
				}
			}
		}
	}
	
	
	private View getActionBarView() {
	    int actionViewResId = 0;
	    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
	        actionViewResId = getResources().getIdentifier(
	                "abs__action_bar_container", "id", getPackageName());
	    } else {
	        actionViewResId = Resources.getSystem().getIdentifier(
	                "action_bar_container", "id", "android");
	    }
	    if (actionViewResId > 0) {
	        return findViewById(actionViewResId);
	    }
	    return null;
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
		
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChanged(session, session.getState(), null);
		}
		uiHelper.onResume();
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
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unbindPlayerService();
		uiHelper.onPause();
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
		uiHelper.onDestroy();
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
	
	public abstract void onPageChanged(Intent intent);
	
}
