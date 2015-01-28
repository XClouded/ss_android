package com.myandb.singsong.audio;

import android.util.Log;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;

public class AutoGainController extends UGen {
	
	private static final int CREATE_FAILED = -1;
	private static final int ABSOLUTE_GATE = -48;
	private static final float MAX_NEW_GAIN = 1.4f;
	private static final int ITERATE_COUNT = 2;
	
	private float lkfs;
	private float replayGain;
	private float newGain;
	private float gainChunk;
	private float[] pcm;
	private float[] out;
	private boolean createFailed;

	static {
		System.loadLibrary("r128-stream");
	}
	
	public AutoGainController(AudioContext context) {
		super(context, 1, 1);
		
		try {
			int code = create(1, 16, 44100, 3);
			if (code == CREATE_FAILED) {
				createFailed = true;
			} else {
				createFailed = false;
			}
		} catch (Exception e) {
			e.printStackTrace(); 
		}
	}
	
	public void close() {
		this.destroy(); 
	}
	
	private native int create(int channels, int resolution, int sampleRate, int mode) throws Exception;
	private native float process(float[] buffer, int offset, int length); 
	private native void destroy();
	
	@Override
	public void calculateBuffer() {
		if (createFailed) {
			return;
		}
		
		pcm = bufIn[0];
		out = bufOut[0];
		int length = pcm.length;
		int chunkLength = length / ITERATE_COUNT; 
		
		for (int i = 0; i < ITERATE_COUNT; i++) {
			int startOffset = i * chunkLength;
			lkfs = this.process(pcm, startOffset, chunkLength);
			
			if (lkfs > ABSOLUTE_GATE) { 
				replayGain = -18 - lkfs;
				newGain = Math.max(Math.min((float) Math.pow(10, replayGain / 20), MAX_NEW_GAIN), 0.7f);
				
				for (int j = startOffset, l = startOffset + chunkLength; j < l; j++) {
					out[j] = Math.min(Math.max(pcm[j] * newGain, -1.0f), 1.0f);
				}
			} else {
				for (int j = startOffset, l = startOffset + chunkLength; j < l; j++) {
					out[j] = (float) (pcm[j] * 0.5);
				}
			}
		}
	}
	
	public float getNewGain() {
		return newGain;
	}
	
	private void logError(String msg) {
		Log.e("TAG", msg);
	}
	
}
