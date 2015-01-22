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
	private int totalOffset;
	private byte[] data;
	private short[] pcm;
	private float volume;
	
	public Track(File source, int channels) throws IOException, IllegalArgumentException {
		if (source != null) {
			if (!source.exists()) {
				source.createNewFile();
			}
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
		totalOffset += offset;
		moveFrameTo(-offset);
	}
	
	public void moveFrameTo(int frame) {
		frameDifference += frame;
	}
	
	public int getOffsetSize() {
		return totalOffset;
	}
	
	public int getSourceDuration() {
		return duration;
	}
	
	public long getDuration() {
		return duration + (totalOffset / PcmPlayer.SAMPLERATE) * 1000;
	}
	
	public void setVolume(float volume) {
		this.volume = Math.min(Math.max(volume, 0f), 2.0f);
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
		frameDifference = -totalOffset;
	}
	
	public int read(short[] stereoPcm) throws IOException {
		if (data == null) {
			int pcmSize = stereoPcm.length * channels / 2;
			pcm = new short[pcmSize];
			data = new byte[pcmSize * 2];
		}
		
		synchronizePosition();
		
		int byteRead = stream.read(data);
		if (byteRead != -1) {
			int shortRead = byteRead / 2;
			convertByteToShortArray(data, pcm);
			
			int skipFrame = Math.abs(frameDifference * channels);
			int writableLength = shortRead - skipFrame;
			if (channels == 1) {
				for (int i = 0; i < writableLength; i++) {
					int j = 2 * (i + skipFrame);
					stereoPcm[j] += pcm[i] * volume;
					stereoPcm[j + 1] += pcm[i] * volume;
				}
				return shortRead * 2;
			} else if (channels == 2) {
				for (int i = 0; i < writableLength; i++) {
					int j = i + skipFrame;
					stereoPcm[j] += pcm[i] * volume;
				}
				return shortRead;
			}
		}
		
		return byteRead;
	}
	
	private synchronized void synchronizePosition() {
		if (Math.abs(frameDifference) > 0) {
			try {
				long position = stream.getChannel().position();
				position += frameDifference * channels * 2;
				
				if (position < 0) {
					frameDifference = (int) (position / (2 * channels));
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
	
	public void release() throws IOException {
		pcm = null;
		data = null;
		
		if (stream != null) {
			stream.close();
			stream = null;
		}
	}
	
}
