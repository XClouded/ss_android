package com.myandb.singsong.audio;

import com.myandb.singsong.audio.AudioConfig.ConfigMode;
import com.myandb.singsong.audio.io.AudioOutputTrack;
import com.myandb.singsong.file.FileManager;

import android.media.AudioManager;
import android.media.AudioTrack;

public class Player extends AudioComponent {
	
	private SimplePlayer player;
	private boolean paused;
	private AudioConfig audioConfig;

	public Player() throws IllegalArgumentException {
		paused = false;
		
		audioConfig = new AudioConfig(ConfigMode.PLAY);
		
		player = new SimplePlayer(
				AudioManager.STREAM_MUSIC,
				AudioConfig.SAMPLERATE,
				audioConfig.getChannelConfig(),
				AudioConfig.ENCODING_CONFIG,
				audioConfig.getBufferSize(),
				AudioTrack.MODE_STREAM);
	}
	
	public void destroy() {
		if (player != null) {
			player.release();
			player = null;
		}
	}
	
	public void setCallback(ISimplePlayCallback callback) {
		player.setPlayCallback(callback);
	}
	
	public void start(float syncAmount, boolean headsetPlugged) {
		if (paused) {
			player.play();
		} else {
			System.gc();
			
			audioContext = getAudioContext(audioConfig, new AudioOutputTrack(player), headsetPlugged);
			if (audioContext != null) {
				setRunningOptions(syncAmount, headsetPlugged);
				audioContext.start();
			}
		}
	}
	
	public boolean isPlaying() {
		if (player.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
			return true;
		} else {
			return false;
		}
	}
	
	public int getDuration() {
		return convertBytesToMilliSec(FileManager.getSecure(FileManager.VOICE_RAW).length(), 1);
	}
	
	public int getCurrentPosition() {
		if (player != null && player.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
			return convertBytesToMilliSec(voiceUgen.getCurrentReadByte(), 2);
    	} else {
    		return 0;
    	}
	}
	
	private int convertBytesToMilliSec(long bytesLength, int channels) {
		int frame = (int) (bytesLength / AudioConfig.BYTE_PER_FRAME) / channels;
		
		return frame / AudioConfig.FRAME_PER_MS;
	}
	
	public void seekTo(int positionInSec) {
		voiceUgen.seekTo(positionInSec);
	}
	
}
