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
import com.myandb.singsong.dialog.SelectPartDialog;
import com.myandb.singsong.event.OnCompleteListener;
import com.myandb.singsong.event.OnCompleteWeakListener;
import com.myandb.singsong.event.OnProgressListener;
import com.myandb.singsong.event.OnProgressWeakListener;
import com.myandb.singsong.event.WeakRunnable;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.DownloadManager;
import com.myandb.singsong.receiver.HeadsetReceiver;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.service.SongUploadService;
import com.myandb.singsong.util.LrcDisplayer;
import com.myandb.singsong.util.PlayCounter;
import com.myandb.singsong.util.StringFormatter;
import com.myandb.singsong.util.Utility;
import com.myandb.singsong.widget.CountViewFactory;
import com.myandb.singsong.widget.SlideAnimation;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextSwitcher;
import android.widget.TextView;

public class KaraokeFragment extends BaseFragment {
	
	public static final String EXTRA_MUSIC = "music";
	public static final String EXTRA_PARENT_SONG = "parent_song";
	public static final int REQUEST_CODE_SETTING = 200;
	
	private static final String FILE_MUSIC_OGG = "music.ogg";
	private static final String FILE_MUSIC_RAW = "music.pcm";
	private static final String FILE_RECORD_RAW = "record.pcm";
	private static final String FILE_LYRIC = "lyric.lrc";
	
	private static boolean running;
	
	private Handler handler;
	private User currentUser;
	private Music music;
	private Song parentSong;
	private Recorder recorder;
	private PcmPlayer player;
	private DownloadManager musicDownloader;
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
	private SelectPartDialog selectorDialog;
	
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
	private ImageView ivThisUserPhoto;
	private ImageView ivParentUserPhoto;
	private ImageView ivThisUserBackground;
	private ImageView ivParentUserBackground;
	private View vLyricWrapper;
	private View vParentUserWrapper;
	private View vInfoWrapper;
	private View vUserWrapper;
	private ProgressBar pbPlayProgress;
	private Button btnControl;
	private ScrollView svLyricScroller;
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
		
		vLyricWrapper = view.findViewById(R.id.ll_lyric_wrapper);
		vParentUserWrapper = view.findViewById(R.id.ll_parent_user_wrapper);
		vInfoWrapper = view.findViewById(R.id.rl_info_wrapper);
		vUserWrapper = view.findViewById(R.id.ll_user_wrapper);
		
		btnControl = (Button) view.findViewById(R.id.btn_record_control);
		pbPlayProgress = (ProgressBar) view.findViewById(R.id.pb_playbar);
		svLyricScroller = (ScrollView) view.findViewById(R.id.sv_lyric_scroller);
		tsLyricStarter = (TextSwitcher) view.findViewById(R.id.ts_lyric_starter);
	}

	@Override
	protected void initialize(Activity activity) {
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		if (SongUploadService.isServiceRunning()) {
			finish(activity, "업로드 중입니다. 잠시 후에 불러주세요 :)");
			return;
		}
		
		if (music == null) {
			finish(activity, "지정된 곡이 없습니다! myandb@myandb.com 으로 문의해주세요.");
			return;
		}
		
		try {
			initializeFiles();
			initializeRecorder();
		} catch (Exception e) {
			finish(activity, "레코딩에 이상이 있습니다! myandb@myandb.com 으로 문의해주세요.");
			return;
		}
		
		initializeDialogs();
		
//		downloadDatas();

		blink = AnimationUtils.loadAnimation(activity, R.anim.blink);
		
		handler = new Handler();
		
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
						if (lrcDisplayer != null) {
							lrcDisplayer.stop();
						}
						
						ivThisUserBackground.clearAnimation();
						
						stopRecording();
						
						if (recorder != null) {
							Intent intent = new Intent(getActivity(), UpActivity.class);
							intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, RecordSettingFragment.class.getName());
							intent.putExtra(UpActivity.EXTRA_FULL_SCREEN, true);
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
		
		Bundle bundle = new Bundle();
		bundle.putString(SelectPartDialog.EXTRA_PART_MALE, music.getMalePart());
		bundle.putString(SelectPartDialog.EXTRA_PART_FEMALE, music.getFemalePart());
		selectorDialog = new SelectPartDialog();
		selectorDialog.setArguments(bundle);
	}
	
	private void downloadDatas() {
		loadingDialog.show(getChildFragmentManager(), "");
		setupLoadingDialogForDownload();
		
		DownloadManager lrcDownloader = new DownloadManager();
		lrcDownloader.start(
				music.getLrcUrl(), lyricFile,
				new OnLyricDownloadCompleteListener(this)
		);
		
		musicDownloader = new DownloadManager();
		musicDownloader.start(
				getAudioUrl(), musicOggFile, 
				new OnAudioDownloadCompleteListener(this), 
				new OnAudioDownloadProgressListener(this)
		);
	}
	
	private static class OnLyricDownloadCompleteListener extends OnCompleteWeakListener<KaraokeFragment> {

		public OnLyricDownloadCompleteListener(KaraokeFragment reference) {
			super(reference);
		}

		@Override
		public void filteredDone(KaraokeFragment reference, Exception e) {
			if (e == null) {
				reference.onLyricDownloadSuccess();
			} else {
				reference.onLyricDownloadFailed();
			}
		}
	}
	
	public void onLyricDownloadSuccess() {
		lrcDisplayer = new LrcDisplayer(lyricFile, getActivity());
		lrcDisplayer.setScrollView(svLyricScroller)
			.setLyricWrapper((ViewGroup) vLyricWrapper)
			.setTextSwitcher(tsLyricStarter)
			.setDynamic(music.isLyricDynamic())
			.ready();
	}
	
	public void onLyricDownloadFailed() {
		finish(getActivity(), "가사를 받는 도중 오류가 발생하였습니다. 잠시 후 다시 이용해주세요.");
	}
	
	private static class OnAudioDownloadCompleteListener extends OnCompleteWeakListener<KaraokeFragment> {

		public OnAudioDownloadCompleteListener(KaraokeFragment reference) {
			super(reference);
		}

		@Override
		public void filteredDone(KaraokeFragment reference, Exception e) {
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
		
		int duration = (int) StringFormatter.getDuration(musicOggFile);
		tvEndTime.setText(StringFormatter.getDuration(duration));
		pbPlayProgress.setMax(duration);
	}
	
	public void onAudioDownloadFailed() {
		finish(getActivity(), "음원을 받는 도중 오류가 발생하였습니다. 잠시 후 다시 이용해주세요.");
	}
	
	private static class OnAudioDownloadProgressListener extends OnProgressWeakListener<KaraokeFragment> {

		public OnAudioDownloadProgressListener(KaraokeFragment reference) {
			super(reference);
		}

		@Override
		public void filteredDone(KaraokeFragment reference, Integer progress) {
			reference.updateLoadingDialog(progress);
		}
		
	}
	
	public void updateLoadingDialog(Integer progress) {
		if (loadingDialog != null && loadingDialog.getDialog().isShowing()) {
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
				if (loadingDialog.getDialog().isShowing()) {
					loadingDialog.updateProgressBar(progress);
					
					if (progress >= 30) {
						loadingDialog.enableControlButton(true);
					}
				}
				
				tvDecodeProgress.setText(progress.toString());
				tvDecodeProgress.append("%");
			}
			
		});
		
		decoder.start(musicOggFile, musicPcmFile);
	}
	
	private void setupLoadingDialogForDownload() {
		loadingDialog.setTitlePrefix("노래 로딩 중입니다...");
		loadingDialog.setControlButtonShown(false);
		loadingDialog.setOnCancelButtonClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (musicDownloader != null) {
					musicDownloader.stop();
				}
				finish(getActivity(), null);
			}
			
		});
	}
	
	private void setupLoadingDialogForDecode() {
		loadingDialog.setTitlePrefix("노래 압축을 풀고 있습니다...");
		loadingDialog.setControlButtonShown(true);
		loadingDialog.enableControlButton(false);
		loadingDialog.setControlButtonText("기다리지 않고 시작하기");
		loadingDialog.setOnCancelButtonClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (decoder != null) {
					decoder.stop();
				}
				finish(getActivity(), null);
			}
		});
		loadingDialog.setOnControlButtonClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				loadingDialog.dismiss();
				
				if (isSolo()) {
					selectorDialog.show(getChildFragmentManager(), "");
				} else {
					prepareRecording();
				}
			}
			
		});
	}
	
	public void prepareRecording() {
		if (receiver != null) {
			if (receiver.isPlugged()) {
				startRecordingWithHeadset();
			} else {
				headsetDialog.show(getChildFragmentManager(), ""); 
			}
		}
	}

	public void onHeadsetPlugged() {
		if (headsetDialog != null && headsetDialog.getDialog().isShowing()) {
			headsetDialog.dismiss();
			prepareRecording();
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
	protected void setupViews() {
		final Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
		final Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out);
		tsLyricStarter.setInAnimation(fadeIn);
		tsLyricStarter.setOutAnimation(fadeOut);
		tsLyricStarter.setFactory(new CountViewFactory(getActivity()));
		
		displayProfile(currentUser, tvThisUserNickname, ivThisUserPhoto);
		if (isSolo()) {
			vParentUserWrapper.setVisibility(View.GONE);
		} else {
			displayProfile(parentSong.getCreator(), tvParentUserNickname, ivParentUserPhoto);
			displayPart(music, parentSong.getLyricPart(), tvParentUserPart, ivParentUserBackground);
			setThisUserPart(parentSong.getPartnerLyricPart());
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
	
	public void updateAudioProgress() {
		if (player != null && player.isPlaying()) {
			int position = (int) player.getCurrentPosition();
			tvStartTime.setText(StringFormatter.getDuration(position));
			pbPlayProgress.setProgress(position);
			
			Runnable r = new WeakRunnable<KaraokeFragment>(this, "updateAudioProgress");
			handler.postDelayed(r, 1000);
		}
	}

	@Override
	protected void onDataChanged() {
		receiver = new HeadsetReceiver(this); 
		onLyricDownloadSuccess();
		prepareRecording();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case REQUEST_CODE_SETTING:
			if (resultCode == Activity.RESULT_FIRST_USER) {
				int musicOffset = data.getIntExtra(SongUploadService.EXTRA_MUSIC_OFFSET, 0);
				int recordOffset = data.getIntExtra(SongUploadService.EXTRA_RECORD_OFFSET, 0);
				int imageId = data.getIntExtra(SongUploadService.EXTRA_IMAGE_ID, 0);
				String message = data.getStringExtra(SongUploadService.EXTRA_SONG_MESSAGE);
				
				Intent intent = new Intent(getActivity(), SongUploadService.class);
				intent.putExtra(SongUploadService.EXTRA_HEADSET_PLUGGED, recorder.isHeadsetPlugged());
				intent.putExtra(SongUploadService.EXTRA_RECORD_PCM_FILE_PATH, recordPcmFile.getAbsolutePath());
				intent.putExtra(SongUploadService.EXTRA_RECORD_OFFSET, recordOffset);
				intent.putExtra(SongUploadService.EXTRA_MUSIC_PCM_FILE_PATH, musicPcmFile.getAbsolutePath());
				intent.putExtra(SongUploadService.EXTRA_MUSIC_OFFSET, musicOffset);
				intent.putExtra(SongUploadService.EXTRA_CREATOR_ID, currentUser.getId());
				intent.putExtra(SongUploadService.EXTRA_MUSIC_ID, music.getId());
				intent.putExtra(SongUploadService.EXTRA_LYRIC_PART, lyricPart);
				intent.putExtra(SongUploadService.EXTRA_IMAGE_ID, imageId);
				intent.putExtra(SongUploadService.EXTRA_SONG_MESSAGE, message);
				
				if (!isSolo()) {
					intent.putExtra(SongUploadService.EXTRA_PARENT_SONG_ID, parentSong.getId());
				}
				
				getActivity().startService(intent);
				finish(getActivity(), null);
			} else if (resultCode == Activity.RESULT_OK) {
				prepareRecording();
			} else if (resultCode == Activity.RESULT_CANCELED) {
				finish(getActivity(), null);
			}
			break;

		default:
			break;
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
		
		if (lrcDisplayer != null) {
			lrcDisplayer.stop();
		}
		
		stopRecording();
		
		stopUpdatingProgressBar();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (musicDownloader != null) {
			musicDownloader.stop();
			musicDownloader = null;
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
		
		stopUpdatingProgressBar();
		
		running = false;
	}
	
	public static boolean isRunning() {
		return running;
	}

	private void stopUpdatingProgressBar() {
		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
		}
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

}
