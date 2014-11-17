package com.myandb.singsong.audio;

import java.io.IOException;

import com.myandb.singsong.R;
import com.myandb.singsong.service.PlayerService;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;

public class StreamPlayer extends MediaPlayer {
	
	private boolean prepared;
	private boolean autoplay;
	private boolean looping;
	private boolean initialStart;
	private String dataSource;
	private PlayEventListener listener;
	private PlayerService service;
	
	public StreamPlayer() {
		this.setOnErrorListener(errorListener);
		this.setOnPreparedListener(preparedListener);
		this.setOnCompletionListener(completionListener);
	}
	
	public StreamPlayer(PlayerService service) {
		this();
		this.service = service;
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(service);
		final String keyLooping = service.getString(R.string.key_player_looping);
		final String keyAutoplay = service.getString(R.string.key_player_autoplay);
		boolean looping = preferences.getBoolean(keyLooping, false);
		boolean autoplay = preferences.getBoolean(keyAutoplay, false);
		
		setLooping(looping);
		setAutoplay(autoplay);
		
		preferences.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
			
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				if (key.equals(keyLooping)) {
					boolean looping = sharedPreferences.getBoolean(key, false);
					setLooping(looping);
				}
				
				if (key.equals(keyAutoplay)) {
					boolean autoplay = sharedPreferences.getBoolean(key, false);
					setAutoplay(autoplay);
				}
			}
		});
	}
	
	private OnErrorListener errorListener = new OnErrorListener() {
		
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			if (listener != null) {
				listener.onPlay(PlayEvent.ERROR);
			}
			return false;
		}
	};
	
	private OnPreparedListener preparedListener = new OnPreparedListener() {
		
		@Override
		public void onPrepared(MediaPlayer mp) {
			prepared = true;
			initialStart = true;
			if (listener != null) {
				listener.onPlay(PlayEvent.PREPARED);
			}
			
			if (autoplay) {
				mp.start();
			}
		}
	};
	
	private OnCompletionListener completionListener = new OnCompletionListener() {
		
		@Override
		public void onCompletion(MediaPlayer mp) {
			mp.seekTo(0);
			
			if (looping) {
				mp.start();
			} else {
				if (listener != null) {
					listener.onPlay(PlayEvent.COMPLETED);
				}
				
				mp.pause();
			}
		}
	};
	
	@Override
	public void setDataSource(String path) 
			throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
		super.setDataSource(path);
		this.dataSource = path;
		this.prepared = false;
	}
	
	public void setOnPlayEventListener(PlayEventListener listener) {
		this.listener = listener;
	}
	
	public boolean isPrepared() {
		return prepared;
	}
	
	public boolean isSourceAvailable() {
		return dataSource != null;
	}

	@Override
	public void start() throws IllegalStateException {
		super.start();
		
		if (listener != null) {
			listener.onPlay(PlayEvent.START);
			if (initialStart) {
				listener.onPlay(PlayEvent.BUFFERED);
				initialStart = false;
			}
		}
		
		if (service != null) {
			service.prepareAndSubmitNotification();
		}
	}

	@Override
	public void pause() throws IllegalStateException {
		super.pause();
		
		if (listener != null) {
			listener.onPlay(PlayEvent.PAUSE);
		}
		
		if (service != null) {
			service.stopForeground(true);
		}
	}
	
	public void setAutoplay(boolean autoplay) {
		this.autoplay = autoplay;
	}
	
	@Override
	public void setLooping(boolean looping) {
		this.looping = looping;
	}
	
}
