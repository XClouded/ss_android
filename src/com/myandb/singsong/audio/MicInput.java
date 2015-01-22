package com.myandb.singsong.audio;

import android.media.AudioRecord;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;

public class MicInput extends UGen {
	
	private AudioRecord recorder;
	private short[] bufIn;
	
	public MicInput(AudioContext context, AudioRecord recorder) {
		super(context, 1);
		this.recorder = recorder;
		this.bufIn = new short[bufferSize];
	}

	@Override
	public void calculateBuffer() {
		int read = read(bufIn);
		
		for (int i = 0; i < read; i++) { 
			bufOut[0][i] = bufIn[i] / (float) Short.MAX_VALUE;
		}
	}
	
	private int read(short[] buffer) {
		if (recorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
			return recorder.read(buffer, 0, buffer.length);
		}
		return -1;
	}

}
