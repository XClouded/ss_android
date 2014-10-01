package com.myandb.singsong.service;

import java.io.File;
import java.io.IOException;

import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.file.Storage;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.util.ImageHelper.BitmapBuilder;
import com.myandb.singsong.util.Logger;
import com.myandb.singsong.util.Utility;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.DiscCacheUtil;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PlayerService extends Service {
	
	public static final String JSON_KEY = "_service_song_";
	private final IBinder binder = new PlayerBinder();
	private SingSongPlayer player;
	private Song thisSong = null;
	private boolean isNew = false;
	private Storage session;
	private BitmapBuilder bitmapBuilder;

	@Override
	public void onCreate() {
		super.onCreate();
		
		player = new SingSongPlayer();
		session = new Storage();
		bitmapBuilder = new BitmapBuilder();
		
		PhoneStateListener phoneStateListener = new PhoneStateListener() {

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
		
		TelephonyManager phoneManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		phoneManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onDestroy() {
		if (player != null) {
			player.reset();
			player.release();
			player = null;
		}
		
		super.onDestroy();
	}
	
	public void startPlaying(IPlayStatusCallback callback, boolean autoplay, boolean looping) {
		if (thisSong == null) {
			thisSong = Utility.getGsonInstance().fromJson(session.getString(JSON_KEY), Song.class);
		}
		
		if (isNew) {
			stopPlaying(false);
			
			try {
				callback.onStatusChange(IPlayStatusCallback.LOADING);
				
				player.setPlayStatusCallback(callback);
				if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
					player.setDataSource(thisSong.getAudioUrl());
				} else {
					player.setDataSource(thisSong.getAudioUrl().replaceFirst("https", "http"));
				}
				player.setLooping(looping);
				player.prepareAsync(autoplay);
			} catch (Exception e) {
				e.printStackTrace();
				callback.onStatusChange(IPlayStatusCallback.ERROR);
			}
		} else {
			if (player != null) {
				player.setPlayStatusCallback(callback);
			}
			callback.onStatusChange(IPlayStatusCallback.RESUME);
		}
	}
	
	private class SingSongPlayer extends MediaPlayer {
		
		private boolean prepared;
		private String dataSource;
		private IPlayStatusCallback callback;
		
		public SingSongPlayer() {
			setOnErrorListener(new OnErrorListener() {
				
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					if (callback != null) {
						callback.onStatusChange(IPlayStatusCallback.ERROR);
					}
					
					return false;
				}
			});
		}
		
		public void prepareAsync(final boolean autoplay) {
			setOnPreparedListener(new OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer mp) {
					prepared = true;
					
					mp.start();
					
					if (thisSong != null) {
						Logger.countAsync(PlayerService.this, "songs", thisSong.getId());
					}
				}
			});
			
			if (autoplay) {
				super.prepareAsync();
			}
			
			if (callback != null) {
				callback.onStatusChange(IPlayStatusCallback.PREPARED);
			}
		}

		@Override
		public void setLooping(final boolean looping) {
			setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					if (looping) {
						mp.seekTo(0);
						mp.start();
						
						if (thisSong != null) {
							Logger.countAsync(PlayerService.this, "songs", thisSong.getId());
						}
					} else {
						if (callback != null) {
							callback.onStatusChange(IPlayStatusCallback.COMPLETE);
						}
						
						try {
							((SingSongPlayer) mp).reset(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
		}

		@Override
		public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
			super.setDataSource(path);
			this.dataSource = path;
			this.prepared = false;
		}
		
		public void setPlayStatusCallback(IPlayStatusCallback callback) {
			this.callback = callback;
		}
		
		public boolean hasPrepared() {
			return prepared;
		}
		
		public boolean isSourceAvailable() {
			return dataSource == null ? false : true;
		}

		public void reset(boolean keepDataSource) {
			super.reset();
			
			if (keepDataSource) {
				try {
					if (dataSource != null) {
						setDataSource(dataSource);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			stopPlayer();
		}

		@Override
		public void start() throws IllegalStateException {
			super.start();
			
			if (callback != null) {
				callback.onStatusChange(IPlayStatusCallback.START);
			}
			
			submitNotification();
		}

		@Override
		public void pause() throws IllegalStateException {
			super.pause();
			
			stopPlayer();
		}
		
		public void stopPlayer() {
			if (callback != null) {
				callback.onStatusChange(IPlayStatusCallback.STOP);
			}
			
			stopForeground(true);
		}
		
	}
	
	private void submitNotification() {
		Bitmap largeIcon = null;
		try {
			Music music = thisSong.getMusic();
			File albumPhoto = DiscCacheUtil.findInCache(music.getAlbumPhotoUrl(), ImageLoader.getInstance().getDiscCache());
			if (albumPhoto != null && albumPhoto.exists()) {
				largeIcon = bitmapBuilder.setSource(albumPhoto)
										 .enableCrop(true)
										 .build();
			} else {
				// download if no album photo
			}
		} catch (Exception e) {
			e.printStackTrace();
			largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_note_symbol);
		}
		
		Intent intent = new Intent("com.myandb.singsong.activity.PlayerActivity");
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setSmallIcon(R.drawable.ic_note_symbol)
			   .setContentTitle(getSingerName())
			   .setContentText(getAlbumTitle())
			   .setAutoCancel(true)
			   .setLargeIcon(largeIcon)
			   .setTicker(getSingerName() + " - " + getAlbumTitle())
			   .setContentIntent(pIntent);
		
		Notification noti = builder.build();
		
		startForeground(App.NOTI_ID_PLAY_SONG, noti);
	}
	
	public void seekTo(int milliseconds) {
		if (hasPrepared()) {
			player.seekTo(milliseconds);
		}
	}
	
	public int getCurrentPosition() {
		return hasPrepared() ? player.getCurrentPosition() : 0;
	}
	
	public boolean isPlaying() {
		return hasPrepared() ? player.isPlaying() : false;
	}
	
	public boolean hasPrepared() {
		return player != null ? player.hasPrepared() : false;
	}
	
	public void resume() {
		try {
			if (player != null && thisSong != null) {
				if (!player.isSourceAvailable()) {
					player.setDataSource(thisSong.getAudioUrl());
				}
				
				if (!player.hasPrepared()) {
					player.prepareAsync();
				} else {
					player.start();
				}
			} else {
				throw new NullPointerException("player or song is null");
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void pause() {
		try {
			if (isPlaying()) {
				player.pause();
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stopPlaying(boolean keepDataSource) {
		player.reset(keepDataSource);
	}
	
	public String getSingerName() {
		if (thisSong != null) {
			Music music = thisSong.getMusic();
			
			if (music != null) {
				return music.getSingerName();
			}
		}
		
		return "가수정보가 없습니다.";
	}
	
	public String getAlbumTitle() {
		if (thisSong != null) {
			Music music = thisSong.getMusic();
			
			if (music != null) {
				return music.getTitle();
			}
		}
		
		return "노래정보가 없습니다.";
	}
	
	public void setSong(Song nowSong) {
		if (thisSong != null && nowSong == null) {
			isNew = true;
		} else if (thisSong == null || (nowSong != null && thisSong.getId() != nowSong.getId())) {
			thisSong = nowSong;
			isNew = true;
			
			stopForeground(true);
			
			session.putString(JSON_KEY, Utility.getGsonInstance().toJson(thisSong, Song.class));
		} else {
			isNew = false;
		}
	}
	
	public Song getSong() {
		return thisSong;
	}
	
	public void setLooping(boolean looping) {
		player.setLooping(looping);
	}
	
	public class PlayerBinder extends Binder {
		public PlayerService getService() {
			return PlayerService.this;
		}
	}
	
	public interface IPlayStatusCallback {
		
		public static final int LOADING = 1;
		public static final int PREPARED = 2;
		public static final int START = 3;
		public static final int STOP = 4;
		public static final int COMPLETE = 5;
		public static final int RESUME = 6;
		public static final int ERROR = 7;
		
		public void onStatusChange(int status);
		
	}

}
