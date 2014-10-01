package com.myandb.singsong.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;

public class AudioConfig {
	
	public enum ConfigMode { RECORD, PLAY, ENCODE }
	
	public static final int SAMPLERATE = 44100;
	
	public static final int FRAME_PER_MS = SAMPLERATE / 1000;
	
	public static final int BIT_DEPTH = 16;
	
	public static final int ENCODING_CONFIG = AudioFormat.ENCODING_PCM_16BIT;
	
	public static final int BYTE_PER_FRAME = 2;
	
	private ConfigMode mode;
	
	public AudioConfig(ConfigMode mode) {
		this.mode = mode;
	}
	
	public int getChannels() {
		switch (mode) {
		case RECORD:
			return 1;
			
		case PLAY:
		case ENCODE:
			return 2;
			
		default:
			return 0;
		}
	}
	
	public int getBytesLengthPerSec() {
		return SAMPLERATE * BYTE_PER_FRAME * getChannels();
	}
	
	public int getChannelConfig() {
		switch (mode) {
		case RECORD:
			return AudioFormat.CHANNEL_IN_MONO;
			
		case PLAY:
		case ENCODE:
			return AudioFormat.CHANNEL_OUT_STEREO;
			
		default:
			return 0;
		}
	}
	
	public int getBufferSize() {
		switch (mode) {
		case RECORD:
			return AudioRecord.getMinBufferSize(SAMPLERATE, getChannelConfig(), ENCODING_CONFIG) * 16;
			
		case PLAY:
		case ENCODE:
			return AudioTrack.getMinBufferSize(SAMPLERATE, getChannelConfig(), ENCODING_CONFIG) * 4;
			
		default:
			return 0;
		}
	}
    
}

