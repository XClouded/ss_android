package com.myandb.singsong.audio;

import android.media.AudioTrack;

public class SimplePlayer extends AudioTrack {
	
	private ISimplePlayCallback callback;
	private boolean isDestroy = false;

	public SimplePlayer(int streamType, int sampleRateInHz,
			int channelConfig, int audioFormat, int bufferSizeInBytes,
			int mode) throws IllegalArgumentException {
		super(streamType, sampleRateInHz, channelConfig, audioFormat,
				bufferSizeInBytes, mode);
	}

	@Override
	public void play() throws IllegalStateException {
		super.play();
		
		if (callback != null) {
			callback.onStatusChange(ISimplePlayCallback.START);
		}
	}

	@Override
	public void stop() throws IllegalStateException {
		super.stop();
		
		if (!isDestroy && callback != null) {
			callback.onStatusChange(ISimplePlayCallback.STOP);
		}
	}
	
	@Override
	public void release() {
		isDestroy = true;
		super.release();
	}

	public void setPlayCallback(ISimplePlayCallback callback) {
		this.callback = callback;
	}
	
}
