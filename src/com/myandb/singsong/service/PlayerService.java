package com.myandb.singsong.service;

import java.io.File;

import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.audio.OnPlayEventListener;
import com.myandb.singsong.audio.PlayEvent;
import com.myandb.singsong.audio.StreamPlayer;
import com.myandb.singsong.image.BitmapBuilder;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.net.DownloadManager;
import com.myandb.singsong.net.DownloadManager.OnDownloadListener;
import com.myandb.singsong.secure.Authenticator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;

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
	private StreamPlayer samplePlayer;
	private Song thisSong;

	@Override
	public void onCreate() {
		super.onCreate();
		
		streamPlayer = new StreamPlayer(this);
		samplePlayer = new StreamPlayer();
		samplePlayer.setAutoplay(true);
		
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
		if (listener == null) {
			listener = new OnPlayEventListener() {
				
				@Override
				public void onPlay(PlayEvent event) {}
			};
		}
		
		try {
			if (samplePlayer.isPlaying()) {
				samplePlayer.pause();
			}
			
			streamPlayer.setOnPlayEventListener(listener);
			
			if (isNewSong(song)) {
				thisSong = song;
				listener.onPlay(PlayEvent.LOADING);
				clearPreviousNotification();
				
				if (!Authenticator.isLoggedIn()) {
					listener.onPlay(PlayEvent.PREPARED);
					return;
				}
				
				streamPlayer.pause();
				streamPlayer.reset();
				streamPlayer.setDataSource(getCompatDataSource(thisSong.getAudioUrl()));
				streamPlayer.prepareAsync();
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
	
	public void startSample(Song song, OnPlayEventListener listener) {
		if (song == null) {
			throw new IllegalArgumentException();
		}
		
		try {
			if (streamPlayer.isPlaying()) {
				streamPlayer.pause();
			}
			
			samplePlayer.pause();
			samplePlayer.reset();
			samplePlayer.setOnPlayEventListener(listener);
			samplePlayer.setDataSource(getCompatDataSource(song.getSampleUrl()));
			samplePlayer.prepareAsync();
		} catch (Exception e) {
			listener.onPlay(PlayEvent.ERROR);
			e.printStackTrace();
		}
	}
	
	public void stopSample() {
		try {
			samplePlayer.pause();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public StreamPlayer getPlayer() {
		return streamPlayer;
	}
	
	public StreamPlayer getSamplePlayer() {
		return samplePlayer;
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
			DownloadManager manager = new DownloadManager();
			manager.start(url, new OnDownloadListener() {

				@Override
				public void onComplete(File file) {
					super.onComplete(file);
					Bitmap bitmap = getIconBitmap(file);
					file.delete();
					Notification noti = makeNotification(bitmap);
					submitNotification(noti);
				}
				
			});
		}
	}
	
	private Notification makeNotification(Bitmap bitmap) {
		Intent intent = new Intent(this, RootActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(RootActivity.EXTRA_SHOW_PLAYER, true);
		
		String singerName = thisSong.getMusic().getSingerName();
		String albumTitle = thisSong.getMusic().getTitle();
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setSmallIcon(R.drawable.ic_launcher)
			.setContentTitle(singerName)
			.setContentText(albumTitle)
			.setAutoCancel(true)
			.setLargeIcon(bitmap)
			.setTicker(singerName + " - " + albumTitle)
			.setContentIntent(pendingIntent);
		
		return builder.build();
	}
	
	private File getFileFromImageDiscCache(String url) {
		return DiskCacheUtils.findInCache(url, ImageLoader.getInstance().getDiskCache());
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
		return (int) res.getDimensionPixelSize(R.dimen.notification_icon_width);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private int getNotificationLargeIconHeight(Resources res) {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
			return (int) res.getDimensionPixelSize(android.R.dimen.notification_large_icon_height);
		}
		return (int) res.getDimensionPixelSize(R.dimen.notification_icon_height);
	}
	
	public class PlayerBinder extends Binder {
		
		public PlayerService getService() {
			return PlayerService.this;
		}
		
	}

}
