package com.myandb.singsong.activity;

import java.lang.ref.WeakReference;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.ExceptionParser;
import com.google.analytics.tracking.android.ExceptionReporter;
import com.myandb.singsong.R;
import com.myandb.singsong.file.Storage;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.service.PlayerService;
import com.myandb.singsong.service.PlayerServiceConnection;
import com.myandb.singsong.service.PlayerService.IPlayStatusCallback;
import com.myandb.singsong.util.ImageHelper;
import com.myandb.singsong.util.Utility;
import com.myandb.singsong.widget.RotateProgressDialog;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public abstract class OldBaseActivity extends FragmentActivity {

	protected static final int NOT_USE_ACTION_BAR = 0;
	
	private static final String TAG = "SingSong";
	
	private ImageView ivAlbumPhoto;
	private ImageView ivLogo;
	private ImageView ivKakaotalk;
	private ImageView ivPlayControl;
	private ImageView ivGnb;
	private TextView tvAlbumInfo;
	private ViewGroup childContainer;
	private PlayerService playerService;
	private PlayerServiceConnection serviceConnection;
	private RotateProgressDialog progressDialog;
	private Intent playerIntent = new Intent("com.myandb.singsong.service.PlayerService");
	
	protected abstract int getChildLayoutResourceId();
	
	protected abstract boolean isRootActivity();
	
	protected abstract boolean enablePlayingThumb();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		if (getChildLayoutResourceId() != NOT_USE_ACTION_BAR) {
			
			setContentView(R.layout.application);
			
			initializeView();
			
			setupView();
		}
	}

	private void initializeView() {
		childContainer = (ViewGroup) findViewById(R.id.fl_container);
		View.inflate(this, getChildLayoutResourceId(), childContainer);
		
		ivGnb = (ImageView) findViewById(R.id.iv_actionbar_gnb);
		ivLogo = (ImageView) findViewById(R.id.iv_actionbar_logo);
		ivAlbumPhoto = (ImageView) findViewById(R.id.iv_actionbar_album_photo);
		ivKakaotalk = (ImageView) findViewById(R.id.iv_actionbar_kakaotalk);
		ivPlayControl = (ImageView) findViewById(R.id.iv_actionbar_play_control);
		
		tvAlbumInfo = (TextView) findViewById(R.id.tv_actionbar_album_info);
	}
	
	private void setupView() {
		if (!isRootActivity()) {
			ivGnb.setImageResource(R.drawable.ic_back_home);
			ivGnb.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(OldBaseActivity.this, MainActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
				
			});
		}
	}
	
	protected void setDrawerOnGnb(final DrawerLayout drawer, final View menu) {
		if (ivGnb != null) {
			ivGnb.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					if (drawer.isDrawerOpen(menu)) {
						drawer.closeDrawers();
					} else {
						drawer.openDrawer(menu);
					}
				}
				
			});
		}
	}
	
	protected void setKakaotalkClickListener(OnClickListener listener) {
		if (ivKakaotalk != null) {
			final String kakaotalkPackageName = "com.kakao.talk";
			
			if (isApplicationInstalled(kakaotalkPackageName)) {
				ivKakaotalk.setVisibility(View.VISIBLE);
				ivKakaotalk.setOnClickListener(listener);
			}
		}
	}
	
	protected void finish(String message) {
		if (message != null && !message.isEmpty()) {
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
		}
		
		finish();
	}
	
	private boolean isApplicationInstalled(String packageName) {
		try {
			getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
		
		return true;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
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
			return "Thread: " + thread + ", Exception: " + org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(throwable);
		}
		
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		
		if (isRootActivity()) {
			startService(playerIntent);
		}
		
		if (serviceConnection == null || !serviceConnection.bind) {
			serviceConnection = new PlayerServiceConnection(this);
			getApplicationContext().bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
		}
	}
	
	public void closeEditText(EditText editText) {
		closeEditText(editText, true);
	}
	
	public void closeEditText(EditText editText, boolean isClear) {
		if (editText != null) {
			if (isClear) {
				editText.setText("");
			}
			
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		}
	}
	
	public void onPlayerConnected(PlayerService service) {
		setService(service);
		
		if (getChildLayoutResourceId() != NOT_USE_ACTION_BAR && enablePlayingThumb()) {
			ivAlbumPhoto.setVisibility(View.VISIBLE); 
			
			final Song currentSong = playerService.getSong();
			if (currentSong != null) {
				ivLogo.setVisibility(View.GONE);
				ivPlayControl.setVisibility(View.VISIBLE);
				tvAlbumInfo.setVisibility(View.VISIBLE);
				
				Storage preferences = new Storage();
				playerService.startPlaying(
						new PlayStatusCallback(this),
						preferences.isPlayerAutoplay(),
						preferences.isPlayerLooping());
				tvAlbumInfo.setText(playerService.getSingerName() + "-" + playerService.getAlbumTitle());
				tvAlbumInfo.setSelected(true);
				ivPlayControl.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						if (playerService.isPlaying()) {
							playerService.pause();
							ivPlayControl.setImageResource(R.drawable.ic_play_basic);
						} else {
							playerService.resume();
							ivPlayControl.setImageResource(R.drawable.ic_pause_basic);
						}						
					}
					
				});
				ivAlbumPhoto.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						playerService.setSong(currentSong);
						
						Intent intent = new Intent(OldBaseActivity.this, PlayerActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						OldBaseActivity.this.startActivity(intent);
					}
					
				});
				
				Music music = currentSong.getMusic();
				if (music != null) {
					ImageHelper.displayPhoto(music.getAlbumPhotoUrl(), ivAlbumPhoto);
				}
			} else {
				ivLogo.setVisibility(View.VISIBLE);
				ivPlayControl.setVisibility(View.GONE);
				tvAlbumInfo.setVisibility(View.GONE);
			}
		}
	}
	
	private static class PlayStatusCallback implements IPlayStatusCallback {
		
		private WeakReference<OldBaseActivity> weakReference;
		
		public PlayStatusCallback(OldBaseActivity reference) {
			weakReference = new WeakReference<OldBaseActivity>(reference);
		}

		@Override
		public void onStatusChange(int status) {
			OldBaseActivity reference = weakReference.get();
			if (reference != null) {
				reference.catchPlayStatusChange(status);
			}
		}
		
	}
	
	public void catchPlayStatusChange(final int status) {
		runOnUiThread(new Runnable() { 
			
			@Override
			public void run() {
				switch (status) {
				case IPlayStatusCallback.START:
					ivPlayControl.setImageResource(R.drawable.ic_pause_basic);
					break;
					
				case IPlayStatusCallback.STOP:
					ivPlayControl.setImageResource(R.drawable.ic_play_basic);
					break;
					
				case IPlayStatusCallback.RESUME:
					if (playerService.isPlaying()) {
						ivPlayControl.setImageResource(R.drawable.ic_pause_basic);
					} else {
						ivPlayControl.setImageResource(R.drawable.ic_play_basic);
					}
					break;
				}
			}
			
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		if (serviceConnection != null && serviceConnection.bind) {
			getApplicationContext().unbindService(serviceConnection);
			serviceConnection.bind = false;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		EasyTracker.getInstance(this).activityStop(this);
	}

	@Override
	protected void onDestroy() {
		dismissProgressDialog();
		progressDialog = null;
		
		super.onDestroy();
		
		Utility.recursiveRecycle(childContainer);
		
		if (isRootActivity()) {
			stopService(playerIntent);
		}
	}
	
	@Override
	public void onBackPressed() {
		if (!isRootActivity()) {
			finish();
		}
	}
	
	public void replaceFragment(Fragment fragment) {
		replaceFragment(fragment, R.id.fl_fragment_container, null);
	}
	
	public void replaceFragment(Fragment fragment, String tag) {
		replaceFragment(fragment, R.id.fl_fragment_container, tag);
	}
	
	protected void replaceFragment(Fragment fragment, int containerResourceId) {
		replaceFragment(fragment, containerResourceId, null);
	}
	
	protected void replaceFragment(Fragment fragment, int containerResourceId, String tag) {
		getSupportFragmentManager().beginTransaction()
		   						   .replace(containerResourceId, fragment, tag)
		   						   .commit();		
	}
	
	protected Fragment getFragment(String tag) {
		return getSupportFragmentManager().findFragmentByTag(tag);
	}
	
	protected void setService(PlayerService service) {
		playerService = service;
	}
	
	public PlayerService getService() {
		return playerService;
	}
	
	public void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new RotateProgressDialog(this);
		}
		
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}
	}
	
	public boolean dismissProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
			return true;
		} else {
			return false;
		}
	}
	
	protected void log(String message) {
		Log.e(TAG, message);
	}
	
	protected void log(int value) {
		Log.e(TAG, "value : " + String.valueOf(value));
	}
	
	protected void log(float value) {
		Log.e(TAG, "value : " + String.valueOf(value));
	}

}
