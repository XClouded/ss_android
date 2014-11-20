package com.myandb.singsong.audio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class PcmPlayer extends AudioTrack {
	
	public static final int SAMPLERATE = 44100;
	public static final int CHANNELS = 2;
	
	private static final int BUFFER_SIZE_MULTIPLIER = 4;
	private final static int BUFFER_SIZE = AudioTrack.getMinBufferSize(
			SAMPLERATE,
			AudioFormat.CHANNEL_OUT_STEREO,
			AudioFormat.ENCODING_PCM_16BIT) * BUFFER_SIZE_MULTIPLIER; 
	
	private OnPlayEventListener listener;
	private ExecutorService service;
	private Map<String, Track> tracks;
	private boolean released;

	public PcmPlayer() throws IllegalArgumentException {
		super(
			AudioManager.STREAM_MUSIC,
			SAMPLERATE,
			AudioFormat.CHANNEL_OUT_STEREO,
			AudioFormat.ENCODING_PCM_16BIT,
			BUFFER_SIZE,
			AudioTrack.MODE_STREAM
		);
		
		this.service = Executors.newSingleThreadExecutor();
		this.tracks = new HashMap<String, Track>();
		this.released = false;
	}
	
	public void addTrack(String key, Track track) {
		if (!isPlaying()) {
			tracks.put(key, track);
		}
	}
	
	public void removeTrack(String key) {
		if (!isPlaying()) {
			tracks.remove(key);
		}
	}
	
	public Collection<Track> getTracks() {
		return tracks.values();
	}
	
	public Track getTrack(String key) {
		return tracks.get(key);
	}
	
	public int getTrackCount() {
		return tracks.size();
	}
	
	public int getBufferSize() {
		return BUFFER_SIZE;
	}
	
	public void setOnPlayEventListener(OnPlayEventListener listener) {
		this.listener = listener;
	}
	
	public void start() {
		setNotificationMarkerPosition(1);
		service.execute(new PcmWriteRunnable(this));
	}
	
	@Override
	public void play() throws IllegalStateException {
		super.play();
		if (listener != null) {
			listener.onPlay(PlayEvent.PLAY);
		}
	}

	@Override
	public void stop() throws IllegalStateException {
		super.stop();
		if (!released && listener != null) {
			listener.onPlay(PlayEvent.STOP);
		}
	}

	@Override
	public void pause() throws IllegalStateException {
		super.pause();
		if (listener != null) {
			listener.onPlay(PlayEvent.PAUSE);
		}
	}

	@Override
	public void release() {
		released = true;
		service.shutdown();
		service = null;
		tracks = null;
		super.release();
	}

	public boolean isPlaying() {
		return getPlayState() == AudioTrack.PLAYSTATE_PLAYING;
	}
	
	public long getDuration() {
		long maxDuration = 0;
		for (Track track : tracks.values()) {
			maxDuration = Math.max(maxDuration, track.getDuration());
		}
		return maxDuration;
	}
	
	public long getCurrentFrame() {
		long maxFrame = 0;
		try {
			for (Track track : tracks.values()) {
				maxFrame = Math.max(maxFrame, track.getCurrentFrame());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		return 0;
	}
	
	public long getCurrentPosition() {
		return getCurrentFrame() * 1000 / SAMPLERATE;
	}
	
	public void seekTo(int milliSeconds) {
		int difference = (int) (milliSeconds - getCurrentPosition()); 
		int differenceFrame = (difference * SAMPLERATE) / 1000;
		for (Track track : tracks.values()) {
			track.moveFrameTo(differenceFrame);
		}
	}

	private static final class PcmWriteRunnable implements Runnable {
		
		private PcmPlayer player;
		
		public PcmWriteRunnable(PcmPlayer player) {
			this.player = player;
		}

		@Override
		public void run() {
			try {
				initializeTracks();
				writePcmToPlayer();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				player.flush();
				player.stop();
				
				releaseResource();
			}
		}
		
		private void initializeTracks() throws FileNotFoundException, IllegalStateException {
			for (Track track : player.getTracks()) {
				track.startStream();
			}
		}
		
		private void writePcmToPlayer() throws IOException {
			int read = 0;
			short[] buffer = new short[player.getBufferSize() / 2];
			
			player.play();
			
			while (isPlayerStateNormal() && read != -1) {
				for (Track track : player.getTracks()) {
					int trackRead = track.read(buffer);
					read = Math.max(read, trackRead);
				}
				
				player.write(buffer, 0, read);
			}
		}
		
		private boolean isPlayerStateNormal() {
			return player.isPlaying() && player.getState() == AudioTrack.STATE_INITIALIZED;
		}
		
		private void releaseResource() {
			for (Track track : player.getTracks()) {
				try {
					track.release();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}

}
