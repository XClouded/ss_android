package com.myandb.singsong.activity;

import java.lang.ref.WeakReference;

import com.google.gson.Gson;
import com.myandb.singsong.R;
import com.myandb.singsong.audio.Decoder;
import com.myandb.singsong.audio.ISimplePlayCallback;
import com.myandb.singsong.audio.Recorder;
import com.myandb.singsong.dialog.HeadsetDialog;
import com.myandb.singsong.dialog.LoadingDialog;
import com.myandb.singsong.dialog.SelectorDialog;
import com.myandb.singsong.event.OnCompleteListener;
import com.myandb.singsong.event.OnProgressListener;
import com.myandb.singsong.event.OnCompleteWeakListener;
import com.myandb.singsong.event.OnProgressWeakListener;
import com.myandb.singsong.event.WeakRunnable;
import com.myandb.singsong.file.FileManager;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.DownloadManager;
import com.myandb.singsong.receiver.HeadsetReceiver;
import com.myandb.singsong.secure.Auth;
import com.myandb.singsong.service.PlayerService;
import com.myandb.singsong.service.SongUploadService;
import com.myandb.singsong.util.ImageHelper;
import com.myandb.singsong.util.Logger;
import com.myandb.singsong.util.LrcDisplayer;
import com.myandb.singsong.util.TimeHelper;
import com.myandb.singsong.util.Utility;
import com.myandb.singsong.widget.CountViewFactory;
import com.myandb.singsong.widget.SlideAnimation;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextSwitcher;
import android.widget.TextView;

public class RecordMainActivity extends BaseActivity {
	
	public static final String INTENT_MUSIC = "_music_";
	public static final String INTENT_PARENT_SONG = "_parent_song_";
	public static final String INTENT_RESULT_UPLOAD = "_upload_";
	public static final int R_CODE_SETTING = 200;
	
	private static boolean isRunning = false;
	private static int restartNum = 0;
	
	private Handler handler = new Handler();
	private User currentUser;
	private HeadsetDialog headsetDialog;
	private LoadingDialog loadingDialog;
	private SelectorDialog selectorDialog;
	private HeadsetReceiver receiver;
	private Recorder recorder;
	private Decoder decoder;
	private LrcDisplayer lrcDisplayer;
	private DownloadManager audioFile;
	private String audioUrl;
	private String lyricUrl;
	private Song parentSong;
	private Music music;
	private TextSwitcher tsLyricStarter;
	private Button btnControl;
	private TextView tvMusicTitle;
	private TextView tvSingerName;
	private TextView tvThisUserNickname;
	private TextView tvParentUserNickname;
	private TextView tvThisUserPart;
	private TextView tvParentUserPart;
	private TextView tvDecodeProgress;
	private TextView tvStartTime;
	private TextView tvEndTime;
	private TextView tvScrollIndicator;
	private ProgressBar pbPlayProgress;
	private ImageView ivThisUserPhoto;
	private ImageView ivParentUserPhoto;
	private ImageView ivThisUserBackground;
	private ImageView ivParentUserBackground;
	private ScrollView svLyricScroller;
	private View vLyricWrapper;
	private View vParentUserWrapper;
	private View vInfoWrapper;
	private View vUserWrapper;
	private Animation blink;
	private int lyricPart;
	private boolean solo;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		isRunning = true;
		setContentView(R.layout.activity_record_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		if (!SongUploadService.isServiceRunning()) {
			currentUser = Auth.getUser();
			
			initializeData(getIntent());
			
			try {
				initializeRecorder();
			} catch (Exception e) {
				finishActivity("레코딩에 이상이 있습니다!. myandb@myandb.com 으로 문의해주세요!");
			}
			
			initializeView();
			
			setupView();
			
			downloadData();
		} else {
			finishActivity("업로드 중입니다. 잠시 후에 불러주세요 :)");
		}
	}
	
	private void initializeData(Intent intent) {
		Gson gson = Utility.getGsonInstance();
		
		String musicInJson = intent.getStringExtra(INTENT_MUSIC);
		String songInJson = intent.getStringExtra(INTENT_PARENT_SONG);
		
		if (musicInJson != null) {
			music = gson.fromJson(musicInJson, Music.class);
			lyricUrl = music.getLrcUrl();
			audioUrl = music.getAudioUrl();
			solo = true;
		} else if (songInJson != null) {
			parentSong = gson.fromJson(songInJson, Song.class);
			music = parentSong.getMusic();
			lyricUrl = music.getLrcUrl();
			audioUrl = parentSong.getAudioUrl();
			solo = false;
		} else {
			finishActivity("레코딩에 이상이 있습니다!. myandb@myandb.com 으로 문의해주세요!");
		}
	}
	
	private void initializeRecorder() throws Exception {
		recorder = new Recorder(
				FileManager.getSecure(FileManager.MUSIC_RAW),
				FileManager.getSecure(FileManager.VOICE_RAW)
		);
		
		recorder.setOnPlaybackStatusChangeListener(new RecorderStatusCallback(this));
	}
	
	private void initializeView() {
		tvThisUserNickname = (TextView) findViewById(R.id.tv_this_user_nickname);
		tvThisUserPart = (TextView) findViewById(R.id.tv_this_user_part);
		tvParentUserNickname = (TextView) findViewById(R.id.tv_parent_user_nickname);
		tvParentUserPart = (TextView) findViewById(R.id.tv_parent_user_part);
		tvScrollIndicator = (TextView) findViewById(R.id.tv_scroll_indicator);
		tvMusicTitle = (TextView) findViewById(R.id.tv_music_title);
		tvSingerName = (TextView) findViewById(R.id.tv_singer_name);
		tvStartTime = (TextView) findViewById(R.id.tv_play_start_time);
		tvEndTime = (TextView) findViewById(R.id.tv_play_end_time);
		tvDecodeProgress = (TextView) findViewById(R.id.tv_decode_progress);
		
		ivThisUserPhoto = (ImageView) findViewById(R.id.iv_this_user_photo);
		ivParentUserPhoto = (ImageView) findViewById(R.id.iv_parent_user_photo);
		ivThisUserBackground = (ImageView) findViewById(R.id.iv_this_user_bg);
		ivParentUserBackground = (ImageView) findViewById(R.id.iv_parent_user_bg);
		
		btnControl = (Button) findViewById(R.id.btn_record_control);
		pbPlayProgress = (ProgressBar) findViewById(R.id.pb_playbar);
		svLyricScroller = (ScrollView) findViewById(R.id.sv_lyric_scroller);
		tsLyricStarter = (TextSwitcher) findViewById(R.id.ts_lyric_starter);
		
		vLyricWrapper = findViewById(R.id.ll_lyric_wrapper);
		vParentUserWrapper = findViewById(R.id.ll_parent_user_wrapper);
		vInfoWrapper = findViewById(R.id.rl_info_wrapper);
		vUserWrapper = findViewById(R.id.ll_user_wrapper);
		
		headsetDialog = new HeadsetDialog(this);
		loadingDialog = new LoadingDialog(this);
		selectorDialog = new SelectorDialog(this, music.getMalePart(), music.getFemalePart());
	}
	
	private void setupView() {
		blink = AnimationUtils.loadAnimation(this, R.anim.blink);
		
		final Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
		final Animation fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
		tsLyricStarter.setInAnimation(fadeIn);
		tsLyricStarter.setOutAnimation(fadeOut);
		tsLyricStarter.setFactory(new CountViewFactory(this));
		
		displayProfile(currentUser, tvThisUserNickname, ivThisUserPhoto);
		if (!solo) {
			displayProfile(parentSong.getCreator(), tvParentUserNickname, ivParentUserPhoto);
			displayPart(music, parentSong.getLyricPart(), tvParentUserPart, ivParentUserBackground);
			setThisUserPart(parentSong.getPartnerLyricPart());
		} else {
			vParentUserWrapper.setVisibility(View.GONE);
		}
		
		tvMusicTitle.setText(music.getTitle());
		tvSingerName.setText(music.getSingerName());
		
		btnControl.setOnClickListener(controlClickListener);
		vLyricWrapper.setOnClickListener(lyricClickListener);
	}
	
	private void displayProfile(User user, TextView tvNickname, ImageView ivUserPhoto) {
		if (user != null) {
			tvNickname.setText(user.getNickname());
			ImageHelper.displayPhoto(user, ivUserPhoto);
		}
	}
	
	public void setThisUserPart(int part) {
		lyricPart = part;
		
		displayPart(music, lyricPart, tvThisUserPart, ivThisUserBackground);
	}
	
	private void displayPart(Music music, int lyricPart, TextView tvPart, ImageView photoBackground) {
		if (music != null) {
			if (lyricPart == Music.PART_MALE) {
				tvPart.setText(music.getMalePart());
				tvPart.setTextColor(getResources().getColor(R.color.font_highlight));
				
				photoBackground.setImageResource(R.drawable.circle_primary);
			} else if (lyricPart == Music.PART_FEMALE) {
				tvPart.setText(music.getFemalePart());
				tvPart.setTextColor(getResources().getColor(R.color.red_dark));
				
				photoBackground.setImageResource(R.drawable.circle_red);
			}
		}
	}
	
	private OnClickListener controlClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			stopRecording();
			stopUpdatingProgressBar();
		}
	};
	
	private OnClickListener lyricClickListener = new OnClickListener() {
		
		private SlideAnimation slide;
		
		@Override
		public void onClick(View v) {
			if (slide == null) {
				final int marginTop = vUserWrapper.getHeight();
				slide = new SlideAnimation(tvScrollIndicator, marginTop, vInfoWrapper);
				slide.setAnimationListener(new AnimationListener() {
					
					@Override
					public void onAnimationStart(Animation animation) {}
					
					@Override
					public void onAnimationRepeat(Animation animation) {}
					
					@Override
					public void onAnimationEnd(Animation animation) {
						if (slide.isStretched()) {
							tvScrollIndicator.setText("▲");
						} else {
							tvScrollIndicator.setText("▼");
						}
					}
				});
			}
			
			slide.changeDirection();
			tvScrollIndicator.startAnimation(slide);
		}
	};
	
	private void stopUpdatingProgressBar() {
		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
		}
	}
	
	private void stopRecording() {
		if (recorder != null) {
			recorder.stop();
		}
	}
	
	private void downloadData() {
		loadingDialog.show();
		setupLoadingDialogForDownload();
		
		DownloadManager lyricFile = new DownloadManager();
		lyricFile.start(
				lyricUrl,
				FileManager.getSecure(FileManager.LYRIC),
				new OnLyricDownloadCompleteListener(this)
		);
		
		audioFile = new DownloadManager();
		audioFile.start(
				audioUrl, 
				FileManager.getSecure(FileManager.MUSIC_OGG), 
				new OnAudioDownloadCompleteListener(this), 
				new OnAudioDownloadProgressListener(this)
		);
	}
	
	private void setupLoadingDialogForDownload() {
		if (loadingDialog != null) {
			loadingDialog.setTitlePrefix("노래 로딩 중입니다...");
			loadingDialog.showControlButton(false);
			loadingDialog.setOnCancelButtonClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (audioFile != null) {
						audioFile.stop();
					}
					
					finishActivity(null);
				}
				
			});
		} else {
			finishActivity(null);
		}
	}
	
	private void setupLoadingDialogForDecode() {
		if (loadingDialog != null) {
			loadingDialog.setTitlePrefix("노래 압축을 풀고 있습니다...");
			loadingDialog.showControlButton(true);
			loadingDialog.enableControlButton(false);
			loadingDialog.setControlButtonText("기다리지 않고 시작하기");
			loadingDialog.setOnCancelButtonClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (decoder != null) {
						decoder.stop();
					}
					
					finishActivity(null);
				}
			});
			loadingDialog.setOnControlButtonClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					loadingDialog.dismiss();
					
					if (solo) {
						if (selectorDialog != null) {
							selectorDialog.show();
						}
					} else {
						prepareRecording();
					}
				}
				
			});
		} else {
			finishActivity(null);
		}
	}
	
	private static class OnLyricDownloadCompleteListener extends OnCompleteWeakListener<RecordMainActivity> {

		public OnLyricDownloadCompleteListener(RecordMainActivity reference) {
			super(reference);
		}

		@Override
		public void filteredDone(RecordMainActivity reference, Exception e) {
			if (e == null) {
				reference.onLyricDownloadSuccess();
			} else {
				reference.onLyricDownloadFailed();
			}
		}
		
	}
	
	public void onLyricDownloadSuccess() {
		lrcDisplayer = new LrcDisplayer(FileManager.getSecure(FileManager.LYRIC), this);
		lrcDisplayer.setScrollView(svLyricScroller)
			.setLyricWrapper((ViewGroup) vLyricWrapper)
			.setTextSwitcher(tsLyricStarter)
			.setDynamic(music.isLyricDynamic())
			.ready();
	}
	
	public void onLyricDownloadFailed() {
		finishActivity("가사를 받는 도중 오류가 발생하였습니다. 잠시 후 다시 이용해주세요.");
	}
	
	private static class OnAudioDownloadCompleteListener extends OnCompleteWeakListener<RecordMainActivity> {

		public OnAudioDownloadCompleteListener(RecordMainActivity reference) {
			super(reference);
		}

		@Override
		public void filteredDone(RecordMainActivity reference, Exception e) {
			if (e == null) {
				reference.onAudioDownloadSuccess();
			} else {
				reference.onAudioDownloadFailed();
			}
		}
		
	}
	
	public void onAudioDownloadSuccess() {
		setupLoadingDialogForDecode();
		startDecoding();
		
		int duration = (int) TimeHelper.getDuration(FileManager.getSecure(FileManager.MUSIC_OGG));
		tvEndTime.setText(TimeHelper.getDuration(duration));
		pbPlayProgress.setMax(duration);
	}
	
	public void onAudioDownloadFailed() {
		finishActivity("음원을 받는 도중 오류가 발생하였습니다. 잠시 후 다시 이용해주세요.");
	}
	
	private static class OnAudioDownloadProgressListener extends OnProgressWeakListener<RecordMainActivity> {

		public OnAudioDownloadProgressListener(RecordMainActivity reference) {
			super(reference);
		}

		@Override
		public void filteredDone(RecordMainActivity reference, Integer progress) {
			reference.updateLoadingDialog(progress);
		}
		
	}
	
	public void updateLoadingDialog(Integer progress) {
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.updateProgressBar(progress);
		}
	}
	
	private void startDecoding() {
		decoder = new Decoder();
		
		decoder.setOnCompleteListener(new OnCompleteListener() {
			
			@Override
			public void done(Exception e) {
				if (e == null) {
					loadingDialog.enableControlButton(true);
					loadingDialog.setControlButtonText("시작하기!");
				} else {
					e.printStackTrace();
				}
			}
			
		});
		
		decoder.setOnProgressListener(new OnProgressListener() {
			
			@Override
			public void done(final Integer progress) {
				if (loadingDialog.isShowing()) {
					loadingDialog.updateProgressBar(progress);
					
					if (progress >= 30) {
						loadingDialog.enableControlButton(true);
					}
				}
				
				tvDecodeProgress.setText(progress.toString());
				tvDecodeProgress.append("%");
			}
			
		});
		
		decoder.start(
				FileManager.getSecure(FileManager.MUSIC_OGG), 
				FileManager.getSecure(FileManager.MUSIC_RAW)
		);
	}
	
	public void prepareRecording() {
		if (receiver != null) {
			if (receiver.isPlugged()) {
				startRecording(true);
			} else {
				headsetDialog.show();
			}
		}
	}
	
	public void startRecording(boolean headsetPlugged) {
		if (recorder != null) {
			recorder.start(headsetPlugged);
		}
	}
	
	public void onHeadsetPlugged() {
		if (headsetDialog != null && headsetDialog.isShowing()) {
			headsetDialog.dismiss();
			prepareRecording();
		}
	}
	
	public void onHeadsetUnplugged() {
		if (recorder != null && recorder.isRecording()) {
			stopRecording();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case R_CODE_SETTING:
			if (resultCode == Activity.RESULT_OK) {
				boolean doUpload = data.getBooleanExtra(INTENT_RESULT_UPLOAD, false);
				
				if (doUpload) {
					float syncAmount = data.getFloatExtra(SongUploadService.INTENT_SYNC_AMOUNT, 0);
					int imageId = data.getIntExtra(SongUploadService.INTENT_IMAGE_ID, -1);
					String message = data.getStringExtra(SongUploadService.INTENT_MESSAGE);
					
					Intent intent = new Intent("com.myandb.singsong.service.SongUploadService");
					intent.putExtra(SongUploadService.INTENT_HEADSET_PLUGGED, recorder.isHeadsetPlugged());
					intent.putExtra(SongUploadService.INTENT_SYNC_AMOUNT, syncAmount);
					intent.putExtra(SongUploadService.INTENT_CREATOR_ID, currentUser.getId());
					intent.putExtra(SongUploadService.INTENT_MUSIC_ID, music.getId());
					intent.putExtra(SongUploadService.INTENT_LYRIC_PART, lyricPart);
					intent.putExtra(SongUploadService.INTENT_IMAGE_ID, imageId);
					intent.putExtra(SongUploadService.INTENT_MESSAGE, message);
					
					if (!solo) {
						intent.putExtra(SongUploadService.INTENT_PARENT_SONG_ID, parentSong.getId());
					}
					
					startService(intent);
					finishActivity(null);
				} else {
					prepareRecording();
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				finishActivity(null);
			}
			
			break;

		default:
			break;
		}
	}
	
	private static class RecorderStatusCallback implements ISimplePlayCallback {
		
		private WeakReference<RecordMainActivity> weakReference;
		
		public RecorderStatusCallback(RecordMainActivity reference) {
			weakReference = new WeakReference<RecordMainActivity>(reference);
		}

		@Override
		public void onStatusChange(int status) {
			RecordMainActivity reference = weakReference.get();
			if (reference != null) {
				reference.onRecorderStatusChange(status);
			}
		}
		
	}
	
	public void onRecorderStatusChange(final int status) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				switch (status) {
				case ISimplePlayCallback.START:
					if (lrcDisplayer != null) {
						lrcDisplayer.start();
					}
					
					updateAudioProgress();
					
					ivThisUserBackground.startAnimation(blink);
					
					if (music != null) {
						Logger.countAsync(RecordMainActivity.this, "musics", music.getId());
					}
					
					break;
					
				case ISimplePlayCallback.STOP:
					if (lrcDisplayer != null) {
						lrcDisplayer.stop();
					}
					
					ivThisUserBackground.clearAnimation();
					
					if (recorder != null) {
						Intent intent = new Intent(RecordMainActivity.this, RecordSettingActivity.class);
						intent.putExtra(SongUploadService.INTENT_HEADSET_PLUGGED, recorder.isHeadsetPlugged());
						startActivityForResult(intent, R_CODE_SETTING);
					}
					
					break;
				}
			}
		});
		
	}
	
	public void updateAudioProgress() {
		if (recorder != null && recorder.isRecording()) {
			int position = recorder.getCurrentPosition();
			tvStartTime.setText(TimeHelper.getDuration(position));
			pbPlayProgress.setProgress(position);
			
			Runnable r = new ProgressRunnable(this);
			handler.postDelayed(r, 1000);
		}
	}
	
	private static class ProgressRunnable extends WeakRunnable<RecordMainActivity> {

		public ProgressRunnable(RecordMainActivity reference) {
			super(reference);
		}

		@Override
		public void onFilteredRun(RecordMainActivity reference) {
			reference.updateAudioProgress();
		}
		
	}
	
	@Override
	public void onResumeFragments() {
		super.onResumeFragments();
		
		currentUser = Auth.getUser();
		if (receiver == null) {
			receiver = new HeadsetReceiver(this);
		}
		
		IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
		registerReceiver(receiver, filter);
	}

	@Override
	public void onPause() {
		super.onPause();
		
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
	}	
	
	@Override
	protected void onStop() {
		super.onStop();
		
		if (lrcDisplayer != null) {
			lrcDisplayer.stop();
		}
		
		stopRecording();
		stopUpdatingProgressBar();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (audioFile != null) {
			audioFile.stop();
			audioFile = null;
		}
		
		if (decoder != null) {
			decoder.stop();
			decoder = null;
		}
		
		if (headsetDialog != null) {
			headsetDialog.dismiss();
			headsetDialog = null;
		}
		
		if (selectorDialog != null) {
			selectorDialog.dismiss();
			selectorDialog = null;
		}
		
		if (loadingDialog != null) {
			loadingDialog.dismiss();
			loadingDialog = null;
		}
		
		if (recorder != null) {
			recorder.destroy();
			recorder = null;
		}
		
		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
			handler = null;
		}
		
		isRunning = false;
		restartNum = 0;
	}
	
	private void finishActivity(String message) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		startActivity(intent);
		
		finish(message);
	}
	
	@Override
	public void onBackPressed() {}

	@Override
	public void onPlayerConnected(PlayerService service) {
		if (service != null) {
			service.stopPlaying(true);
		}
	}
	
	public static boolean isActivityRunning() {
		return isRunning;
	}
	
	public static void incrementRestartNum() {
		restartNum++;
	}
	
	public static int getRestartNum() {
		return restartNum;
	}

	@Override
	protected int getChildLayoutResourceId() {
		return NOT_USE_ACTION_BAR;
	}

	@Override
	protected boolean isRootActivity() {
		return false;
	}

	@Override
	protected boolean enablePlayingThumb() {
		return false;
	}

}
