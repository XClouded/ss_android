package com.myandb.singsong.audio;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioPlayback extends SimplePlayer {
	
	private AudioConfig audioConfig;
	private ExecutorService service;
	private boolean isPlaying;
	
	public AudioPlayback(AudioConfig audioConfig) throws IllegalArgumentException {
		super(AudioManager.STREAM_MUSIC,
				AudioConfig.SAMPLERATE,
				audioConfig.getChannelConfig(),
				AudioConfig.ENCODING_CONFIG,
				audioConfig.getBufferSize(),
				AudioTrack.MODE_STREAM);
		
		this.audioConfig = audioConfig;
		
		service = Executors.newSingleThreadExecutor(); 
	}
	
	public AudioConfig getConfig() {
		return audioConfig;
	}
	
	public boolean isPlaying() {
		return isPlaying;
	}
	
	public void startPlaying(File source, OnPlaybackPositionUpdateListener playbackStartListener) {
		setNotificationMarkerPosition(1);
		setPlaybackPositionUpdateListener(playbackStartListener);
		
		startPlaying(source);
	}
	
	public void startPlaying(File source) {
		isPlaying = true;
		
		service.execute(new PlaybackWriteRunnable(this, source));
	}
	
	public void stopPlaying() {
		isPlaying = false;
	}
	
	@Override
	public void release() {
		stopPlaying();
		service.shutdown();
		
		super.release();
	}

	private static class PlaybackWriteRunnable implements Runnable {
		
		private File source;
		private AudioPlayback playback;
		
		public PlaybackWriteRunnable(AudioPlayback playback, File source) {
			this.playback = playback;
			this.source = source;
		}

		@Override
		public void run() {
			try {
				FileInputStream inputStream = new FileInputStream(source);
				
				int read = 0;
				byte[] buffer = new byte[playback.getConfig().getBufferSize()];
				
				playback.play();
				
				while ( playback.isPlaying() && read != -1 && playback.getState() == AudioTrack.STATE_INITIALIZED ) { 
					read = inputStream.read(buffer);
					playback.write(buffer, 0, read);
				} 
				
				inputStream.close();
				
				playback.flush();
				playback.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

}
