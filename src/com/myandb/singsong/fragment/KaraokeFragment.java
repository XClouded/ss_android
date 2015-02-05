package com.myandb.singsong.fragment;

import java.io.File;
import java.io.IOException;

import com.google.gson.Gson;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.UpActivity;
import com.myandb.singsong.audio.Decoder;
import com.myandb.singsong.audio.OnPlayEventListener;
import com.myandb.singsong.audio.PcmPlayer;
import com.myandb.singsong.audio.PlayEvent;
import com.myandb.singsong.audio.Recorder;
import com.myandb.singsong.audio.Track;
import com.myandb.singsong.dialog.HeadsetDialog;
import com.myandb.singsong.dialog.LoadingDialog;
import com.myandb.singsong.event.OnCompleteListener;
import com.myandb.singsong.event.OnProgressListener;
import com.myandb.singsong.event.WeakRunnable;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.DownloadManager;
import com.myandb.singsong.net.DownloadManager.OnDownloadListener;
import com.myandb.singsong.receiver.HeadsetReceiver;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.service.SongUploadService;
import com.myandb.singsong.util.Lrc;
import com.myandb.singsong.util.Lrc.Line.Type;
import com.myandb.singsong.util.DynamicLrcDisplayer;
import com.myandb.singsong.util.LrcDisplayer;
import com.myandb.singsong.util.PlayCounter;
import com.myandb.singsong.util.Reporter;
import com.myandb.singsong.util.StaticLrcDisplayer;
import com.myandb.singsong.util.StringFormatter;
import com.myandb.singsong.util.Utility;
import com.myandb.singsong.util.LrcDisplayer.OnTypeChangeListener;
import com.myandb.singsong.widget.CountViewFactory;
import com.myandb.singsong.widget.SlideAnimation;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;

public class KaraokeFragment extends BaseFragment {
	
	public static final String EXTRA_MUSIC = "music";
	public static final String EXTRA_PARENT_SONG = "parent_song";
	public static final String EXTRA_PART = "part";
	public static final int REQUEST_CODE_SETTING = 200;
	
	private static final String FILE_MUSIC_OGG = "music.ogg";
	private static final String FILE_MUSIC_RAW = "music.pcm";
	private static final String FILE_RECORD_RAW = "record.pcm";
	private static final String FILE_LYRIC = "lyric.lrc";
	
	private static boolean running;
	
	private Handler playHandler;
	private Handler countHandler;
	private User currentUser;
	private Music music;
	private Song parentSong;
	private Recorder recorder;
	private PcmPlayer player;
	private DownloadManager audioDownload;
	private DownloadManager lrcDownload;
	private Decoder decoder;
	private HeadsetReceiver receiver;
	private LrcDisplayer lrcDisplayer;
	private Animation blink;
	private File musicOggFile;
	private File musicPcmFile;
	private File recordPcmFile;
	private File lyricFile;
	private int lyricPart;
	
	private HeadsetDialog headsetDialog;
	private LoadingDialog loadingDialog;
	
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
	private TextView tvRecordControl;
	private ImageView ivThisUserPhoto;
	private ImageView ivParentUserPhoto;
	private ImageView ivThisUserBackground;
	private ImageView ivParentUserBackground;
	private ImageView ivBackground;
	private View vLyricWrapper;
	private View vParentUserWrapper;
	private View vInfoWrapper;
	private View vUserWrapper;
	private ProgressBar pbPlayProgress;
	private TextSwitcher tsLyricStarter;

	@Override
	protected int getResourceId() {
		return R.layout.fragment_karaoke;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		Gson gson = Utility.getGsonInstance();
		
		String musicInJson = bundle.getString(EXTRA_MUSIC);
		String songInJson = bundle.getString(EXTRA_PARENT_SONG);
		
		if (musicInJson != null) {
			music = gson.fromJson(musicInJson, Music.class);
			lyricPart = bundle.getInt(EXTRA_PART);
		} else if (songInJson != null) {
			parentSong = gson.fromJson(songInJson, Song.class);
			music = parentSong.getMusic();
		}
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		tvThisUserNickname = (TextView) view.findViewById(R.id.tv_this_user_nickname);
		tvThisUserPart = (TextView) view.findViewById(R.id.tv_this_user_part);
		tvParentUserNickname = (TextView) view.findViewById(R.id.tv_parent_user_nickname);
		tvParentUserPart = (TextView) view.findViewById(R.id.tv_parent_user_part);
		tvScrollIndicator = (TextView) view.findViewById(R.id.tv_scroll_indicator);
		tvMusicTitle = (TextView) view.findViewById(R.id.tv_music_title);
		tvSingerName = (TextView) view.findViewById(R.id.tv_singer_name);
		tvStartTime = (TextView) view.findViewById(R.id.tv_play_start_time);
		tvEndTime = (TextView) view.findViewById(R.id.tv_play_end_time);
		tvDecodeProgress = (TextView) view.findViewById(R.id.tv_decode_progress);
		
		ivThisUserPhoto = (ImageView) view.findViewById(R.id.iv_this_user_photo);
		ivParentUserPhoto = (ImageView) view.findViewById(R.id.iv_parent_user_photo);
		ivThisUserBackground = (ImageView) view.findViewById(R.id.iv_this_user_bg);
		ivParentUserBackground = (ImageView) view.findViewById(R.id.iv_parent_user_bg);
		ivBackground = (ImageView) view.findViewById(R.id.iv_background);
		
		vLyricWrapper = view.findViewById(R.id.ll_lyric_wrapper);
		vParentUserWrapper = view.findViewById(R.id.ll_parent_user_wrapper);
		vInfoWrapper = view.findViewById(R.id.rl_info_wrapper);
		vUserWrapper = view.findViewById(R.id.ll_user_wrapper);
		
		tvRecordControl = (TextView) view.findViewById(R.id.tv_record_control);
		pbPlayProgress = (ProgressBar) view.findViewById(R.id.pb_playbar);
		tsLyricStarter = (TextSwitcher) view.findViewById(R.id.ts_lyric_starter);
	}

	@Override
	protected void initialize(Activity activity) {
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		if (SongUploadService.isServiceRunning()) {
			finish(activity, getString(R.string.t_alert_uploading));
			return;
		}
		
		if (music == null) {
			finish(activity, getString(R.string.t_critical_recording_error));
			return;
		}
		
		try {
			initializeFiles();
			initializeRecorder();
		} catch (Exception e) {
			finish(activity, getString(R.string.t_critical_recording_error));
			return;
		}
		
		initializeDialogs();
		
		downloadDatas();

		blink = AnimationUtils.loadAnimation(activity, R.anim.blink);
		
		playHandler = new Handler();
		countHandler = new Handler();
		
		currentUser = Authenticator.getUser();
		
		running = true;
	}
	
	private void finish(Activity activity, String message) {
		makeToast(message);
		activity.finish();
	}
	
	private void initializeFiles() {
		File dir = getActivity().getFilesDir();
		musicOggFile = new File(dir, FILE_MUSIC_OGG);
		musicPcmFile = new File(dir, FILE_MUSIC_RAW);
		recordPcmFile = new File(dir, FILE_RECORD_RAW);
		lyricFile = new File(dir, FILE_LYRIC);
	}
	
	private void initializeRecorder() throws IOException, IllegalArgumentException, IllegalStateException {
		Track track = new Track(musicPcmFile, PcmPlayer.CHANNELS);
		player = new PcmPlayer();
		player.addTrack("music", track);
		player.setOnPlayEventListener(onPlayEventListener);
		recorder = new Recorder(recordPcmFile);
		recorder.setBackgroundPlayer(player);
	}
	
	private OnPlayEventListener onPlayEventListener = new OnPlayEventListener() {
		
		@Override
		public void onPlay(final PlayEvent event) {
			getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					switch (event) {
					case PLAY:
						if (lrcDisplayer != null) {
							lrcDisplayer.start();
						}
						
						updateAudioProgress();
						
						ivThisUserBackground.startAnimation(blink);
						
						if (music != null) {
							PlayCounter.countAsync(getActivity(), "musics", music.getId());
						}
						
						break;
						
					case STOP:
						stopRecording();
						
						if (recorder != null) {
							Intent intent = new Intent(getActivity(), UpActivity.class);
							intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, RecordSettingFragment.class.getName());
							Bundle bundle = new Bundle();
							bundle.putBoolean(SongUploadService.EXTRA_HEADSET_PLUGGED, recorder.isHeadsetPlugged());
							bundle.putString(SongUploadService.EXTRA_MUSIC_PCM_FILE_PATH, musicPcmFile.getAbsolutePath());
							bundle.putString(SongUploadService.EXTRA_RECORD_PCM_FILE_PATH, recordPcmFile.getAbsolutePath());
							intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
							startActivityForResult(intent, REQUEST_CODE_SETTING);
						}
						
						break;
						
					default:
						break;
					}
				}
			});
		}
	};
	
	private void initializeDialogs() {
		headsetDialog = new HeadsetDialog();
		loadingDialog = new LoadingDialog();
	}
	
	private void downloadDatas() {
		loadingDialog.show(getChildFragmentManager(), loadingDialog.getClass().getName());
		
		lrcDownload = new DownloadManager();
		lrcDownload.start(music.getLrcUrl(), lyricFile, lyricDownloadListener);
		
		audioDownload = new DownloadManager();
		audioDownload.start(getAudioUrl(), musicOggFile, audioDownloadListener);
	}
	
	private OnDownloadListener lyricDownloadListener = new OnDownloadListener() {

		@Override
		public void onComplete(File file) {
			super.onComplete(file);
			onLyricDownloadSuccess();
		}

		@Override
		public void onError(Exception exception) {
			super.onError(exception);
			onLyricDownloadFailed();
		}
		
	};
	
	private OnDownloadListener audioDownloadListener = new OnDownloadListener() {

		@Override
		public void onComplete(File file) {
			super.onComplete(file);
			onAudioDownloadSuccess();
		}

		@Override
		public void onProgress(Integer progress) {
			super.onProgress(progress);
			updateLoadingDialog(Math.min(50, progress / 2));
		}

		@Override
		public void onError(Exception exception) {
			super.onError(exception);
			onAudioDownloadFailed();
		}
		
	};
	
	public void onLyricDownloadSuccess() {
		try {
			Lrc lrc = new Lrc(lyricFile);
			if (music.isLyricDynamic()) {
				lrcDisplayer = new DynamicLrcDisplayer(getActivity());
			} else {
				lrcDisplayer = new StaticLrcDisplayer(getActivity());
			}
			lrcDisplayer.setLrc(lrc)
				.setWrapper((ViewGroup) vLyricWrapper)
				.setOnTypeChangeListener(typeChangeListener)
				.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private OnTypeChangeListener typeChangeListener = new OnTypeChangeListener() {

		@Override
		public void onChange(Type type) {
			switch (type) {
			case GO:
				updateTextSwitcher(countHandler, 3);
				break;

			default:
				break;
			}
		}
	};
	
	private void updateTextSwitcher(final Handler handler, final int count) {
		if (count < -1) {
			tsLyricStarter.setText("");
			tsLyricStarter.setVisibility(View.GONE);
			return;
		}
		
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if (count > 0) {
					tsLyricStarter.setVisibility(View.VISIBLE);
					tsLyricStarter.setText(String.valueOf(count));
				} else if (count == 0) {
					tsLyricStarter.setText("GO!");
				}
				
				updateTextSwitcher(handler, count - 1);
			}
		}, 1000);
	}
	
	public void onLyricDownloadFailed() {
		finish(getActivity(), getString(R.string.t_critical_download_lyric_error));
	}
	
	public void onAudioDownloadSuccess() {
		startDecoding();
		
		int duration = (int) StringFormatter.getDuration(musicOggFile);
		tvEndTime.setText(StringFormatter.getDuration(duration));
		pbPlayProgress.setMax(duration);
	}
	
	public void onAudioDownloadFailed() {
		finish(getActivity(), getString(R.string.t_critical_download_audio_error));
	}
	
	public void updateLoadingDialog(Integer progress) {
		if (loadingDialog != null) {
			loadingDialog.updateProgressBar(progress);
		}
	}
	
	private void startDecoding() {
		decoder = new Decoder();
		
		decoder.setOnCompleteListener(new OnCompleteListener() {
			
			@Override
			public void done(Exception e) {
				if (loadingDialog == null) {
					return;
				}
				
				if (e == null) {
					loadingDialog.enableControlButton(true);
				} else {
					makeToast(R.string.t_critical_recording_error);
					e.printStackTrace();
				}
			}
			
		});
		
		decoder.setOnProgressListener(new OnProgressListener() {
			
			@Override
			public void onProgress(Integer progress) {
				if (loadingDialog == null) {
					return;
				}
				
				updateLoadingDialog(Math.min(100, 50 + progress / 2));
				if (progress > 40) {
					loadingDialog.enableControlButton(true);
				}
				
				tvDecodeProgress.setText(progress.toString());
				tvDecodeProgress.append("%");
			}
			
		});
		
		decoder.start(musicOggFile, musicPcmFile);
	}
	
	public void prepareRecording() {
		prepareRecording(true);
	}
	
	private void prepareRecording(boolean showDialog) {
		if (receiver != null) {
			if (receiver.isPlugged()) {
				startRecordingWithHeadset();
			} else {
				if (showDialog) {
					try {
						headsetDialog.show(getChildFragmentManager(), "");
					} catch (Exception e) {
						e.printStackTrace();
						Reporter.getInstance(getActivity()).reportExceptionOnAnalytics("headsetDialog", e.getMessage());
						startRecordingWithoutHeadset();
					}
				} else {
					startRecordingWithoutHeadset();
				}
			}
		}
	}

	public void onHeadsetPlugged() {
		if (headsetDialog.getDialog() != null && headsetDialog.getDialog().isShowing()) {
			headsetDialog.dismiss();
			startRecordingWithHeadset();
		} else {
			if (recorder != null && recorder.isRecording()) {
				stopRecording();
			}
		}
	}
	
	public void onHeadsetUnplugged() {
		if (recorder != null && recorder.isRecording()) {
			stopRecording();
		}
	}

	private void stopRecording() {
		if (recorder != null && recorder.isRecording()) {
			recorder.stop();
			stopAnimations();
		}
	}
	
	private void stopAnimations() {
		stopUpdatingProgressBar();
		stopTextSwitcher();
		stopLyricDisplayer();
		stopBackgroundAnimation();
	}
	
	private void stopLyricDisplayer() {
		if (lrcDisplayer != null) {
			lrcDisplayer.stop();
		}
	}

	private void stopUpdatingProgressBar() {
		if (playHandler != null) {
			playHandler.removeCallbacksAndMessages(null);
		}
	}
	
	private void stopTextSwitcher() {
		if (countHandler != null) {
			countHandler.removeCallbacksAndMessages(null);
		}
		
		if (tsLyricStarter != null) {
			tsLyricStarter.setVisibility(View.GONE);
		}
	}
	
	private void stopBackgroundAnimation() {
		if (ivThisUserBackground != null) {
			ivThisUserBackground.clearAnimation();
		}
	}
	
	public void startRecordingWithHeadset() {
		if (recorder != null) {
			recorder.start(true);
		}
	}
	
	public void startRecordingWithoutHeadset() {
		if (recorder != null) {
			recorder.start(false);
		}
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		final Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
		final Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
		tsLyricStarter.setInAnimation(fadeIn);
		tsLyricStarter.setOutAnimation(fadeOut);
		tsLyricStarter.setFactory(new CountViewFactory(getActivity()));
		
		displayBackgroundImage(music);
		
		displayProfile(currentUser, tvThisUserNickname, ivThisUserPhoto);
		if (isSolo()) {
			vParentUserWrapper.setVisibility(View.GONE);
			setThisUserPart(lyricPart);
		} else {
			displayProfile(parentSong.getCreator(), tvParentUserNickname, ivParentUserPhoto);
			displayPart(music, parentSong.getLyricPart(), tvParentUserPart, ivParentUserBackground);
			setThisUserPart(parentSong.getPartnerLyricPart());
		}
		
		tvMusicTitle.setText(music.getTitle());
		tvSingerName.setText(music.getSingerName());
		tvMusicTitle.setSelected(true);
		tvSingerName.setSelected(true);
		
		tvRecordControl.setOnClickListener(controlClickListener);
		vLyricWrapper.setOnClickListener(lyricClickListener);
	}

	private void displayBackgroundImage(Music music) {
		final String url = music.getAlbumPhotoUrl();
		final ImageSize imageSize = new ImageSize(100, 150);
		final int radius = 5;
		ImageHelper.displayBlurPhoto(url, ivBackground, imageSize, radius);
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
				tvPart.setTextColor(getResources().getColor(R.color.sub));
				
				photoBackground.setImageResource(R.drawable.circle_sub);
			}
		}
	}
	
	private OnClickListener controlClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			stopRecording();
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
							tvScrollIndicator.setText("¡ã");
						} else {
							tvScrollIndicator.setText("¡å");
						}
					}
				});
			}
			
			slide.changeDirection();
			tvScrollIndicator.startAnimation(slide);
		}
	};
	
	public void updateAudioProgress() {
		if (player != null && player.isPlaying()) {
			int position = (int) player.getCurrentPosition();
			tvStartTime.setText(StringFormatter.getDuration(position));
			pbPlayProgress.setProgress(position);
			
			Runnable r = new WeakRunnable<KaraokeFragment>(this, "updateAudioProgress");
			playHandler.postDelayed(r, 1000);
		}
	}

	@Override
	protected void onDataChanged() {}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case REQUEST_CODE_SETTING:
			if (resultCode == Activity.RESULT_FIRST_USER) {
				data.setClass(getActivity(), SongUploadService.class);
				data.putExtra(SongUploadService.EXTRA_HEADSET_PLUGGED, recorder.isHeadsetPlugged());
				data.putExtra(SongUploadService.EXTRA_RECORD_PCM_FILE_PATH, recordPcmFile.getAbsolutePath());
				data.putExtra(SongUploadService.EXTRA_MUSIC_PCM_FILE_PATH, musicPcmFile.getAbsolutePath());
				data.putExtra(SongUploadService.EXTRA_CREATOR_ID, currentUser.getId());
				data.putExtra(SongUploadService.EXTRA_MUSIC_ID, music.getId());
				data.putExtra(SongUploadService.EXTRA_LYRIC_PART, lyricPart);
				data.putExtra(SongUploadService.EXTRA_SAMPLE_SKIP_SECOND, getSampleSkipSecond());
				
				if (!isSolo()) {
					data.putExtra(SongUploadService.EXTRA_PARENT_SONG_ID, parentSong.getId());
				}
				
				getActivity().startService(data);
				finish(getActivity(), null);
			} else if (resultCode == Activity.RESULT_OK) {
				prepareRecording(false);
			} else if (resultCode == Activity.RESULT_CANCELED) {
				finish(getActivity(), null);
			}
			break;

		default:
			break;
		}
	}
	
	private float getSampleSkipSecond() {
		if (lrcDisplayer != null) {
			return lrcDisplayer.getSampleSkipSecond();
		} else {
			return 5f;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		currentUser = Authenticator.getUser();
		
		if (receiver == null) {
			receiver = new HeadsetReceiver(this);
		}
		
		IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
		getActivity().registerReceiver(receiver, filter);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		if (receiver != null) {
			getActivity().unregisterReceiver(receiver);
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		stopRecording();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (lrcDownload != null) {
			lrcDownload.stop();
			lrcDownload = null;
		}
		
		if (audioDownload != null) {
			audioDownload.stop();
			audioDownload = null;
		}
		
		if (decoder != null) {
			decoder.stop();
			decoder = null;
		}
		
		if (recorder != null) {
			recorder.release();
			recorder = null;
		}
		
		if (player != null) {
			player.release();
			player = null;
		}
		
		running = false;
	}
	
	public static boolean isRunning() {
		return running;
	}

	private boolean isSolo() {
		return music != null && parentSong == null;
	}
	
	private boolean isCollabo() {
		return music != null && parentSong != null;
	}
	
	private String getAudioUrl() throws IllegalStateException {
		if (isSolo()) {
			return music.getAudioUrl();
		} 
		
		if (isCollabo()) {
			return parentSong.getAudioUrl();
		}
		
		throw new IllegalStateException();
	}

	@Override
	public void onBackPressed() {
		return;
	}

	@Override
	public boolean isActionBarDisabled() {
		return true;
	}

}
