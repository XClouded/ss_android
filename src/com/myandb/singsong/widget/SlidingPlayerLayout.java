package com.myandb.singsong.widget;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.android.volley.Request.Method;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.adapter.CommentAdapter;
import com.myandb.singsong.audio.OnPlayEventListener;
import com.myandb.singsong.audio.PlayEvent;
import com.myandb.singsong.audio.StreamPlayer;
import com.myandb.singsong.dialog.WriteCommentDialog;
import com.myandb.singsong.event.ActivateOnlyClickListener;
import com.myandb.singsong.event.Listeners;
import com.myandb.singsong.event.WeakRunnable;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.SongComment;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.GradualLoader;
import com.myandb.singsong.net.GradualLoader.OnLoadCompleteListener;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JustRequest;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.net.JSONObjectSuccessListener;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.service.PlayerService;
import com.myandb.singsong.util.StringFormatter;
import com.myandb.singsong.util.Utility;
import com.nineoldandroids.view.animation.AnimatorProxy;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class SlidingPlayerLayout extends SlidingUpPanelLayout {
	
	private PlayerService service;
	private ViewGroup slidingContainer; 
	private String bitlyUrl;
	private Animation blink;
	private User currentUser;
	private Handler handler;
	private GradualLoader loader;
	private CommentAdapter commentAdapter;
	private boolean like;
	private boolean initialized; 
	private int actionBarHeight;
	
	private WriteCommentDialog commentDialog;
	
	private View vStartCollabo;
	private View vLikeSong;
	private View vWriteComment;
	private View vPartnerMask;
	private View vPartnerWrapper;
	private TextView tvParentUserNickname;
	private TextView tvThisUserNickname;
	private TextView tvParentUserPart;
	private TextView tvThisUserPart;
	private TextView tvParentSongMessage;
	private TextView tvThisSongMessage;
	private TextView tvLikeNum;
	private TextView tvCommentNum;
	private TextView tvStartTime;
	private TextView tvEndTime;
	private TextView tvTargetContent;
	private TextView tvMusicTitle;
	private TextView tvSingerName;
	private ImageView ivParentUserPhoto;
	private ImageView ivThisUserPhoto;
	private ImageView ivParentSongImage;
	private ImageView ivThisSongImage;
	private ImageView ivLikeIcon;
	private ImageView ivPlayControl;
	private ImageView ivLoopControl;
	private ImageView ivAutoplayControl;
	private ImageView ivNeon;
	private ImageView ivChildSong;
	private ImageView ivAlbumPhoto;
	private ImageView ivDragPlayControl;
	private ListView lvComments;
	private SeekBar sbPlay;

	public SlidingPlayerLayout(Context context) {
		super(context);
	}
	
	public SlidingPlayerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SlidingPlayerLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setPlayerService(PlayerService service) {
		this.service = service;
		
		if (!initialized) {
			setSlidingContainer(R.id.fl_sliding_container);
			
			setContentView(R.layout.player_wrapper);
			
			setDragView(R.id.music_info);
			
			setPanelSlideListener(panelSlideListener);
			
			initialize();
			
			registerSharedPreferenceChangeListener();
			
			ivLoopControl.setOnClickListener(loopControlClickListener);
			ivAutoplayControl.setOnClickListener(autoplayControlClickListener);
			ivPlayControl.setOnClickListener(playControlClickListener);
			ivDragPlayControl.setOnClickListener(playControlClickListener);
			sbPlay.setOnSeekBarChangeListener(seekBarChangeListener);
			
			service.getPlayer().setOnPlayEventListener(onPlayEventListener);
			
			initialized = true;
		}
		
		if (service.getSong() != null) {
			showPanel();
			setupViews();
			if (service.getPlayer().isPlaying()) {
				catchPlayStatusChange(PlayEvent.PLAY);
			}
		}
	}
	
	private void initialize() {
		currentUser = Authenticator.getUser();
		
		commentAdapter = new CommentAdapter();
		lvComments.setAdapter(commentAdapter);
		
		loader = new GradualLoader(getContext());
		loader.setListView(lvComments);
		
		handler = new Handler();
		
		blink = new AlphaAnimation(0.0f, 0.5f);
		blink.setDuration(500);
		blink.setRepeatMode(Animation.REVERSE);
		blink.setRepeatCount(Animation.INFINITE);
	}
	
	public void setSlidingContainer(int containerId) {
		if (slidingContainer == null) {
			slidingContainer = (ViewGroup) findViewById(containerId);
		}
	}
	
	public void setContentView(int layoutId) {
		View.inflate(getContext(), layoutId, slidingContainer);
		View header = View.inflate(getContext(), R.layout.player_header, null);
		
		lvComments = (ListView) findViewById(R.id.lv_full_width);
		lvComments.addHeaderView(header);
		
		onViewInflated();
	}
	
	private void onViewInflated() {
		vLikeSong = findViewById(R.id.rl_like_song);
		vStartCollabo = findViewById(R.id.rl_start_collabo);
		vWriteComment = findViewById(R.id.rl_write_comment);
		vPartnerMask = findViewById(R.id.fl_partner_mask);
		vPartnerWrapper = findViewById(R.id.rl_partner_wrapper);
		
		tvParentUserNickname = (TextView) findViewById(R.id.tv_parent_user_nickname);
		tvThisUserNickname = (TextView) findViewById(R.id.tv_this_user_nickname);
		tvParentUserPart = (TextView) findViewById(R.id.tv_parent_user_part);
		tvThisUserPart = (TextView) findViewById(R.id.tv_this_user_part);
		tvParentSongMessage = (TextView) findViewById(R.id.tv_parent_song_message);
		tvThisSongMessage = (TextView) findViewById(R.id.tv_this_song_message);
		tvLikeNum = (TextView) findViewById(R.id.tv_like_num);
		tvCommentNum = (TextView) findViewById(R.id.tv_comment_num);
		tvStartTime = (TextView) findViewById(R.id.tv_play_start_time);
		tvEndTime = (TextView) findViewById(R.id.tv_play_end_time);
		tvTargetContent = (TextView) findViewById(R.id.tv_root_info);
		tvMusicTitle = (TextView) findViewById(R.id.tv_music_title);
		tvSingerName = (TextView) findViewById(R.id.tv_singer_name);
		
		ivParentUserPhoto = (ImageView) findViewById(R.id.iv_parent_user_photo);
		ivThisUserPhoto = (ImageView) findViewById(R.id.iv_this_user_photo);
		ivParentSongImage = (ImageView) findViewById(R.id.iv_parent_song_image);
		ivThisSongImage = (ImageView) findViewById(R.id.iv_this_song_image);
		ivLikeIcon = (ImageView) findViewById(R.id.iv_like_icon);
		ivPlayControl = (ImageView) findViewById(R.id.iv_play_control);
		ivLoopControl = (ImageView) findViewById(R.id.iv_loop_control);
		ivAutoplayControl = (ImageView) findViewById(R.id.iv_autoplay_control);
		ivNeon = (ImageView) findViewById(R.id.iv_neon);
		ivChildSong = (ImageView) findViewById(R.id.iv_child_song);
		ivAlbumPhoto = (ImageView) findViewById(R.id.iv_album_photo);
		ivDragPlayControl = (ImageView) findViewById(R.id.iv_drag_play_control);
		
		sbPlay = (SeekBar) findViewById(R.id.sb_play);
	}
	
	private void setDragView(int dragViewId) {
		View dragView = findViewById(dragViewId);
		setDragView(dragView);
	}
	
	private void registerSharedPreferenceChangeListener() {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		final String keyLooping = getContext().getString(R.string.key_player_looping);
		final String keyAutoplay = getContext().getString(R.string.key_player_autoplay);
		boolean looping = preferences.getBoolean(keyLooping, false);
		boolean autoplay = preferences.getBoolean(keyAutoplay, false);
		
		setPlayerLooping(looping);
		setPlayerAutoplay(autoplay);
		
		preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
	}
	
	private OnSharedPreferenceChangeListener preferenceChangeListener = new OnSharedPreferenceChangeListener() {
		
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			final String keyLooping = getContext().getString(R.string.key_player_looping);
			final String keyAutoplay = getContext().getString(R.string.key_player_autoplay);
			
			if (key.equals(keyLooping)) {
				boolean looping = sharedPreferences.getBoolean(key, false);
				setPlayerLooping(looping);
				showLoopingChangeMessage(looping);
			}
			
			if (key.equals(keyAutoplay)) {
				boolean autoplay = sharedPreferences.getBoolean(key, false);
				setPlayerAutoplay(autoplay);
				showAutoplayChangeMessage(autoplay);
			}
		}
	};
	
	private void setPlayerLooping(boolean looping) {
		service.getPlayer().setLooping(looping);
		if (looping) {
			ivLoopControl.setImageResource(R.drawable.ic_loop_on);
		} else {
			ivLoopControl.setImageResource(R.drawable.ic_loop_off);
		}
	}
	
	private void showLoopingChangeMessage(boolean looping) {
		if (looping) {
			makeToast(R.string.t_enable_replay);
		} else {
			makeToast(R.string.t_disable_replay);
		}
	}
	
	private void setPlayerAutoplay(boolean autoplay) {
		service.getPlayer().setAutoplay(autoplay);
		if (autoplay) {
			ivAutoplayControl.setImageResource(R.drawable.ic_autoplay_on);
		} else {
			ivAutoplayControl.setImageResource(R.drawable.ic_autoplay_off);
		}
	}
	
	private void showAutoplayChangeMessage(boolean autoplay) {
		if (autoplay) {
			makeToast(R.string.t_enable_autoplay);
		} else {
			makeToast(R.string.t_disable_autoplay);
		}
	}
	
	private void makeToast(int stringRes) {
		Toast.makeText(getContext().getApplicationContext(), getContext().getString(stringRes), Toast.LENGTH_SHORT).show();
	}
	
	private OnClickListener loopControlClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
			String key = getContext().getString(R.string.key_player_looping);
			boolean looping = preferences.getBoolean(key, false);
			preferences.edit().putBoolean(key, !looping).commit();
		}
	};
	
	private OnClickListener autoplayControlClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
			String key = getContext().getString(R.string.key_player_autoplay);
			boolean autoplay = preferences.getBoolean(key, false);
			preferences.edit().putBoolean(key, !autoplay).commit();
		}
	};

	private OnClickListener playControlClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			StreamPlayer player = service.getPlayer();
			
			if (player.isPlaying()) {
				player.pause();
			} else {
				player.start();
			}
		}
	};
	
	private OnPlayEventListener onPlayEventListener = new OnPlayEventListener() {
		
		@Override
		public void onPlay(PlayEvent event) {
			catchPlayStatusChange(event);
		}
	};
	
	public void catchPlayStatusChange(final PlayEvent event) {
		Activity activity = (Activity) getContext();
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				switch (event) {
				case LOADING:
					showPanel();
					expandPanel();
					setupViews();
					ivPlayControl.setEnabled(false);
					ivDragPlayControl.setEnabled(false);
					break;
					
				case PREPARED:
					ivDragPlayControl.setEnabled(true);
					ivPlayControl.setEnabled(true);
					break;
					
				case PLAY:
					onProgressUpdate();
					ivPlayControl.setImageResource(R.drawable.ic_pause_neon);
					ivDragPlayControl.setImageResource(R.drawable.ic_pause_basic);
					ivNeon.startAnimation(blink);
					ivNeon.setVisibility(View.VISIBLE);
					break;
					
				case RESUME:
					expandPanel();
					break;
					
				case PAUSE:
				case COMPLETED:
					ivPlayControl.setImageResource(R.drawable.ic_play_neon);
					ivDragPlayControl.setImageResource(R.drawable.ic_play_basic);
					ivNeon.clearAnimation();
					ivNeon.setVisibility(View.INVISIBLE);
					break;
					
				case ERROR:
					break;
					
				default:
					break;
				}
			}
		});
	}
	
	private OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {
		
		private boolean trackManuallyChanged;
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			trackManuallyChanged = true;
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (trackManuallyChanged) {
				StreamPlayer player = service.getPlayer();
				if (player != null && player.isPlaying()) {
					player.seekTo(progress);
					trackManuallyChanged = false;
				}
			}
		}
	};
	
	public void onProgressUpdate() {
		StreamPlayer player = service.getPlayer();
		if (player != null && player.isPlaying()) {
			tvStartTime.setText(StringFormatter.getDuration(player.getCurrentPosition()));
			sbPlay.setProgress(player.getCurrentPosition());
			
			if (handler != null) {
				Runnable r = new WeakRunnable<SlidingPlayerLayout>(this, "onProgressUpdate");
				handler.postDelayed(r, 1000);
			}
		}
	}
	
	private void setupViews() {
		final Song song = service.getSong();
		final User parentUser = song.getParentUser();
		final User thisUser = song.isRoot() ? currentUser : song.getCreator();
		final Music music = song.getMusic();
		
		displayMusicInfo(music);
		
		displaySongImage(song);
		
		displaySongMessage(song);
		
		displayProfile(parentUser, ivParentUserPhoto, tvParentUserNickname);
		
		displayProfile(thisUser, ivThisUserPhoto, tvThisUserNickname);
		
		displaySongPart(song);
		
		displayPartnerMask(song);
		
		displayChildSongIcon(song);
		
		displayTargetContent(parentUser.getNickname(), song.getParentPartName());
		
		displayCommentNum(song.getCommentNum());
		
		displayLikeNum(song.getWorkedLikeNum());
		
		requestNewComments(song);
		
		checkUserLikeSong(currentUser, song);
		
		initializeSeekBar(song);
		
		ivParentUserPhoto.setOnClickListener(Listeners.getProfileClickListener(getContext(), parentUser));
		ivThisUserPhoto.setOnClickListener(Listeners.getProfileClickListener(getContext(), thisUser));
		ivChildSong.setOnClickListener(Listeners.getChildrenClickListener(getContext(), song));
		vStartCollabo.setOnClickListener(Listeners.getCollaboClickListener(getContext(), song));
		vLikeSong.setOnClickListener(likeClickListener);
		vWriteComment.setOnClickListener(writeCommentClickListener);
		
		if (isPanelExpanded()) {
			ivDragPlayControl.setVisibility(View.GONE);
			ivChildSong.setVisibility(View.VISIBLE);
		} else {
			ivDragPlayControl.setVisibility(View.VISIBLE);
			ivChildSong.setVisibility(View.GONE);
		}
	}
	
	private void displayMusicInfo(Music music) {
		if (music != null) {
			tvMusicTitle.setText(music.getTitle());
			tvSingerName.setText(music.getSingerName());
			tvMusicTitle.setSelected(true);
			tvSingerName.setSelected(true);
			ImageHelper.displayPhoto(music.getAlbumPhotoUrl(), ivAlbumPhoto);
		}
	}
	
	private void displaySongImage(Song song) {
		if (song != null) {
			if (song.isRoot()) {
				ImageHelper.displayPhoto(song.getPhotoUrl(), ivParentSongImage);
			} else {
				Song parentSong = song.getParentSong();
				ImageHelper.displayPhoto(parentSong.getPhotoUrl(), ivParentSongImage);
				ImageHelper.displayPhoto(song.getPhotoUrl(), ivThisSongImage);
			}
		}
	}
	
	private void displaySongMessage(Song song) {
		if (song != null) {
			if (song.isRoot()) {
				tvParentSongMessage.setText(song.getCroppedMessage());
			} else {
				Song parentSong = song.getParentSong();
				tvParentSongMessage.setText(parentSong.getMessage());
				tvThisSongMessage.setText(song.getMessage());
			}
		}
	}
	
	private void displayProfile(User user, ImageView ivPhoto, TextView tvNickname) {
		if (user != null) {
			tvNickname.setText(user.getNickname());
			ImageHelper.displayPhoto(user, ivPhoto);
		}
	}
	
	private void displaySongPart(Song song) {
		if (song != null) {
			tvParentUserPart.setText(song.getParentPartName());
			tvThisUserPart.setText(song.getPartName());
		}
	}
	
	private void displayPartnerMask(Song song) {
		if (song != null) {
			if (song.isRoot()) {
				vPartnerMask.setVisibility(View.VISIBLE);
				vPartnerWrapper.setVisibility(View.GONE);
			} else {
				vPartnerMask.setVisibility(View.GONE);
				vPartnerWrapper.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private void displayChildSongIcon(Song song) {
		if (song != null) {
			if (song.isRoot()) {
				ivChildSong.setImageResource(R.drawable.img_collabo);
			} else {
				ivChildSong.setImageResource(R.drawable.img_collabo_other);
			}
		}
	}
	
	private void displayTargetContent(String nickname, String part) {
		final Spannable nicknameSpan = new SpannableString(nickname);
		Utility.getStyleSpan(nicknameSpan, Typeface.BOLD);
		
		final Spannable partSpan = new SpannableString(part);
		Utility.getColorSpan(partSpan, "#6ab8d3");
		
		final Spannable collaboSpan = new SpannableString("콜라보하기!");
		Utility.getStyleSpan(collaboSpan, Typeface.BOLD);
		
		tvTargetContent.setText(nicknameSpan);
		tvTargetContent.append("님이 부른 ");
		tvTargetContent.append(partSpan);
		tvTargetContent.append("와 ");
		tvTargetContent.append(collaboSpan);
	}
	
	private void displayCommentNum(int num) {
		tvCommentNum.setText("댓글 (");
		tvCommentNum.append(String.valueOf(num));
		tvCommentNum.append(")");
	}
	
	private void displayLikeNum(String num) {
		tvLikeNum.setText("좋아요 (");
		tvLikeNum.append(num);
		tvLikeNum.append(")");
	}
	
	private void checkUserLikeSong(User user, Song song) {
		if (user != null && song != null) {
			String segment = new StringBuilder()
				.append("songs/")
				.append(song.getId())
				.append("/likings?user_id=")
				.append(user.getId()).toString();
			
			JSONObjectRequest request = new JSONObjectRequest(
					segment, null,
					new JSONObjectSuccessListener(this, "onGetUserLikeResponse"), 
					new JSONErrorListener(this, "onGetUserLikeError")
			);
			
			((App) getContext().getApplicationContext()).addShortLivedRequest(getContext(), request);
		}
	}
	
	public void onGetUserLikeResponse(JSONObject response) {
		setUserLikeSong(true);
	}
	
	public void onGetUserLikeError() {
		setUserLikeSong(false);
	}
	
	private void setUserLikeSong(boolean like) {
		this.like = like;
		
		if (like) {
			ivLikeIcon.setImageResource(R.drawable.ic_like_pink);
		} else {
			ivLikeIcon.setImageResource(R.drawable.ic_like_inverse);
		}
	}
	
	private void initializeSeekBar(Song song) {
		int duration = song.getDuration();
		
		tvStartTime.setText(StringFormatter.getDuration(0));
		tvEndTime.setText(StringFormatter.getDuration(duration));
		sbPlay.setMax(duration);
		sbPlay.setProgress(0);
	}
	
	private void requestNewComments(Song song) {
		if (commentAdapter != null) {
			commentAdapter.clear();
			
			UrlBuilder urlBuilder = new UrlBuilder();
			urlBuilder.s("songs").s(song.getId()).s("comments");
			loader.setUrlBuilder(urlBuilder);
			loader.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				
				@Override
				public void onComplete(JSONArray response) {
					commentAdapter.addAll(response);
				}
			});
		}
	}

	private OnClickListener likeClickListener = new ActivateOnlyClickListener() {
		
		@Override
		public void onActivated(View v, User user) {
			final Song song = service.getSong();
			final String segment = new StringBuilder()
				.append("songs/")
				.append(song.getId())
				.append("/likings").toString();
			int method = 0;
			
			if (like) {
				song.decrementLikeNum();
				method = Method.DELETE;
			} else {
				song.incrementLikeNum();
				method = Method.POST;
			}
			
			setUserLikeSong(!like);
			displayLikeNum(song.getWorkedLikeNum());
			
			JustRequest request = new JustRequest(method, segment, null);
			((App) getContext().getApplicationContext()).addLongLivedRequest(request);
		}
	};
	
	private OnClickListener writeCommentClickListener = new ActivateOnlyClickListener() {
		
		@Override
		public void onActivated(View v, User user) {
			if (commentDialog != null) {
				final int inputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
				final Song song = service.getSong();
//				commentDialog.setSong(song);
//				commentDialog.show();
				commentDialog.getDialog().getWindow().setSoftInputMode(inputMode);
			}
		}
	};
	
	public void addComment(SongComment comment) {
		commentAdapter.addItem(comment);
		displayCommentNum(commentAdapter.getCount());
	}
	
	public void removeComment(SongComment comment) {
		commentAdapter.removeItem(comment);
		displayCommentNum(commentAdapter.getCount());
	}
	
	/*
	private void getBitlyShortUrl() {
		final String apiUrl = "https://api-ssl.bitly.com/v3/shorten?"; 
		final String token = "adaad7e775370d959bdb74ddeafed457a541710c";
		
		UrlBuilder urlBuilder = new UrlBuilder();
		String longUrl = urlBuilder.s("w").s("listen").s(thisSong.getId()).toString();
		String requestUrl = apiUrl;
		requestUrl += "access_token=" + token;
		requestUrl += "&longUrl=" + longUrl;
		
		try {
			JSONObject message = new JSONObject();
			message.put("access_token", token);
			message.put("longUrl", longUrl);
			
			JsonObjectRequest request = new JsonObjectRequest(
					Method.GET, requestUrl, null,
					new BitlyResponse(this), null
			);
			RequestQueue queue = ((App) getContext().getApplicationContext()).getQueueInstance();
			queue.add(request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private static class BitlyResponse extends OnVolleyWeakResponse<SlidingPlayerLayout, JSONObject> {

		public BitlyResponse(SlidingPlayerLayout reference) {
			super(reference);
		}

		@Override
		public void onFilteredResponse(SlidingPlayerLayout reference, JSONObject response) {
			try {
				JSONObject data = response.getJSONObject("data");
				String bitlyUrl = data.getString("url");
				reference.showKakaotalkDialog(bitlyUrl);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void showKakaotalkDialog(String url) {
		String message = thisSong.getCreator().getNickname();
		message += "님이 부른 ";
//		message += playerService.getSingerName() + "의 ";
//		message += playerService.getAlbumTitle() + " 듣기!";
		message += "\n\n";
		message += url;
		
		if (kakaotalkDialog != null && !kakaotalkDialog.isShowing()) {
			kakaotalkDialog.setBaseMessage(message);
			kakaotalkDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			kakaotalkDialog.show();
		}
	}
	 */

	public void hideActionBarWhenSliding(boolean enable) {
		if (enable) {
			actionBarHeight = getActionBarHeight();
		} else {
			actionBarHeight = 0;
		}
	}

    private int getActionBarHeight(){
    	return getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height);
    }

    private PanelSlideListener panelSlideListener = new PanelSlideListener() {
    	
    	private ActionBar actionBar;
    	
		@Override
		public void onPanelSlide(View panel, float slideOffset) {
			if (actionBar == null) {
				actionBar = ((RootActivity) getContext()).getSupportActionBar();
			}
			
			if (slideOffset > 0.8) {
				if (actionBar.isShowing()) {
					actionBar.hide();
				}
				
				if (ivDragPlayControl.isShown()) {
					ivDragPlayControl.setVisibility(View.GONE);
					ivChildSong.setVisibility(View.VISIBLE);
				}
			} else {
				if (!actionBar.isShowing()) {
					actionBar.show();
				}
				
				if (ivChildSong.isShown()) {
					ivDragPlayControl.setVisibility(View.VISIBLE);
					ivChildSong.setVisibility(View.GONE);
				}
			}
		}
		
		@Override
		public void onPanelHidden(View panel) {}
		
		@Override
		public void onPanelExpanded(View panel) {
			if (getContext() instanceof RootActivity) {
				((RootActivity) getContext()).onContentInvisible();
			}
		}
		
		@Override
		public void onPanelCollapsed(View panel) {
			if (getContext() instanceof RootActivity) {
				((RootActivity) getContext()).onContentVisible();
			}
		}
		
		@Override
		public void onPanelAnchored(View panel) {}
	};

	@Override
	public int getCurrentParalaxOffset() {
		// Content will not move when sliding
		return 0;
	}

	public void setActionBarTranslation(float y) {
		final int actionBarContentId = android.R.id.content;
		
		View content = ((Activity) getContext()).findViewById(actionBarContentId);
		ViewGroup window = (ViewGroup) content.getParent();
        for (int i = 0, l = window.getChildCount(); i < l; i++) {
            View child = window.getChildAt(i);
            if (child.getId() != actionBarContentId) {
                if (y <= -actionBarHeight) {
                    child.setVisibility(View.GONE);
                } else {
                    child.setVisibility(View.VISIBLE);
                    setCompatTranslationY(child, y);
                }
            }
        }
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setCompatTranslationY(View view, float y) {
    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            view.setTranslationY(y);
        } else {
            AnimatorProxy.wrap(view).setTranslationY(y);
        }
    }
	
    public void onDestroy() {
    	if (commentDialog != null) {
    		commentDialog.dismiss();
    		commentDialog = null;
    	}
    	
    	if (handler != null) {
    		handler.removeCallbacksAndMessages(null);
    		handler = null;
    	}
    }
    
}
