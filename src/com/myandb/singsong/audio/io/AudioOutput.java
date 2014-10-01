package com.myandb.singsong.audio.io;

import java.io.FileInputStream;
import java.io.InputStream;

import com.myandb.singsong.event.OnCompleteListener;
import com.myandb.singsong.event.OnProgressListener;
import com.myandb.singsong.file.FileManager;

import android.media.AudioRecord;
import android.media.AudioTrack;
import net.beadsproject.beads.core.AudioIO;
import net.beadsproject.beads.core.IOAudioFormat;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.audiofile.AudioFile;

public abstract class AudioOutput extends AudioIO {
	
	protected short[] finalOut;
	protected AudioInput audioInput;
	private int read = 0;
	private int channels;
	private int buffSizeInBytes;
	private int bufferSizeInFrames;
	private Thread audioThread;
	private boolean needBytesCheck;
	private int i, j;
	private long maxTotalReadBytes;
	private long totalReadBytes;
	private long bytesInPercent;
	private int currentProgress;
	private int preProgress;
	private OnCompleteListener completeCallback;
	private OnProgressListener progressCallback;
	
	public AudioOutput(boolean needBytesCheck) {
		this.needBytesCheck = needBytesCheck;
		
		audioThread = new Thread(new Runnable() {
			public void run() {
				try {
					initSetup();
					runRealTime();
				} finally {
					destroy();
				}
			}
		});
		
		audioThread.setPriority(Thread.MAX_PRIORITY);
	}
	
	public void setEncodingCallback(OnCompleteListener cCallback, OnProgressListener pCallback) {
		this.completeCallback = cCallback;
		this.progressCallback = pCallback;
	}

	@Override
	protected boolean start() {
		audioThread.start();
		
		return true;
	}
	
	protected void initSetup() {
		IOAudioFormat audioFormat = getContext().getAudioFormat();
		channels = audioFormat.outputs;
		bufferSizeInFrames = getContext().getBufferSize();
		buffSizeInBytes = bufferSizeInFrames * 2;
		
		audioInput.initBuffer(bufferSizeInFrames);
		finalOut = new short[bufferSizeInFrames * channels];
		
		if (needBytesCheck) {
			totalReadBytes = 0;
			maxTotalReadBytes = FileManager.getSecure(FileManager.MUSIC_RAW).length() / 2;
			
			bytesInPercent = FileManager.getSecure(FileManager.VOICE_RAW).length() / 90;
			currentProgress = 0;
			preProgress = 0;
		}
	}

	private void runRealTime() {
		preLooping();
		
		while (context.isRunning()) {
			
			if (!updateInput()) {
				break;
			}
			
			update(); // update context
			
			for (i = 0; i < bufferSizeInFrames; ++i) {
				int outIndex = i * channels;
				
				for (j = 0; j < channels; ++j) {
					finalOut[outIndex + j] = (short) ((32767. * Math.min(Math.max(context.out.getValue(j, i), -1.0f), 1.0f)));
				} 
			}
			
			if (!updateOutput()) {
				break;
			}
		}
	}
	
	protected void preLooping() {  }
	
	protected boolean updateInput() {
		read = audioInput.trigger();
		if (needBytesCheck) {
			totalReadBytes += read;
			if (totalReadBytes > maxTotalReadBytes) {
				return false;
			}
		}
		
		if (read < 0) {
			return false;
		}
		
		return true;
	}
	
	protected boolean updateOutput() {
		if (progressCallback != null) {
			currentProgress = (int) (totalReadBytes / bytesInPercent);
			if (currentProgress > preProgress) {
				progressCallback.done(currentProgress);
				preProgress = currentProgress;
			}
		}
		
		return true;
	}
	
	public void destroy() {
		if (audioInput != null) {
			audioInput.destroy();
			audioInput = null;
		}
		
		if (audioThread != null) {
			audioThread = null;
		}
		
		if (completeCallback != null) {
			completeCallback.done(null);
		}
	}

	public UGen getAudioInput(FileInputStream is) {
		audioInput = new AudioInputFile(context, 2, is);
		
		return audioInput;
	}
	
	public UGen getAudioInput(AudioRecord recorder, AudioTrack track) {
		audioInput = new AudioInputMIC(context, 1, recorder, track);
		
		return audioInput;
	}

	@Override
	public AudioFile getAudioFile(InputStream stream) {
		return null;
	}

	@Override
	public AudioFile getAudioFile(String filename) {
		return null;
	}

	@Override
	public int getIntBuffSize() {
		return buffSizeInBytes;
	}

	@Override
	protected UGen getAudioInput(int[] channels) {
		return null;
	}

}