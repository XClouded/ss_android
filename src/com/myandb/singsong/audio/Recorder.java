package com.myandb.singsong.audio;

import java.io.File;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.AudioIO;
import net.beadsproject.beads.core.IOAudioFormat;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.ugens.BiquadFilter;
import net.beadsproject.beads.ugens.BiquadFilter.Type;
import net.beadsproject.beads.ugens.Compressor;
import net.beadsproject.beads.ugens.Mult;
import net.beadsproject.beads.ugens.Reverb;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;

public class Recorder extends AudioRecord {
	
	private static final int BUFFER_SIZE_MULTIPLIER = 16;
	private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(
			PcmPlayer.SAMPLERATE,
			AudioFormat.CHANNEL_IN_MONO,
			AudioFormat.ENCODING_PCM_16BIT) * BUFFER_SIZE_MULTIPLIER; 
	
	private PcmPlayer player;
	private AudioContext audioContext;
	private UGen autoGainController;
	private UGen micInput;
	private UGen postCompressor;
	private UGen reverb;
	private boolean headsetPlugged;
	private boolean recording;
	
	private Recorder() throws IllegalStateException {
		super(
			MediaRecorder.AudioSource.MIC, 
			PcmPlayer.SAMPLERATE,
			AudioFormat.CHANNEL_IN_MONO,
			AudioFormat.ENCODING_PCM_16BIT, 
			BUFFER_SIZE
		);
		
		if (getState() != AudioTrack.STATE_INITIALIZED) {
			throw new IllegalStateException();
		}
	}
	
	public Recorder(File outputFile) throws IllegalArgumentException, IllegalStateException {
		this();
		
		AudioIO audioIo = new AudioOutput(outputFile);
		audioContext = instantiateAudioContext(audioIo);
		instantiateAndChainUGens(audioContext);
	}
	
	private AudioContext instantiateAudioContext(AudioIO audioIo) {
		return new AudioContext(
				BUFFER_SIZE / 2, 
				audioIo,
				new IOAudioFormat(PcmPlayer.SAMPLERATE, 16, 1, 1));
	}

	private void instantiateAndChainUGens(AudioContext context) {
		micInput = instantiateMicInput(context);
		UGen highPassFilter = instantiateHighPassFilter(context);
		UGen lowPassFilter = instantiateLowPassFilter(context);
		autoGainController = instantiateAutoGainController(context);
		UGen preCompressor = instantiatePreCompressor(context);
		UGen multiplier = instantiateMultiplier(context);
		postCompressor = instantiatePostCompressor(context);
		reverb = instantiateReverb(context);
		
		highPassFilter.addInput(micInput);
		lowPassFilter.addInput(highPassFilter);
		autoGainController.addInput(lowPassFilter);
		preCompressor.addInput(autoGainController);
		multiplier.addInput(preCompressor);
		postCompressor.addInput(multiplier);
		reverb.addInput(postCompressor);
    }
	
	private UGen instantiateMicInput(AudioContext context) {
		return new MicInput(context, this);
	}
	
	private UGen instantiateHighPassFilter(AudioContext context) {
		BiquadFilter lowPassFilter = new BiquadFilter(context, 1, Type.HP);
		lowPassFilter.setFrequency(170.0f);
		return lowPassFilter;
	}
	
	private UGen instantiateLowPassFilter(AudioContext context) {
		BiquadFilter lowPassFilter = new BiquadFilter(context, 1, Type.LP);
		lowPassFilter.setFrequency(16000.0f);
		return lowPassFilter;
	}
	
	private UGen instantiateAutoGainController(AudioContext context) {
		return new AutoGainController(context);
	}
	
	private UGen instantiatePreCompressor(AudioContext context) {
		Compressor preCompressor = new Compressor(context, 1);
    	preCompressor.setAttack(3.0f);
    	preCompressor.setDecay(120.0f);
    	preCompressor.setRatio(2.5f);
    	preCompressor.setThreshold(0.18f);
    	preCompressor.setKnee(0.8f);
    	return preCompressor;
	}
	
	private UGen instantiateMultiplier(AudioContext context) {
		return new Mult(context, 1, 1.6f);
	}
	
	private UGen instantiatePostCompressor(AudioContext context) {
		Compressor postCompressor = new Compressor(context, 1);
    	postCompressor.setAttack(5.0f);
    	postCompressor.setDecay(100.0f);
    	postCompressor.setRatio(1.5f);
    	postCompressor.setThreshold(0.6f);
    	postCompressor.setKnee(0.4f);
    	return postCompressor;
	}
	
	private UGen instantiateReverb(AudioContext context) {
		Reverb reverb = new Reverb(context, 1);
		reverb.setSize(0.75f);
		reverb.setDamping(0.6f);
		reverb.setEarlyReflectionsLevel(.15f);
		reverb.setLateReverbLevel(.35f);
		return reverb;
	}
	
	public void setBackgroundPlayer(PcmPlayer player) {
		this.player = player;
	}
	
	public void start(boolean headsetPlugged) {
		this.headsetPlugged = headsetPlugged;
		
		stop();
		
		setAudioContextInput();
		
		if (player != null) {
			player.setPlaybackPositionUpdateListener(new OnPlaybackPositionUpdateListener() {
				
				@Override
				public void onPeriodicNotification(AudioTrack track) {}
				
				@Override
				public void onMarkerReached(AudioTrack track) {
					start();
				}
			});
			player.start();
		} else {
			start();
		}
    }
	
	private void start() {
		startRecording();
		audioContext.start();
	}
	
	private void setAudioContextInput() {
		audioContext.out.clearInputConnections();
		
		if (headsetPlugged) {
			audioContext.out.addInput(postCompressor);
			audioContext.out.addInput(reverb);
		} else {
			audioContext.out.addInput(micInput);
		}
	}
    
	@Override
	public void startRecording() throws IllegalStateException {
		super.startRecording();
		recording = true;
	}
    
    @Override
	public void stop() throws IllegalStateException {
		super.stop();
		recording = false;
		
		if (player != null && player.isPlaying()) {
			player.stop();
		}
		
		if (audioContext != null) {
			audioContext.stop();
		}
	}

	@Override
	public void release() {
		super.release();
		
		audioContext = null;
		
		if (autoGainController != null) {
			if (autoGainController instanceof AutoGainController) {
				((AutoGainController) autoGainController).close();
			}
			autoGainController = null;
		}
	}
    
    public boolean isHeadsetPlugged() {
    	return headsetPlugged;
    }
    
    public boolean isRecording() {
    	return recording;
    }
	
	public static boolean isValidRecordingTime(File file) {
    	if (file != null && file.exists()) {
    		return file.length() >= getBytesLengthInSecOnRecord() * 30;
    	}
    	
    	return false;
	}
	
	private static int getBytesLengthInSecOnRecord() {
		return PcmPlayer.SAMPLERATE * 2;
	}
	
}
