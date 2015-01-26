package com.myandb.singsong.widget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.android.volley.Request.Method;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.adapter.CommentAdapter;
import com.myandb.singsong.adapter.LikingUserAdapter;
import com.myandb.singsong.audio.OnPlayEventListener;
import com.myandb.singsong.audio.PlayEvent;
import com.myandb.singsong.audio.StreamPlayer;
import com.myandb.singsong.dialog.ShareDialog;
import com.myandb.singsong.event.ActivateOnlyClickListener;
import com.myandb.singsong.event.MemberOnlyClickListener;
import com.myandb.singsong.event.WeakRunnable;
import com.myandb.singsong.image.BlurAsyncTask;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Comment;
import com.myandb.singsong.model.Model;
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
import com.myandb.singsong.util.PlayCounter;
import com.myandb.singsong.util.StringFormatter;
import com.myandb.singsong.util.Utility;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class SlidingPlayerLayout extends SlidingUpPanelLayout {
	
	private PlayerService service;
	private ViewGroup slidingContainer; 
	private Handler handler;
	private GradualLoader commentLoader;
	private GradualLoader likingLoader;
	private CommentAdapter commentAdapter;
	private LikingUserAdapter likingAdapter;
	private boolean like;
	private boolean initialized; 

	private TextView tvParentUserNickname;
	private TextView tvThisUserNickname;
	private TextView tvFloatingUserNickname;
	private TextView tvParentUserPart;
	private TextView tvThisUserPart;
	private TextView tvFloatingUserPart;
	private TextView tvParentSongMessage;
	private TextView tvThisSongMessage;
	private TextView tvPlayStartTime;
	private TextView tvPlayEndTime;
	private TextView tvMusicInfoOnCollapsed;
	private TextView tvUsersInfoOnCollapsed;
	private TextView tvNoSongOnCollapsed;
	private TextView tvMusicTitleOnExpanded;
	private TextView tvSingerNameOnExpanded;
	private TextView tvCommentNum;
	private TextView tvCommentNumOut;
	private TextView tvLikingNum;
	private TextView tvFloatingDescription;
	private TextView tvRefreshComment;
	private TextView tvRefreshLiking;
	private ImageView ivParentUserPhoto;
	private ImageView ivThisUserPhoto;
	private ImageView ivFloatingUserPhoto;
	private ImageView ivLikeSong;
	private ImageView ivShowComment;
	private ImageView ivCloseComment;
	private ImageView ivCloseLiking;
	private ImageView ivPlayControl;
	private ImageView ivDragPlayControl;
	private ImageView ivLoopControl;
	private ImageView ivAutoplayControl;
	private ImageView ivBackground;
	private ImageView ivBackgroundMask;
	private ImageView ivBackgroundGradient;
	private ImageView ivShare;
	private ImageView ivOtherCollabo;
	private Button btnLikingNumOut;
	private Button btnSubmitComment;
	private EditText etComment;
	private View vDragPanelOnExpanded;
	private View vDragPanelOnCollapsed;
	private View vDefaultWindow;
	private View vCommentWindow;
	private View vLikingWindow;
	private View vPartnerWrapper;
	private View vStartCollabo;
	private View vFloatingCollabo; 
	private View vFloatingWrapper;
	private View vFloatingUserWrapper;
	private View vFloatingCollaboWrapper;
	private ListView lvComments;
	private GridView gvLikings;
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
		if (this.service != null) {
			return;
		}
		
		this.service = service;
		
		if (!initialized) {
			setSlidingContainer(R.id.fl_sliding_container);
			
			setContentView(R.layout.player);
			
			setDragView(R.id.layout_drag_panel);
			
			setPanelSlideListener(panelSlideListener);
			
			initialize();
			
			registerSharedPreferenceChangeListener();
			
			setBackgroundGradientRadius();
			
			ivLoopControl.setOnClickListener(loopControlClickListener); 
			ivAutoplayControl.setOnClickListener(autoplayControlClickListener);
			ivPlayControl.setOnClickListener(playControlClickListener);
			ivDragPlayControl.setOnClickListener(playControlClickListener);
			sbPlay.setOnSeekBarChangeListener(seekBarChangeListener);
			ivShowComment.setOnClickListener(showCommentClickListener);
			ivCloseComment.setOnClickListener(closeCommentClickListener);
			ivCloseLiking.setOnClickListener(closeLikingClickListener);
			btnLikingNumOut.setOnClickListener(showLikingClickListener);
			
			tvParentSongMessage.setMovementMethod(new ScrollingMovementMethod());
			tvThisSongMessage.setMovementMethod(new ScrollingMovementMethod());
			
			service.getPlayer().setOnPlayEventListener(onPlayEventListener);
			
			initialized = true;
		}
		
		setupViews();
		
		if (service.getPlayer().isPlaying()) {
			catchPlayStatusChange(PlayEvent.PLAY);
		}
	}
	
	int k;
	
	private int getRelativeLeft(View view) {
	    if (view.getParent() == view.getRootView()) {
	    	return view.getLeft();
	    } else {
	    	return view.getLeft() + getRelativeLeft((View) view.getParent());
	    }
	}

	private int getRelativeTop(View view) {
	    if (view.getParent() == view.getRootView()) {
	    	return view.getTop();
	    } else {
	    	return view.getTop() + getRelativeTop((View) view.getParent());
	    }
	}
	
	private void initialize() {
		commentAdapter = new CommentAdapter(this);
		commentLoader = new GradualLoader(getContext());
		lvComments.setAdapter(commentAdapter);
		lvComments.setOnScrollListener(commentLoader);
		
		likingAdapter = new LikingUserAdapter();
		likingLoader = new GradualLoader(getContext());
		gvLikings.setAdapter(likingAdapter);
		gvLikings.setOnScrollListener(likingLoader);
		
		handler = new Handler();
	}
	
	public void setSlidingContainer(int containerId) {
		if (slidingContainer == null) {
			slidingContainer = (ViewGroup) findViewById(containerId);
		}
	}
	
	public void setContentView(int layoutId) {
		View.inflate(getContext(), layoutId, slidingContainer);
		onViewInflated();
	}
	
	private void onViewInflated() {
		vDragPanelOnExpanded = findViewById(R.id.layout_player_drag_panel_on_expanded);
		vDragPanelOnCollapsed = findViewById(R.id.layout_player_drag_panel_on_collapsed);
		vDefaultWindow = findViewById(R.id.layout_player_default);
		vCommentWindow = findViewById(R.id.layout_player_comment);
		vLikingWindow = findViewById(R.id.layout_player_liking);
		vPartnerWrapper = findViewById(R.id.layout_partner_wrapper);
		vStartCollabo = findViewById(R.id.layout_collabo);
		vFloatingCollabo = findViewById(R.id.layout_floating_collabo);
		vFloatingCollaboWrapper = findViewById(R.id.ll_floating_collabo_wrapper);
		vFloatingUserWrapper = findViewById(R.id.ll_floating_user_wrapper);
		vFloatingWrapper = findViewById(R.id.fl_floating_wrapper);
		
		tvParentUserNickname = (TextView) findViewById(R.id.tv_parent_user_nickname);
		tvThisUserNickname = (TextView) findViewById(R.id.tv_this_user_nickname);
		tvParentUserPart = (TextView) findViewById(R.id.tv_parent_user_part);
		tvThisUserPart = (TextView) findViewById(R.id.tv_this_user_part);
		tvParentSongMessage = (TextView) findViewById(R.id.tv_parent_song_message);
		tvThisSongMessage = (TextView) findViewById(R.id.tv_this_song_message);
		tvPlayStartTime = (TextView) findViewById(R.id.tv_play_start_time);
		tvPlayEndTime = (TextView) findViewById(R.id.tv_play_end_time);
		tvMusicTitleOnExpanded = (TextView) findViewById(R.id.tv_music_title_on_expanded);
		tvSingerNameOnExpanded = (TextView) findViewById(R.id.tv_singer_name_on_expanded);
		tvMusicInfoOnCollapsed = (TextView) findViewById(R.id.tv_music_info_on_collapsed);
		tvUsersInfoOnCollapsed = (TextView) findViewById(R.id.tv_users_info_on_collapsed);
		tvNoSongOnCollapsed = (TextView) findViewById(R.id.tv_no_song_on_collapsed);
		tvCommentNum = (TextView) findViewById(R.id.tv_comment_num);
		tvCommentNumOut = (TextView) findViewById(R.id.tv_comment_num_out);
		tvLikingNum = (TextView) findViewById(R.id.tv_liking_num);
		tvFloatingUserNickname = (TextView) findViewById(R.id.tv_floating_user_nickname);
		tvFloatingUserPart = (TextView) findViewById(R.id.tv_floating_user_part);
		tvFloatingDescription = (TextView) findViewById(R.id.tv_floating_description);
		tvRefreshComment = (TextView) findViewById(R.id.tv_refresh_comment);
		tvRefreshLiking = (TextView) findViewById(R.id.tv_refresh_liking);
		
		ivParentUserPhoto = (ImageView) findViewById(R.id.iv_parent_user_photo);
		ivThisUserPhoto = (ImageView) findViewById(R.id.iv_this_user_photo);
		ivLikeSong = (ImageView) findViewById(R.id.iv_like_song);
		ivPlayControl = (ImageView) findViewById(R.id.iv_play_control);
		ivDragPlayControl = (ImageView) findViewById(R.id.iv_drag_play_control);
		ivLoopControl = (ImageView) findViewById(R.id.iv_loop_control);
		ivAutoplayControl = (ImageView) findViewById(R.id.iv_autoplay_control);
		ivShowComment = (ImageView) findViewById(R.id.iv_show_comment);
		ivCloseComment = (ImageView) findViewById(R.id.iv_close_comment);
		ivCloseLiking = (ImageView) findViewById(R.id.iv_close_liking);
		ivBackground = (ImageView) findViewById(R.id.iv_background);
		ivBackgroundMask = (ImageView) findViewById(R.id.iv_background_mask);
		ivBackgroundGradient = (ImageView) findViewById(R.id.iv_background_gradient);
		ivShare = (ImageView) findViewById(R.id.iv_share);
		ivFloatingUserPhoto = (ImageView) findViewById(R.id.iv_floating_user_photo);
		ivOtherCollabo = (ImageView) findViewById(R.id.iv_other_collabo);
		
		lvComments = (ListView) findViewById(R.id.lv_comment);
		gvLikings = (GridView) findViewById(R.id.gv_liking);
		btnSubmitComment = (Button) findViewById(R.id.btn_submit_comment);
		btnLikingNumOut = (Button) findViewById(R.id.btn_liking_num_out);
		etComment = (EditText) findViewById(R.id.et_comment);
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
			makeToast(R.string.t_notify_enabled_replay);
		} else {
			makeToast(R.string.t_notify_disabled_replay);
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
			makeToast(R.string.t_notify_enabled_autoplay);
		} else {
			makeToast(R.string.t_notify_disabled_autoplay);
		}
	}
	
	private void makeToast(int stringRes) {
		Toast.makeText(getContext().getApplicationContext(), getContext().getString(stringRes), Toast.LENGTH_SHORT).show();
	}
	
	private void setBackgroundGradientRadius() {
		GradientDrawable gradient = (GradientDrawable) ivBackgroundGradient.getBackground();
		int radius = getResources().getDimensionPixelSize(R.dimen.gradient_radius);
		gradient.mutate();
		gradient.setGradientRadius(radius);
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

	private OnClickListener playControlClickListener = new MemberOnlyClickListener() {
		
		@Override
		public void onLoggedIn(View v, User user) {
			StreamPlayer player = service.getPlayer();
			StreamPlayer samplePlayer = service.getSamplePlayer();
			
			if (samplePlayer.isPlaying()) {
				samplePlayer.pause();
			}
			
			if (player.isPlaying()) {
				player.pause();
			} else {
				player.startIfPrepared();
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
					setupViews();
					showDefaultWindow();
					expandPanel();
					ivPlayControl.setEnabled(false);
					ivDragPlayControl.setEnabled(false);
					break;
					
				case PREPARED:
					ivDragPlayControl.setEnabled(true);
					ivPlayControl.setEnabled(true);
					break;
					
				case PLAY:
					if (service != null && service.getSong() != null) {
						PlayCounter.countAsync(getContext(), "songs", service.getSong().getId());
					}
					onProgressUpdate();
					ivPlayControl.setImageResource(R.drawable.ic_pause);
					ivDragPlayControl.setImageResource(R.drawable.ic_pause);
					break;
					
				case RESUME:
					showDefaultWindow();
					expandPanel();
					break;
					
				case PAUSE:
				case COMPLETED:
					ivPlayControl.setImageResource(R.drawable.ic_play);
					ivDragPlayControl.setImageResource(R.drawable.ic_play);
					break;
					
				case ERROR:
					break;
					
				default:
					break;
				}
			}
		});
	}
	
	public void showDefaultWindow() {
		ViewHelper.setAlpha(ivBackgroundMask, 0.15f);
		vDefaultWindow.setVisibility(View.VISIBLE);
		vCommentWindow.setVisibility(View.GONE);
		vLikingWindow.setVisibility(View.GONE);
		vFloatingWrapper.setVisibility(View.GONE);
		InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
		imm.hideSoftInputFromWindow(etComment.getWindowToken(), 0);
	}
	
	public void setCommentWindowShown(boolean shown) {
		if (vCommentWindow.isShown() == shown) {
			return;
		}
		
		if (shown) {
			ViewHelper.setAlpha(ivBackgroundMask, 0.6f);
			vDefaultWindow.setVisibility(View.GONE);
			vCommentWindow.setVisibility(View.VISIBLE);
			vLikingWindow.setVisibility(View.GONE);
		} else {
			showDefaultWindow();
		}
	}
	
	public void setLikingWindowShown(boolean shown) {
		if (vLikingWindow.isShown() == shown) {
			return;
		}
		
		if (shown) {
			ViewHelper.setAlpha(ivBackgroundMask, 0.6f);
			vDefaultWindow.setVisibility(View.GONE);
			vCommentWindow.setVisibility(View.GONE);
			vLikingWindow.setVisibility(View.VISIBLE);
		} else {
			showDefaultWindow();
		}
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
	
	private OnClickListener showCommentClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			setCommentWindowShown(true);
		}
	};
	
	private OnClickListener closeCommentClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			setCommentWindowShown(false);
		}
	};
	
	private OnClickListener showLikingClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			setLikingWindowShown(true);
		}
	};
	
	private OnClickListener closeLikingClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			setLikingWindowShown(false);
		}
	};
	
	public void onProgressUpdate() {
		StreamPlayer player = service.getPlayer();
		if (player != null && player.isPlaying()) {
			tvPlayStartTime.setText(StringFormatter.getDuration(player.getCurrentPosition()));
			sbPlay.setProgress(player.getCurrentPosition());
			
			if (handler != null) {
				Runnable r = new WeakRunnable<SlidingPlayerLayout>(this, "onProgressUpdate");
				handler.postDelayed(r, 1000);
			}
		}
	}
	
	private void setupViews() {
		if (service.getSong() == null) {
			tvNoSongOnCollapsed.setVisibility(View.VISIBLE);
			collapsePanel();
			setSlidingEnabled(false);
			return;
		}
		
		tvNoSongOnCollapsed.setVisibility(View.GONE);
		setSlidingEnabled(true);
		
		final User currentUser = Authenticator.getUser();
		final Song song = service.getSong();
		final User parentUser = song.getParentUser();
		final User thisUser = song.isRoot() ? currentUser : song.getCreator();
		final Music music = song.getMusic();
		
		displayMusicInfo(music);
		
		displayUsersInfo(song);
		
		setSelectedMarqueeViews();
		
		displayBackgroundImage(song);
		
		displaySongMessage(song);
		
		displayProfile(parentUser, ivParentUserPhoto, tvParentUserNickname);
		
		displayProfile(thisUser, ivThisUserPhoto, tvThisUserNickname);
		
		displayProfile(currentUser, ivFloatingUserPhoto, tvFloatingUserNickname);
		
		displaySongPart(song);
		
		displayTargetContent(parentUser.getNickname(), song);
		
		displayCommentNum(song.getCommentNum());
		
		displayLikeNum(song.getLikeNum());
		
		requestNewComments(song);
		
		requestNewLikings(song);
		
		checkUserLikeSong(currentUser, song);
		
		initializeSeekBar(song);
		
		ivParentUserPhoto.setOnClickListener(parentUser.getProfileClickListener());
		if (thisUser != null) {
			ivThisUserPhoto.setOnClickListener(thisUser.getProfileClickListener());
		}
		ivLikeSong.setOnClickListener(likeClickListener);
		btnSubmitComment.setOnClickListener(submitCommentClickListner);
		ivShare.setOnClickListener(shareClickListener);
		ivOtherCollabo.setOnClickListener(song.getChildrenClickListener());
		tvRefreshComment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				requestNewComments(song);
			}
		});
		tvRefreshLiking.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				requestNewLikings(song);
			}
		});
		
		if (song.isRoot()) {
			vPartnerWrapper.setVisibility(View.GONE);
			setCollaboButtonReal(vStartCollabo, song);
		} else {
			vPartnerWrapper.setVisibility(View.VISIBLE); 
			setCollaboButtonFake(vStartCollabo);
			vFloatingCollabo.setOnClickListener(song.getCollaboClickListner());
		}
		
		if (isPanelExpanded()) {
			vDragPanelOnExpanded.setVisibility(View.VISIBLE);
			vDragPanelOnCollapsed.setVisibility(View.GONE);
		} else {
			vDragPanelOnExpanded.setVisibility(View.GONE);
			vDragPanelOnCollapsed.setVisibility(View.VISIBLE);
		}
		
		vFloatingWrapper.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				v.setVisibility(View.GONE);
				vPartnerWrapper.setVisibility(View.VISIBLE);
			}
		});
		
		slidingContainer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {}
		});
		
		showDefaultWindow();
	}
	
	private void setCollaboButtonFake(View view) {
		view.setBackgroundResource(R.drawable.button_collabo_transparent);
		view.setOnClickListener(new ActivateOnlyClickListener() {
			
			@Override
			public void onActivated(View v, User user) {
				final Animation fadeIn = AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in);
				fadeIn.setDuration(200);
				vPartnerWrapper.setVisibility(View.INVISIBLE);
				vFloatingWrapper.setVisibility(View.VISIBLE);
				vFloatingWrapper.startAnimation(fadeIn);
				positioningFloatingWrapper(vPartnerWrapper, vFloatingUserWrapper, false);
				positioningFloatingWrapper(vStartCollabo, vFloatingCollaboWrapper, true);
			}
		});
		TextView text = (TextView) view.findViewWithTag(getResources().getString(R.string.tag_button_collabo_text));
		text.setText("콜라보 하기");
	}
	
	private void setCollaboButtonReal(View view, Song song) {
		view.setBackgroundResource(R.drawable.button_collabo_primary);
		view.setOnClickListener(song.getCollaboClickListner());
		TextView text = (TextView) view.findViewWithTag(getResources().getString(R.string.tag_button_collabo_text));
		text.setText("콜라보 시작");
	}
	
	private void positioningFloatingWrapper(View original, View floating, boolean matchParent) {
		int left = getRelativeLeft(original);
		int top = getRelativeTop(original);
		int width = original.getWidth();
		FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) floating.getLayoutParams();
		lp.topMargin = top - getResources().getDimensionPixelSize(R.dimen.player_floating_offset);
		if (!matchParent) {
			lp.leftMargin = left;
			lp.width = width;
		}
		floating.requestLayout();
	}
	
	private void displayMusicInfo(Music music) {
		tvMusicInfoOnCollapsed.setText(music.getTitle() + " - " + music.getSingerName());
		tvMusicTitleOnExpanded.setText(music.getTitle());
		tvSingerNameOnExpanded.setText(music.getSingerName());
	}
	
	private void displayUsersInfo(Song song) {
		String info = song.getParentUser().getNickname();
		if (!song.isRoot()) {
			info += " X ";
			info += song.getCreator().getNickname();
		}
		tvUsersInfoOnCollapsed.setText(info);
	}
	
	private void setSelectedMarqueeViews() {
		tvMusicInfoOnCollapsed.setSelected(true);
		tvMusicTitleOnExpanded.setSelected(true);
		tvSingerNameOnExpanded.setSelected(true);
		tvUsersInfoOnCollapsed.setSelected(true);
	}
	
	private void displayBackgroundImage(Song song) {
		String url = song.getMusic().getAlbumPhotoUrl();
		ImageLoader.getInstance().displayImage(url, ivBackground, imageLoadingListener);
	}
	
	private ImageLoadingListener imageLoadingListener = new ImageLoadingListener() {

		@Override
		public void onLoadingCancelled(String arg0, View arg1) {}

		@Override
		public void onLoadingComplete(String url, View imageView, Bitmap bitmap) {
			setBackgroundBlurImage(bitmap, (ImageView) imageView);
		}

		@Override
		public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {}

		@Override
		public void onLoadingStarted(String arg0, View imageView) {}
		
	};
	
	private void setBackgroundBlurImage(Bitmap bitmap, ImageView imageView) {
		BlurAsyncTask blurTask = new BlurAsyncTask();
		blurTask.setImageView(imageView);
		blurTask.execute(bitmap);
	}
	
	private void displaySongMessage(Song song) {
		if (song.isRoot()) {
			tvParentSongMessage.setText(song.getMessage());
		} else {
			Song parentSong = song.getParentSong();
			tvParentSongMessage.setText(parentSong.getMessage());
			tvThisSongMessage.setText(song.getMessage());
		}
	}
	
	private void displayProfile(User user, ImageView ivPhoto, TextView tvNickname) {
		if (user != null) {
			tvNickname.setText(user.getNickname());
			ImageHelper.displayPhoto(user, ivPhoto);
		}
	}
	
	private void displaySongPart(Song song) {
		tvParentUserPart.setText(song.getParentPartName());
		tvThisUserPart.setText(song.getPartName());
		tvFloatingUserPart.setText(song.getPartName());
		
		int thisSongLyricPart = song.getLyricPart();
		int rootSongLyricPart = song.getPartnerLyricPart();
		if (song.isRoot()) {
			paintSongPart(thisSongLyricPart, tvParentUserPart);
			paintSongPart(thisSongLyricPart, ivParentUserPhoto);
		} else {
			paintSongPart(thisSongLyricPart, tvThisUserPart);
			paintSongPart(thisSongLyricPart, ivThisUserPhoto);
			paintSongPart(thisSongLyricPart, tvFloatingUserPart);
			paintSongPart(thisSongLyricPart, ivFloatingUserPhoto);
			paintSongPart(thisSongLyricPart, ivOtherCollabo);
			paintSongPart(rootSongLyricPart, tvParentUserPart);
			paintSongPart(rootSongLyricPart, ivParentUserPhoto);
		}
	}
	
	private void paintSongPart(int part, TextView textView) {
		if (part == Music.PART_MALE) {
			textView.setTextColor(getResources().getColor(R.color.primary));
		} else if (part == Music.PART_FEMALE) {
			textView.setTextColor(getResources().getColor(R.color.sub));
		}
	}
	
	private void paintSongPart(int part, ImageView imageView) {
		if (part == Music.PART_MALE) {
			imageView.setBackgroundResource(R.drawable.circle_primary);
		} else if (part == Music.PART_FEMALE) {
			imageView.setBackgroundResource(R.drawable.circle_sub);
		}
	}
	
	private void displayTargetContent(String nickname, Song song) {
		String croppedNickname = "";
		final int maxDisplayNicknameLength = 8;
		if (nickname.length() > maxDisplayNicknameLength) {
			croppedNickname += nickname.substring(0, maxDisplayNicknameLength);
			croppedNickname += "..";
		} else {
			croppedNickname += nickname;
		}
		Spannable spannableNickname = Utility.getBoldSpan(croppedNickname);
		Utility.getColorSpan(spannableNickname, getResources().getColor(R.color.white));
		
		tvFloatingDescription.setText(spannableNickname);
		tvFloatingDescription.append("님이 부른\n");
		
		String partName = song.getParentPartName();
		Spannable spannablePartName = new SpannableString(partName);
		if (song.getPartnerLyricPart() == Music.PART_MALE) {
			Utility.getColorSpan(spannablePartName, getResources().getColor(R.color.primary));
		} else {
			Utility.getColorSpan(spannablePartName, getResources().getColor(R.color.sub));
		}
		
		tvFloatingDescription.append(spannablePartName);
		tvFloatingDescription.append(" 파트에 콜라보를 해보세요!");
	}
	
	private void displayCommentNum(int num) {
		tvCommentNum.setText("댓글 (");
		tvCommentNum.append(String.valueOf(num));
		tvCommentNum.append(")");
		tvCommentNumOut.setText(Model.safeString(num));
	}
	
	private void displayLikeNum(int num) {
		tvLikingNum.setText("좋아요 (");
		tvLikingNum.append(String.valueOf(num));
		tvLikingNum.append(")");
		btnLikingNumOut.setText(Model.safeString(num));
	}
	
	private void checkUserLikeSong(User user, Song song) {
		if (user != null && song != null) {
			String segment = new StringBuilder()
				.append("songs/")
				.append(song.getId())
				.append("/likings")
				.toString();
			Bundle params = new Bundle();
			params.putString("user_id", String.valueOf(user.getId()));
			
			JSONObjectRequest request = new JSONObjectRequest(
					segment, params, null,
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
			ivLikeSong.setImageResource(R.drawable.ic_like_activated);
		} else {
			ivLikeSong.setImageResource(R.drawable.ic_like);
		}
	}
	
	private void initializeSeekBar(Song song) {
		int duration = song.getDuration();
		
		tvPlayStartTime.setText(StringFormatter.getDuration(0));
		tvPlayEndTime.setText(StringFormatter.getDuration(duration));
		sbPlay.setMax(duration);
		sbPlay.setProgress(0);
	}
	
	private void requestNewComments(Song song) {
		if (commentAdapter != null) {
			commentAdapter.clear();
			
			UrlBuilder urlBuilder = new UrlBuilder();
			urlBuilder.s("songs").s(song.getId()).s("comments");
			commentLoader.setUrlBuilder(urlBuilder);
			commentLoader.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				
				@Override
				public void onComplete(JSONArray response) {
					commentAdapter.addAll(response);
				}
			});
		}
	}
	
	private void requestNewLikings(Song song) {
		if (likingAdapter != null) {
			likingAdapter.clear();
			
			UrlBuilder urlBuilder = new UrlBuilder();
			urlBuilder.s("songs").s(song.getId()).s("likings");
			likingLoader.setUrlBuilder(urlBuilder);
			likingLoader.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				
				@Override
				public void onComplete(JSONArray response) {
					likingAdapter.addAll(response);
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
			displayLikeNum(song.getLikeNum());
			
			JustRequest request = new JustRequest(method, segment, null, null);
			((App) getContext().getApplicationContext()).addLongLivedRequest(request);
		}
	};
	
	private OnClickListener submitCommentClickListner = new ActivateOnlyClickListener() {
		
		@Override
		public void onActivated(View v, User user) {
			String comment = etComment.getText().toString();
			if (comment.trim().length() > 0) {
				JSONObject message = new JSONObject();
				try {
					message.put("user_id", user.getId());
					message.put("content", comment);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				Song song = service.getSong();
				JSONObjectRequest request = new JSONObjectRequest(
						"songs/" + song.getId() + "/comments", null, message,
						new JSONObjectSuccessListener(SlidingPlayerLayout.this, "onSubmitSuccess", SongComment.class),
						new JSONErrorListener(SlidingPlayerLayout.this, "onSubmitError")
				);
				((App) getContext().getApplicationContext()).addShortLivedRequest(getContext(), request);
				
				etComment.setText("");
			} else {
				makeToast(R.string.t_alert_comment_validation_failed);
			}
		}
	};
	
	private OnClickListener shareClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			BaseActivity activity = ((BaseActivity) getContext());
			Song song = service.getSong();
			Bundle bundle = new Bundle();
			bundle.putString(ShareDialog.EXTRA_SONG, song.toString());
			ShareDialog dialog = new ShareDialog();
			dialog.setArguments(bundle);
			dialog.show(activity.getSupportFragmentManager(), "");
		}
	};
	
	public void onSubmitSuccess(SongComment response) {
		addComment(response);
	}
	
	public void onSubmitError() {
		
	}
	
	public void addComment(Comment<?> comment) {
		commentAdapter.addItemToHead(comment);
		service.getSong().incrementCommentNum();
		displayCommentNum(service.getSong().getCommentNum());
	}
	
	public void deleteComment(Comment<?> comment) {
		JustRequest request = new JustRequest(Method.DELETE, "comments/" + comment.getId(), null, null);
		((App) getContext().getApplicationContext()).addShortLivedRequest(getContext(), request);
		commentAdapter.removeItem(comment);
		service.getSong().decrementCommentNum();
		displayCommentNum(service.getSong().getCommentNum());
	}

    private PanelSlideListener panelSlideListener = new PanelSlideListener() {
    	
    	private static final float OFFSET_MULTIPLIER = 1.5f;
    	private ActionBar actionBar;
    	private View actionBarContainer; 
    	
		@Override
		public void onPanelSlide(View panel, float slideOffset) {
			initializeActionBar();
			showOrHideActionBar(slideOffset);
			showDragPanelIfNotVisible();
			setAlphaOnDragPanel(slideOffset);
			setAlphaOnMask(slideOffset);
		}
		
		private void initializeActionBar() {
			if (actionBar == null) {
				actionBar = ((BaseActivity) getContext()).getSupportActionBar();
				if (actionBarContainer == null) {
					actionBarContainer = ((BaseActivity) getContext()).getActionBarView();
				}
			}
		}
		
		private void showOrHideActionBar(float slideOffset) {
			if (actionBarContainer != null) {
				ViewHelper.setAlpha(actionBarContainer, 1 - slideOffset);
				if (!actionBarContainer.isShown()) {
					actionBarContainer.setVisibility(View.VISIBLE);
				}
			} else {
				showOrHideActionBarCompat(slideOffset);
			}
		}
		
		private void showOrHideActionBarCompat(float slideOffset) {
			if (actionBar != null) {
				if (slideOffset > 0.8) {
					if (actionBar.isShowing()) {
						actionBar.hide();
					}
				} else if (slideOffset < 0.2) {
					if (!actionBar.isShowing()) {
						actionBar.show();
					}
				}
			}
		}
		
		private void showDragPanelIfNotVisible() {
			if (!vDragPanelOnCollapsed.isShown()) {
				vDragPanelOnCollapsed.setVisibility(View.VISIBLE);
			}
			
			if (!vDragPanelOnExpanded.isShown()) {
				vDragPanelOnExpanded.setVisibility(View.VISIBLE);
			}
		}
		
		private void setAlphaOnDragPanel(float slideOffset) {
			ViewHelper.setAlpha(vDragPanelOnCollapsed, getCollapsedAlpha(slideOffset));
			ViewHelper.setAlpha(vDragPanelOnExpanded, getExpandedAlpha(slideOffset));
		}
		
		private float getCollapsedAlpha(float slideOffset) {
			return Math.max((float) (1 - slideOffset * OFFSET_MULTIPLIER), 0f);
		}
		
		private float getExpandedAlpha(float slideOffset) {
			return Math.min((float) (slideOffset * OFFSET_MULTIPLIER), 1f);
		}
		
		private void setAlphaOnMask(float slideOffset) {
			if (vDefaultWindow.isShown()) {
				ViewHelper.setAlpha(ivBackgroundMask, Math.min(Math.max((float) (1 - slideOffset), 0.15f), 0.3f));
			}
		}
		
		@Override
		public void onPanelHidden(View panel) {}
		
		@Override
		public void onPanelExpanded(View panel) {
			if (getContext() instanceof RootActivity) {
				((RootActivity) getContext()).onContentInvisible();
			}
			
			if (actionBarContainer != null) {
				actionBarContainer.setVisibility(View.GONE);
			}
			
			vDragPanelOnExpanded.setVisibility(View.VISIBLE);
			vDragPanelOnCollapsed.setVisibility(View.GONE);
		}
		
		@Override
		public void onPanelCollapsed(View panel) {
			if (getContext() instanceof RootActivity) {
				((RootActivity) getContext()).onContentVisible();
			}
			
			if (actionBarContainer != null) {
				actionBarContainer.setVisibility(View.VISIBLE);
			}
			
			vDragPanelOnCollapsed.setVisibility(View.VISIBLE);
			vDragPanelOnExpanded.setVisibility(View.GONE);
			InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE); 
			imm.hideSoftInputFromWindow(etComment.getWindowToken(), 0);
		}
		
		@Override
		public void onPanelAnchored(View panel) {}
	};

	@Override
	public int getCurrentParalaxOffset() {
		// Content will not move when sliding
		return 0;
	}
	
    public void onDestroy() {
    	if (handler != null) {
    		handler.removeCallbacksAndMessages(null);
    		handler = null;
    	}
    }
    
}
