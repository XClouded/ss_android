package com.myandb.singsong.audio.io;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;

public abstract class AudioInput extends UGen {
	
	protected int read;

	public AudioInput(AudioContext context, int outs) {
		super(context, outs);
	}

	public abstract void initBuffer(int size);
	
	public abstract int trigger();
	
	public abstract void destroy();
	
}
