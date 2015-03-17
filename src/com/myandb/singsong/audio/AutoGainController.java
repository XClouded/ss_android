package com.myandb.singsong.audio;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;

public class AutoGainController extends UGen {
	
	private static final int ABSOLUTE_GATE = -48;
	private static final float MAX_NEW_GAIN = 1.4f;
	private static final int ITERATE_COUNT = 2;
	
	private float lkfs;
	private float replayGain;
	private float newGain;
	private float[] pcm;
	private float[] out;
	
	public AutoGainController(AudioContext context) {
		super(context, 1, 1);
		AutoGainWrapper.getInstance().initialize(1, 16, PcmPlayer.SAMPLERATE);
	}
	
	@Override
	public void calculateBuffer() {
		pcm = bufIn[0];
		out = bufOut[0];
		
		if (!AutoGainWrapper.getInstance().isAvailable()) {
			for (int i = 0, l = pcm.length; i < l; i++) {
				out[i] = pcm[i];
			}
			return;
		}
		
		int length = pcm.length;
		int chunkLength = length / ITERATE_COUNT; 
		
		for (int i = 0; i < ITERATE_COUNT; i++) {
			int startOffset = i * chunkLength;
			lkfs = AutoGainWrapper.getInstance().processSample(pcm, startOffset, chunkLength);
			
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
	
	public void close() {
		AutoGainWrapper.getInstance().close();
	}
	
}
