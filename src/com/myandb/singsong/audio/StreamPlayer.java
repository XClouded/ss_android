package com.myandb.singsong.audio;

import java.io.IOException;

import com.myandb.singsong.service.PlayerService;

import android.media.MediaPlayer;

public class StreamPlayer extends MediaPlayer {
	
	private boolean prepared;
	private boolean autoplay;
	private boolean initialStart;
	private String dataSource;
	private OnPlayEventListener listener;
	private PlayerService service;
	
	public StreamPlayer() {
		this.setOnErrorListener(errorListener);
		this.setOnPreparedListener(preparedListener);
		this.setOnCompletionListener(completionListener);
	}
	
	public StreamPlayer(PlayerService service) {
		this();
		this.service = service;
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
			prepared = false;
			if (listener != null) {
				listener.onPlay(PlayEvent.COMPLETED);
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
	
	public void setOnPlayEventListener(OnPlayEventListener listener) {
		this.listener = listener;
	}
	
	public OnPlayEventListener getOnPlayEventListener() {
		return listener;
	}
	
	public boolean isPrepared() {
		return prepared;
	}
	
	public boolean isSourceAvailable() {
		return dataSource != null;
	}
	
	public void startIfPrepared() {
		if (isPrepared()) {
			start();
		} else {
			try {
				reset();
				setDataSource(dataSource);
				prepareAsync();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void start() throws IllegalStateException {
		super.start();
		
		if (listener != null) {
			listener.onPlay(PlayEvent.PLAY);
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
	
}
