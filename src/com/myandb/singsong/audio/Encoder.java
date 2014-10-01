package com.myandb.singsong.audio;

import java.io.IOException;

import com.myandb.singsong.audio.AudioConfig.ConfigMode;
import com.myandb.singsong.audio.io.AudioOutputOgg;
import com.myandb.singsong.event.OnCompleteListener;
import com.myandb.singsong.event.OnProgressListener;
import com.myandb.singsong.file.FileManager;
import com.myandb.singsong.libogg.VorbisFileOutputStream;

public class Encoder extends AudioComponent {
	
	private OnCompleteListener completeCallback;
	private OnProgressListener progressCallback;
	
	public void setCallback(OnCompleteListener completeCallback, OnProgressListener progressCallback) {
		this.completeCallback = completeCallback;
		this.progressCallback = progressCallback;
	}
	
    public void start(float syncAmount, boolean headsetPlugged) {
    	VorbisFileOutputStream os;
    	
		try {
			os = new VorbisFileOutputStream(FileManager.getSecure(FileManager.SONG_OGG).getAbsolutePath());
			
			AudioOutputOgg io = new AudioOutputOgg(os);
			AudioConfig audioConfig = new AudioConfig(ConfigMode.ENCODE);
			
	    	io.setEncodingCallback(completeCallback, progressCallback);
			audioContext = getAudioContext(audioConfig, io, headsetPlugged);
			if (audioContext != null) {
				setRunningOptions(syncAmount, headsetPlugged);
				audioContext.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

}
