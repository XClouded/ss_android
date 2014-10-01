package com.myandb.singsong.audio.io;

import android.media.AudioRecord;
import android.media.AudioTrack;
import net.beadsproject.beads.core.AudioContext;

public class AudioInputMIC extends AudioInput {
	
	private AudioRecord recorder;
	private boolean stop;
	private OnAudioInputStop stopCallback;
	private short[] bufIn;
	
	public AudioInputMIC(AudioContext context, int outs, AudioRecord recorder, AudioTrack track) {
		super(context, outs);
		
		this.recorder = recorder;
		this.stop = false;
	}
	
	@Override
	public void initBuffer(int size) {
		bufIn = new short[size];
	}

	@Override
	public int trigger() {
		read = recorder.read(bufIn, 0, bufIn.length);
		
		if (stop) {
			if (stopCallback != null) {
				stopCallback.onStop();
			}
			
			return -1;
		}
		
		return read;
	}

	@Override
	public void calculateBuffer() {
		for (int i = 0; i < bufferSize; i++) { 
			for (int j = 0; j < 1; j++) {
				bufOut[j][i] = bufIn[i] / 32768.0F;
			}
		}
	}

	@Override
	public void destroy() {
		try {
			 recorder.stop();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		this.stop = true;
	}
	
	public void setOnAudioInputStop(OnAudioInputStop callback) {
		this.stopCallback = callback;
	}
	
	public interface OnAudioInputStop {
		
		public void onStop();
		
	}

}
