package com.myandb.singsong.audio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.myandb.singsong.audio.io.AudioOutput;
import com.myandb.singsong.audio.io.AudioInputFile;
import com.myandb.singsong.file.FileManager;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.IOAudioFormat;

public abstract class AudioComponent {
	
	protected AudioInputFile voiceUgen;
	protected AudioContext audioContext;

	protected AudioContext getAudioContext(AudioConfig config, AudioOutput io, boolean headsetPlugged) {
    	AudioContext ac = new AudioContext(config.getBufferSize() / AudioConfig.BYTE_PER_FRAME, io, new IOAudioFormat(config));
    	
		try {
			FileInputStream voiceIs = new FileInputStream(FileManager.getSecure(FileManager.VOICE_RAW));
			voiceUgen = (AudioInputFile)io.getAudioInput(voiceIs);
			
			if (headsetPlugged) {
				FileInputStream musicIs = new FileInputStream(FileManager.getSecure(FileManager.MUSIC_RAW));
				voiceUgen.setStereoInput(musicIs);
		    	
		    	ac.out.addInput(voiceUgen);
			} else {
				ac.out.addInput(voiceUgen);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return ac;
    }
	
	protected void setRunningOptions(float syncAmount, boolean headsetPlugged) {
		if (headsetPlugged) {
			adjustSync(syncAmount);
		}
	}
    
    public void adjustSync(float sec) {
    	if (voiceUgen != null) {
    		long sampleNum = (long) (AudioConfig.SAMPLERATE * AudioConfig.BYTE_PER_FRAME * sec);
    		voiceUgen.adjustSync(sampleNum);
    	}
    }
    
    public void stop() {
    	if (audioContext != null) {
    		audioContext.stop();
    		audioContext = null;
    	}
    }
	
}
