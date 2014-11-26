package com.myandb.singsong.service;

import java.io.File;
import java.io.IOException;

import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.audio.OnPlayEventListener;
import com.myandb.singsong.audio.PlayEvent;
import com.myandb.singsong.audio.StreamPlayer;
import com.myandb.singsong.event.OnCompleteListener;
import com.myandb.singsong.image.BitmapBuilder;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.net.DownloadManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.DiscCacheUtil;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PlayerService extends Service {
	
	public static final String JSON_KEY = "_service_song_";
	
	private final IBinder binder = new PlayerBinder();
	private Notification notification;
	private StreamPlayer streamPlayer;
	private Song thisSong;
	private File tempFile;

	@Override
	public void onCreate() {
		super.onCreate();
		
		try {
			tempFile = File.createTempFile("_music_album_", ".tmp");
		} catch (IOException e) {
			tempFile = null;
		}
		
		streamPlayer = new StreamPlayer(this);
		
		TelephonyManager phoneManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		phoneManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	private PhoneStateListener phoneStateListener = new PhoneStateListener() {
		
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				pause();
				break;
			}
			
			super.onCallStateChanged(state, incomingNumber);
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onDestroy() {
		if (streamPlayer != null) {
			streamPlayer.reset();
			streamPlayer.release();
			streamPlayer = null;
		}
		
		super.onDestroy();
	}
	
	public void startPlaying(Song song) {
		if (song == null) {
			throw new IllegalArgumentException();
		}
		
		OnPlayEventListener listener = streamPlayer.getOnPlayEventListener();
		
		try {
			streamPlayer.setOnPlayEventListener(listener);
			
			if (isNewSong(song)) {
				thisSong = song;
				listener.onPlay(PlayEvent.LOADING);
				
				try {
					clearPreviousNotification();
					
					streamPlayer.reset();
					streamPlayer.setDataSource(getCompatDataSource(thisSong.getAudioUrl()));
					streamPlayer.prepareAsync();
				} catch (Exception e) {
					listener.onPlay(PlayEvent.ERROR);
					e.printStackTrace();
				}
			} else {
				listener.onPlay(PlayEvent.RESUME);
			}
		} catch (Exception e) {
			listener.onPlay(PlayEvent.ERROR);
			e.printStackTrace();
		}
	}
	
	private boolean isNewSong(Song song) {
		return thisSong == null || (thisSong.getId() != song.getId());
	}
	
	private void clearPreviousNotification() {
		notification = null;
	}
	
	private String getCompatDataSource(String url) {
		if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
			return url;
		} else {
			return url.replaceFirst("https", "http");
		}
	}
	
	public StreamPlayer getPlayer() {
		return streamPlayer;
	}
	
	public Song getSong() {
		return thisSong;
	}
	
	public void pause() {
		try {
			if (streamPlayer != null && streamPlayer.isPlaying()) {
				streamPlayer.pause();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void prepareAndSubmitNotification() {
		if (notification != null) {
			submitNotification(notification);
		} else {
			prepareNotification();
		}
	}
	
	private void submitNotification(Notification noti) {
		this.notification = noti;
		startForeground(App.NOTI_ID_PLAY_SONG, noti);
	}
	
	private void prepareNotification() {
		String url = thisSong.getMusic().getAlbumPhotoUrl();
		File albumPhoto = getFileFromImageDiscCache(url);
		if (albumPhoto != null && albumPhoto.exists()) {
			Bitmap bitmap = getIconBitmap(albumPhoto);
			Notification noti = makeNotification(bitmap);
			submitNotification(noti);
		} else {
			downloadPhoto(url, new OnCompleteListener() {
				
				@Override
				public void done(Exception e) {
					if (e == null) {
						Bitmap bitmap = getIconBitmap(tempFile);
						Notification noti = makeNotification(bitmap);
						submitNotification(noti);
					} else {
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	private Notification makeNotification(Bitmap bitmap) {
		Intent intent = new Intent(this, RootActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		String singerName = thisSong.getMusic().getSingerName();
		String albumTitle = thisSong.getMusic().getTitle();
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setSmallIcon(R.drawable.ic_note_symbol)
			.setContentTitle(singerName)
			.setContentText(albumTitle)
			.setAutoCancel(true)
			.setLargeIcon(bitmap)
			.setTicker(singerName + " - " + albumTitle)
			.setContentIntent(pendingIntent);
		
		return builder.build();
	}
	
	private void downloadPhoto(String url, OnCompleteListener listener) {
		DownloadManager manager = new DownloadManager();
		manager.start(url, tempFile, listener);
	}
	
	private File getFileFromImageDiscCache(String url) {
		return DiscCacheUtil.findInCache(url, ImageLoader.getInstance().getDiscCache());
	}
	
	private Bitmap getIconBitmap(File file) {
		BitmapBuilder builder = new BitmapBuilder();
		int size = getNotificationIconSize();
		return builder.setSource(file)
				.setOutputSize(size)
				.enableCrop(true)
				.build();
	}

	private int getNotificationIconSize() {
		int width = getNotificationLargeIconWidth(getResources());
		int height = getNotificationLargeIconHeight(getResources());
		return Math.max(width, height);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private int getNotificationLargeIconWidth(Resources res) {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
			return (int) res.getDimensionPixelSize(android.R.dimen.notification_large_icon_width);
		}
		return 60;
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private int getNotificationLargeIconHeight(Resources res) {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
			return (int) res.getDimensionPixelSize(android.R.dimen.notification_large_icon_height);
		}
		return 60;
	}
	
	public class PlayerBinder extends Binder {
		
		public PlayerService getService() {
			return PlayerService.this;
		}
		
	}

}
