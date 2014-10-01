package com.myandb.singsong.audio.io;

import android.media.AudioTrack;

public class AudioOutputTrack extends AudioOutput {

	private AudioTrack audioOut;
	
	public AudioOutputTrack(AudioTrack track) {
		super(true);
		
		this.audioOut = track;
	}

	@Override
	protected void preLooping() {
		super.preLooping();
		
		try {
			audioOut.play();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			
			audioOut = null;
		}
	}

	@Override
	protected boolean updateOutput() {
		if (!super.updateOutput()) {
			return false;
		}
		
		if (audioOut != null && audioOut.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
			audioOut.write(finalOut, 0, finalOut.length);
		} else {
			return false;
		}
		
		return true;
	}

	@Override
	public void destroy() {
		super.destroy();
		
		if (audioOut != null) {
			if (audioOut.getState() == AudioTrack.STATE_INITIALIZED) {
				try {
					audioOut.flush();
					audioOut.stop();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
