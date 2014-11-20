package com.myandb.singsong.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class Track {
	
	private File source;
	private FileInputStream stream;
	private int duration;
	private int channels;
	private int frameDifference;
	private long totalOffset;
	private byte[] data;
	private short[] pcm;
	private float volume;
	
	public Track(File source, int channels) {
		if (source != null && source.exists()) {
			this.source = source;
			this.channels = channels;
			this.duration = calculateSourceDuration(source, channels);
			this.volume = 1.0f;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	private int calculateSourceDuration(File source, int channels) {
		int frame = (int) (source.length() / 2) / channels;
		return frame / (PcmPlayer.SAMPLERATE / 1000);
	}
	
	public void addOffsetFrame(int offset) {
		totalOffset = Math.max(totalOffset + offset, 0);
		moveFrameTo(offset);
	}
	
	public void moveFrameTo(int frame) {
		frameDifference += frame;
	}
	
	public long getOffsetSize() {
		return totalOffset;
	}
	
	public int getSourceDuration() {
		return duration;
	}
	
	public long getDuration() {
		return duration + (totalOffset / PcmPlayer.SAMPLERATE) * 1000;
	}
	
	public void setVolume(float volume) {
		this.volume = Math.min(Math.max(volume, 0f), 1.5f);
	}
	
	public float getVolume() {
		return volume;
	}
	
	public long getFullFrame() {
		return (source.length() / 2) / channels;
	}
	
	public long getCurrentFrame() throws IllegalStateException, IOException {
		if (stream == null) {
			throw new IllegalStateException();
		}
		
		return (stream.getChannel().position() / 2) / channels;
	}
	
	public void startStream() throws IllegalStateException, FileNotFoundException {
		if (stream != null) {
			throw new IllegalStateException();
		}
		
		stream = new FileInputStream(source);
	}
	
	public int read(short[] finalPcm) throws IOException {
		if (data == null) {
			int pcmSize = finalPcm.length * channels / 2;
			pcm = new short[pcmSize];
			data = new byte[pcmSize * 2];
		}
		
		synchronizePosition();
		
		int read = stream.read(data);
		if (read != -1) {
			convertByteToShortArray(data, pcm);
			
			int convertedFrame = frameDifference * channels;
			int writableLength = read / 2 - convertedFrame;
			for (int i = 0; i < writableLength; i++) {
				if (channels == 1) {
					int j = 2 * (i + convertedFrame);
					finalPcm[j] += pcm[i] * volume;
					finalPcm[j + 1] += pcm[i] * volume;
				} else if (channels == 2) {
					int j = i + convertedFrame;
					finalPcm[j] += pcm[i] * volume;
				}
			}
		}
		
		return read;
	}
	
	private synchronized void synchronizePosition() {
		if (frameDifference != 0) {
			try {
				long position = stream.getChannel().position();
				position -= (frameDifference * channels * 2);
				
				if (position < 0) {
					frameDifference = (int) (-position / (2 * channels));
					position = 0;
				} else {
					frameDifference = 0;
				}
				
				stream.getChannel().position(position);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void convertByteToShortArray(byte[] data, short[] target) {
		ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(target);
	}
	
	public void release() throws IllegalStateException, IOException {
		pcm = null;
		data = null;
		
		if (stream == null) {
			throw new IllegalStateException();
		}
		
		stream.close();
		stream = null;
	}
	
}
