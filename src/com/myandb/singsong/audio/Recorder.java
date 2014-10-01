package com.myandb.singsong.audio;

import java.io.File;
import java.io.FileOutputStream;

import com.myandb.singsong.audio.AudioConfig.ConfigMode;
import com.myandb.singsong.audio.io.AudioInputMIC;
import com.myandb.singsong.audio.io.AudioInputMIC.OnAudioInputStop;
import com.myandb.singsong.audio.io.AudioOutputRaw;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.IOAudioFormat;
import net.beadsproject.beads.ugens.BiquadFilter;
import net.beadsproject.beads.ugens.BiquadFilter.Type;
import net.beadsproject.beads.ugens.Compressor;
import net.beadsproject.beads.ugens.Mult;
import net.beadsproject.beads.ugens.Reverb;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;

public class Recorder {
	
	private AudioContext audioContext;
	private AutoGainController agc;
	private AudioRecord audioRecord;
	private AudioPlayback playback;
	private AudioInputMIC voiceUgen;
	private AudioConfig recorderConfig;
	private File playbackSource;
	private File recordingFile; 
	private boolean headsetPlugged;
	
	public Recorder(File playbackSource, File recordingFile) throws IllegalArgumentException, IllegalStateException {
		this.playbackSource = playbackSource;
		this.recordingFile = recordingFile;
		
		playback = new AudioPlayback(new AudioConfig(ConfigMode.PLAY));
		
		recorderConfig = new AudioConfig(ConfigMode.RECORD);
		audioRecord = new AudioRecord(
				MediaRecorder.AudioSource.MIC, 
				AudioConfig.SAMPLERATE,
				recorderConfig.getChannelConfig(),
				AudioConfig.ENCODING_CONFIG, 
				recorderConfig.getBufferSize());
		
		if (playback.getState() != AudioTrack.STATE_INITIALIZED) {
			throw new IllegalStateException();
		}
		
		if (audioRecord.getState() != AudioTrack.STATE_INITIALIZED) {
			throw new IllegalStateException();
		}
	}
	
	public void start(boolean headsetPlugged) {
		if (playback.isPlaying()) {
			stop();
		}
		
    	this.headsetPlugged = headsetPlugged;
    	
    	prepareAudioContext();
    	System.gc();
    	
    	playback.startPlaying(playbackSource, new OnPlaybackPositionUpdateListener() {
			
			@Override
			public void onPeriodicNotification(AudioTrack track) {}
			
			@Override
			public void onMarkerReached(AudioTrack track) {
				if (audioRecord != null) {
					audioRecord.startRecording();
					audioContext.start();
				}
			}
			
		});
    }
    
    public void stop() {
    	if (voiceUgen != null) {
    		voiceUgen.stop();
    	}
    }
    
    private void prepareAudioContext() {
		try {
			FileOutputStream outputStream = new FileOutputStream(recordingFile);
			AudioOutputRaw audioOutputRaw = new AudioOutputRaw(outputStream);
			audioContext = new AudioContext(
					recorderConfig.getBufferSize() / AudioConfig.BYTE_PER_FRAME, 
					audioOutputRaw, 
					new IOAudioFormat(recorderConfig));
			
			voiceUgen = (AudioInputMIC) audioOutputRaw.getAudioInput(audioRecord, playback);
			voiceUgen.setOnAudioInputStop(new OnAudioInputStop() {
				
				@Override
				public void onStop() {
					if (playback != null) {
						playback.stopPlaying();
					}
					
					if (isHeadsetPlugged() && agc != null) {
						agc.close();
					}
				}
			});
			
			if (isHeadsetPlugged()) {
				BiquadFilter highPassFilter = new BiquadFilter(audioContext, 1, Type.HP);
				highPassFilter.setFrequency(170.0f);
				highPassFilter.addInput(voiceUgen);
				
				BiquadFilter lowPassFilter = new BiquadFilter(audioContext, 1, Type.LP);
				lowPassFilter.setFrequency(16000.0f);
				lowPassFilter.addInput(highPassFilter);
				
				agc = new AutoGainController(audioContext);
				agc.addInput(lowPassFilter);
				
		    	Compressor preCompressor = new Compressor(audioContext, 1);
		    	preCompressor.setAttack(3.0f);
		    	preCompressor.setDecay(120.0f);
		    	preCompressor.setRatio(2.5f);
		    	preCompressor.setThreshold(0.18f);
		    	preCompressor.setKnee(0.8f);
		    	preCompressor.addInput(agc);
		    	
		    	Mult multiplier = new Mult(audioContext, 1, 1.6f);
		    	multiplier.addInput(preCompressor);
		    	
		    	Compressor postCompressor = new Compressor(audioContext, 1);
		    	postCompressor.setAttack(5.0f);
		    	postCompressor.setDecay(100.0f);
		    	postCompressor.setRatio(1.5f);
		    	postCompressor.setThreshold(0.6f);
		    	postCompressor.setKnee(0.4f);
		    	postCompressor.addInput(multiplier);
		    	
		    	Reverb reverb = new Reverb(audioContext, 1);
				reverb.setSize(0.75f);
				reverb.setDamping(0.6f);
				reverb.setEarlyReflectionsLevel(.15f);
				reverb.setLateReverbLevel(.35f);
				reverb.addInput(postCompressor);
			    	
				audioContext.out.addInput(postCompressor);
		    	audioContext.out.addInput(reverb);
			} else {
				audioContext.out.addInput(voiceUgen);
			}
		} catch (Exception e) {
			e.printStackTrace();
			stop();
		}
    }
    
    public boolean isHeadsetPlugged() {
    	return headsetPlugged;
    }
    
    public boolean isRecording() {
    	return playback.isPlaying();
    }
    
    public int getCurrentPosition() {
    	if (playback != null && playback.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
    		return playback.getPlaybackHeadPosition() / AudioConfig.FRAME_PER_MS; 
    	} else {
    		return 0;
    	}
    }
	
	public void setOnPlaybackStatusChangeListener(ISimplePlayCallback callback) {
		if (playback != null) {
			playback.setPlayCallback(callback);
		}
	}
    
    public void destroy() {
    	if (voiceUgen != null) {
    		voiceUgen.stop();
    		voiceUgen = null;
    	}
    	
    	if (audioRecord != null) {
    		audioRecord.release();
    		audioRecord = null;
    	}
    	
    	if (playback != null) {
    		playback.release();
    		playback = null;
    	}
    	
    	audioContext = null;
    }
	
	public static boolean isValidRecordingTime(File file) {
    	if (file != null && file.exists()) {
    		return file.length() >= getBytesLengthInSecOnRecord() * 30;
    	}
    	
    	return false;
	}
	
	private static int getBytesLengthInSecOnRecord() {
		return AudioConfig.SAMPLERATE * AudioConfig.BYTE_PER_FRAME;
	}
	
}
