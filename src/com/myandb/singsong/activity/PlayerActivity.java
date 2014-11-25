package com.myandb.singsong.activity;

import java.lang.ref.WeakReference;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.adapter.CommentAdapter;
import com.myandb.singsong.dialog.KakaotalkDialog;
import com.myandb.singsong.dialog.WriteCommentDialog;
import com.myandb.singsong.event.Listeners;
import com.myandb.singsong.event.MemberOnlyClickListener;
import com.myandb.singsong.event.OnVolleyWeakResponse;
import com.myandb.singsong.event.WeakRunnable;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Comment;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.OAuthJsonObjectRequest;
import com.myandb.singsong.net.OAuthJustRequest;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.service.PlayerService;
import com.myandb.singsong.util.StringFormatter;
import com.myandb.singsong.util.Utility;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class PlayerActivity extends Activity {
	
	public String bitlyUrl;
	
	private View vStartCollabo;
	private View vLikeSong;
	private View vWriteComment;
	private View vPartnerMask;
	private View vPartnerWrapper;
	private TextView tvParentUserNickname,
					 tvThisUserNickname,
					 tvParentUserPart,
					 tvThisUserPart,
					 tvParentSongMessage,
					 tvThisSongMessage,
					 tvLikeNum,
					 tvCommentNum,
					 tvStartTime,
					 tvEndTime,
					 tvTargetContent,
					 tvSingerName,
					 tvMusicTitle;
	private ImageView ivParentUserPhoto,
					  ivThisUserPhoto,
					  ivParentSongImage,
					  ivThisSongImage,
					  ivLikeIcon,
					  ivPlayControl,
					  ivLoopControl,
					  ivAutoplayControl,
					  ivNeon,
					  ivChildSong;
	private ListView lvComments;
	private SeekBar sbPlay;
	private Song thisSong;
	private User currentUser;
	private CommentAdapter commentAdapter;
	private final Handler handler = new Handler();
	private PlayerService playerService;
	private boolean isLike;
	private Animation blink;
	private WriteCommentDialog commentDialog;
	private KakaotalkDialog kakaotalkDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		currentUser = Authenticator.getUser();
		
		initializeView();
	}
	
	private void initializeView() {
		View header = View.inflate(getApplicationContext(), R.layout.activity_play_song_header, null);
		
		lvComments = (ListView) findViewById(R.id.lv_full_width);
		lvComments.addHeaderView(header);
		
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
		tvSingerName = (TextView) findViewById(R.id.tv_singer_name);
		tvMusicTitle = (TextView) findViewById(R.id.tv_music_title);
		
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
		
		sbPlay = (SeekBar) findViewById(R.id.sb_play);
	}

	/*
	@Override
	public void onPlayerConnected(PlayerService service) {
		playerService = service;
		
		if (playerService != null) {
			thisSong = playerService.getSong();
			
			if (thisSong != null) {
				initializeComponent();
				
				setupView();
				
				checkUserLikeSong();
				
				requestCommments();
				
				runService();
				
				setKakaotalkClickListener(new MemberOnlyClickListener() {
					
					@Override
					public void onActivated(View v) {
						if (bitlyUrl != null) {
							showKakaotalkDialog(bitlyUrl);
						} else {
							getBitlyShortUrl();
						}
						
					}
				});
			} else {
				finish();
			}
		} else {
			finish();
		}
	}
	*/
	
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
			RequestQueue queue = ((App) getApplicationContext()).getQueueInstance();
			queue.add(request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private static class BitlyResponse extends OnVolleyWeakResponse<PlayerActivity, JSONObject> {

		public BitlyResponse(PlayerActivity reference) {
			super(reference);
		}

		@Override
		public void onFilteredResponse(PlayerActivity reference, JSONObject response) {
			try {
				JSONObject data = response.getJSONObject("data");
				reference.bitlyUrl = data.getString("url");
				reference.showKakaotalkDialog(reference.bitlyUrl);
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
		
		if (kakaotalkDialog != null && !isFinishing() && !kakaotalkDialog.isShowing()) {
			kakaotalkDialog.setBaseMessage(message);
			kakaotalkDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			kakaotalkDialog.show();
		}
	}
	
	private void initializeComponent() {
		commentDialog = new WriteCommentDialog(this, currentUser, thisSong);
		kakaotalkDialog = new KakaotalkDialog(this, currentUser);
		
		commentAdapter = new CommentAdapter();
		lvComments.setAdapter(commentAdapter);
		
		blink = new AlphaAnimation(0.0f, 0.5f);
		blink.setDuration(500);
		blink.setRepeatMode(Animation.REVERSE);
		blink.setRepeatCount(Animation.INFINITE);
	}
	
	private void checkUserLikeSong() {
		if (currentUser != null && thisSong != null) {
			UrlBuilder urlBuilder = new UrlBuilder();
			String url = urlBuilder.s("songs").s(thisSong.getId()).s("likings").p("user_id", currentUser.getId()).toString();
			
			OAuthJsonObjectRequest request = new OAuthJsonObjectRequest(
					Method.GET, url, null,
					new OnVolleyWeakResponse<PlayerActivity, JSONObject>(this, "onGetUserLikeResponse"), 
					null
			);
			
			RequestQueue queue = ((App) getApplicationContext()).getQueueInstance();
			queue.add(request);
		}
	}
	
	public void onGetUserLikeResponse(JSONObject response) {
		setUserLikeSong(true);
	}
	
	private void requestCommments() {
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.s("songs").s(thisSong.getId()).s("comments");
		
		if (commentAdapter != null) {
//			commentAdapter.resetRequest(urlBuilder);
		}
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
	
	private void setupView() {
		final User parentUser = thisSong.getParentUser();
		final User thisUser = thisSong.isRoot() ? currentUser : thisSong.getCreator();
		final Music music = thisSong.getMusic();
		
		displayMusic(music, tvSingerName, tvMusicTitle);
		
		displaySongImage(thisSong, ivParentSongImage, ivThisSongImage);
		
		displaySongMessage(thisSong, tvParentSongMessage, tvThisSongMessage);
		
		displayProfile(parentUser, ivParentUserPhoto, tvParentUserNickname);
		
		displayProfile(thisUser, ivThisUserPhoto, tvThisUserNickname);
		
		displaySongPart(thisSong, tvParentUserPart, tvThisUserPart);
		
		ivParentUserPhoto.setOnClickListener(Listeners.getProfileClickListener(this, parentUser));
		
		ivThisUserPhoto.setOnClickListener(Listeners.getProfileClickListener(this, thisUser));
		
		if (thisSong.isRoot()) {
			vPartnerMask.setVisibility(View.VISIBLE);
			vPartnerWrapper.setVisibility(View.GONE);
			ivChildSong.setImageResource(R.drawable.img_collabo);
		} else {
			vPartnerMask.setVisibility(View.GONE);
			vPartnerWrapper.setVisibility(View.VISIBLE);
			ivChildSong.setImageResource(R.drawable.img_collabo_other);
		}
		
		final Spannable nicknameSpan = new SpannableString(parentUser.getNickname());
		Utility.getStyleSpan(nicknameSpan, Typeface.BOLD);
		
		final Spannable partSpan = new SpannableString(thisSong.getParentPartName());
		Utility.getColorSpan(partSpan, "#6ab8d3");
		
		final Spannable collaboSpan = new SpannableString("콜라보하기!");
		Utility.getStyleSpan(collaboSpan, Typeface.BOLD);
		
		tvTargetContent.setText(nicknameSpan);
		tvTargetContent.append("님이 부른 ");
		tvTargetContent.append(partSpan);
		tvTargetContent.append("와 ");
		tvTargetContent.append(collaboSpan);
		
		displayCommentNum(thisSong.getCommentNum());
		
		displayLikeNum(thisSong.getWorkedLikeNum());
		
//		setPlayerLooping(preferences.isPlayerLooping());
		
//		setPlayerAutoplay(preferences.isPlayerAutoplay());
		
		ivChildSong.setOnClickListener(Listeners.getChildrenClickListener(this, thisSong));
		ivLoopControl.setOnClickListener(loopControlClickListener);
		ivAutoplayControl.setOnClickListener(autoplayControlClickListener);
		vStartCollabo.setOnClickListener(Listeners.getCollaboClickListener(this, thisSong));
		vLikeSong.setOnClickListener(likeClickListener);
		vWriteComment.setOnClickListener(new MemberOnlyClickListener() {
			
			@Override
			public void onActivated(View v) {
				if (commentDialog != null) {
					commentDialog.show();
					commentDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});
	}
	
	private void displayMusic(Music music, TextView tvSinger, TextView tvTitle) {
		if (music != null) {
			tvSinger.setText(music.getSingerName());
			tvTitle.setText(music.getTitle());
		}
	}
	
	private void displaySongImage(Song song, ImageView ivLeftImage, ImageView ivRightImage) {
		if (song != null) {
			if (song.isRoot()) {
				ImageHelper.displayPhoto(song.getPhotoUrl(), ivLeftImage);
			} else {
				Song parentSong = song.getParentSong();
				
				ImageHelper.displayPhoto(parentSong.getPhotoUrl(), ivLeftImage);
				ImageHelper.displayPhoto(song.getPhotoUrl(), ivRightImage);
			}
		}
	}
	
	private void displayProfile(User user, ImageView ivPhoto, TextView tvNickname) {
		if (user != null) {
			tvNickname.setText(user.getNickname());
			
			ImageHelper.displayPhoto(user, ivPhoto);
		}
	}
	
	private void displaySongPart(Song song, TextView tvUpperUser, TextView tvLowerUser) {
		if (song != null) {
			tvUpperUser.setText(song.getParentPartName());
			tvLowerUser.setText(song.getPartName());
		}
	}
	
	private void displaySongMessage(Song song, TextView tvUpperMessage, TextView tvLowerMessage) {
		if (song != null) {
			if (song.isRoot()) {
				tvUpperMessage.setText(song.getCroppedMessage());
			} else {
				Song parentSong = song.getParentSong();
				
				tvUpperMessage.setText(parentSong.getMessage());
				tvLowerMessage.setText(song.getMessage());
			}
		}
	}
	
	private void setUserLikeSong(boolean isLike) {
		this.isLike = isLike;
		
		if (isLike) {
			ivLikeIcon.setImageResource(R.drawable.ic_like_pink);
		} else {
			ivLikeIcon.setImageResource(R.drawable.ic_like_inverse);
		}
	}
	
	private void setPlayerLooping(boolean looping) {
//		preferences.setPlayerLooping(looping);
//		playerService.setLooping(looping);
		
		if (looping) {
			ivLoopControl.setImageResource(R.drawable.ic_loop_on);
		} else {
			ivLoopControl.setImageResource(R.drawable.ic_loop_off);
		}
	}
	
	private void setPlayerAutoplay(boolean autoplay) {
//		preferences.setPlayerAutoplay(autoplay);
		
		if (autoplay) {
			ivAutoplayControl.setImageResource(R.drawable.ic_autoplay_on);
		} else {
			ivAutoplayControl.setImageResource(R.drawable.ic_autoplay_off);
		}
	}
	
	private OnClickListener loopControlClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
//			boolean isCurrentLooping = preferences.isPlayerLooping();
			boolean isCurrentLooping = true;
			
			if (isCurrentLooping) {
				Toast.makeText(PlayerActivity.this, getString(R.string.t_disable_replay), Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(PlayerActivity.this, getString(R.string.t_enable_replay), Toast.LENGTH_LONG).show();
			}
			
			setPlayerLooping(!isCurrentLooping);
		}
	};
	
	private OnClickListener autoplayControlClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
//			boolean isCurrentAutoplay = preferences.isPlayerAutoplay();
			boolean isCurrentAutoplay = true;
			
			if (isCurrentAutoplay) {
				Toast.makeText(PlayerActivity.this, getString(R.string.t_disable_autoplay), Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(PlayerActivity.this, getString(R.string.t_enable_autoplay), Toast.LENGTH_LONG).show();
			}
			
			setPlayerAutoplay(!isCurrentAutoplay);
		}
	};
	
	private OnClickListener playControlClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			ImageView playButton = (ImageView) v;
			
			if (playerService == null) {
				return;
			}
			
			/*
			if (playerService.isPlaying()) {
				playerService.pause();
				
				playButton.setImageResource(R.drawable.ic_play_neon);
				ivNeon.clearAnimation();
				ivNeon.setVisibility(View.INVISIBLE);
			} else {
				playerService.resume();
				
				playButton.setImageResource(R.drawable.ic_pause_neon);
				ivNeon.setVisibility(View.VISIBLE);
				ivNeon.startAnimation(blink);
			}
			*/
		}
	};

	private OnClickListener likeClickListener = new MemberOnlyClickListener() {
		
		@Override
		public void onActivated(View v) {
			UrlBuilder urlBuilder = new UrlBuilder();
			String url = urlBuilder.s("songs").s(thisSong.getId()).s("likings").toString();
			int method = 0;
			
			if (isLike) {
				thisSong.decrementLikeNum();
				method = Method.DELETE;
			} else {
				thisSong.incrementLikeNum();
				method = Method.POST;
			}
			
			setUserLikeSong(!isLike);
			displayLikeNum(thisSong.getWorkedLikeNum());
			
			OAuthJustRequest request = new OAuthJustRequest(method, url, null);
			RequestQueue queue = ((App) getApplicationContext()).getQueueInstance();
			queue.add(request);
		}
	};
	
	public void insertComment(Comment<?> comment) {
//		commentAdapter.insertItem(comment);
		thisSong.incrementCommentNum();
		displayCommentNum(thisSong.getCommentNum());
	}
	
	public void removeComment(Comment<?> comment) {
//		commentAdapter.removeItem(comment);
		thisSong.decrementCommentNum();
		displayCommentNum(thisSong.getCommentNum());
	}
	
	private void runService() {
		/*
		playerService.startPlaying(
				new PlayStatusCallback(this),
//				preferences.isPlayerAutoplay(),
//				preferences.isPlayerLooping()
				true, true
		);
		*/
	}
	
	/*
	private static class PlayStatusCallback implements IPlayStatusCallback {
		
		private WeakReference<PlayerActivity> weakReference;
		
		public PlayStatusCallback(PlayerActivity reference) {
			weakReference = new WeakReference<PlayerActivity>(reference);
		}

		@Override
		public void onStatusChange(int status) {
			PlayerActivity reference = weakReference.get();
			if (reference != null) {
				reference.catchPlayStatusChange(status);
			}
		}
		
	}
	
	@Override
	public void catchPlayStatusChange(final int status) {
		runOnUiThread(new Runnable() { 
			
			@Override
			public void run() {
				switch (status) {
				case IPlayStatusCallback.LOADING:
					showProgressDialog();
					break;

				case IPlayStatusCallback.PREPARED:
					setupPlayer();
					dismissProgressDialog();
					break;
					
				case IPlayStatusCallback.START:
					ivPlayControl.setImageResource(R.drawable.ic_pause_neon);
					ivNeon.startAnimation(blink);
					ivNeon.setVisibility(View.VISIBLE);
					break;
					
				case IPlayStatusCallback.STOP:
					ivPlayControl.setImageResource(R.drawable.ic_play_neon);
					ivNeon.clearAnimation();
					ivNeon.setVisibility(View.INVISIBLE);
					break;
					
				case IPlayStatusCallback.COMPLETE:
					handler.removeCallbacksAndMessages(null);
					break;
					
				case IPlayStatusCallback.RESUME:
					if (playerService.isPlaying()) {
						ivPlayControl.setImageResource(R.drawable.ic_pause_neon);
						ivNeon.startAnimation(blink);
						ivNeon.setVisibility(View.VISIBLE);
					} else {
						ivPlayControl.setImageResource(R.drawable.ic_play_neon);
						ivNeon.clearAnimation();
						ivNeon.setVisibility(View.INVISIBLE);
					}
					
					setupPlayer();
					dismissProgressDialog();
					break;
					
				case IPlayStatusCallback.ERROR:
					dismissProgressDialog();
					break;
				}
			}
		});
	}
	*/
	
	private void setupPlayer() {
		/*
		int position = playerService.getCurrentPosition();
		
		ivPlayControl.setOnClickListener(playControlClickListener);
		tvStartTime.setText(StringFormatter.getDuration(position));
		tvEndTime.setText(thisSong.getWorkedDuration());
		sbPlay.setMax(thisSong.getDuration());
		sbPlay.setProgress(position);
		sbPlay.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				seekChange(v);
				
				return false;
			}
		});
		
		playProgressUpdater();
		*/
	}
	
	private void seekChange(View v) {
		/*
		if (playerService.isPlaying()) {
			playerService.seekTo(sbPlay.getProgress());
		}
		*/
	}
	
	public void playProgressUpdater() {
		/*
		if (playerService.isPlaying()) {
			tvStartTime.setText(StringFormatter.getDuration(playerService.getCurrentPosition()));
			sbPlay.setProgress(playerService.getCurrentPosition());
		}
		
		Runnable notification = new WeakRunnable<PlayerActivity>(this, "playProgressUpdater");
		handler.postDelayed(notification, 1000);
		*/
	}

	@Override
	protected void onPause() {
		/*
		if (playerService != null) {
			if (dismissProgressDialog()) {
				playerService.setSong(null);
				playerService.stopPlaying(false);
			} else {
				playerService.setSong(playerService.getSong());
			}
		}
		*/
		
		handler.removeCallbacksAndMessages(null);
		
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (kakaotalkDialog != null) {
			kakaotalkDialog.dismiss();
			kakaotalkDialog = null;
		}
		
		if (commentDialog != null) {
			commentDialog.dismiss();
			commentDialog = null;
		}
		
		if (commentAdapter != null) {
//			commentAdapter.onDestroy();
		}
	}
	
}
