package com.myandb.singsong.audio.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.myandb.singsong.audio.AudioConfig;

import net.beadsproject.beads.core.AudioContext;

public class AudioInputFile extends AudioInput {
	
	private FileInputStream mono;
	private FileInputStream stereo;
	private boolean hasAdjusted = true;
	private long adjustSizeInByte = 0;
	private int musicRead;
	private short[] pcmMonoIn;
	private byte[] bufMonoInByte;
	private short[] pcmStereoIn;
	private byte[] bufStereoInByte;
	private long musicTotalReadInByte;

	public AudioInputFile(AudioContext context, int outs, FileInputStream is) {
		super(context, outs);
		
		this.mono = is;
		musicTotalReadInByte = 0;
	}
	
	public void setStereoInput(FileInputStream is) {
		this.stereo = is;
	}

	@Override
	public void initBuffer(int size) {
		pcmMonoIn = new short[size];
		bufMonoInByte = new byte[pcmMonoIn.length * 2];
		if (stereo != null) {
			pcmStereoIn = new short[size * 2];
			bufStereoInByte = new byte[pcmStereoIn.length * 2];
			musicRead = 0;
		}
	}

	@Override
	public int trigger() {
		try {
			performSync();
			read = mono.read(bufMonoInByte);
			ByteBuffer.wrap(bufMonoInByte).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(pcmMonoIn);
			
			if (stereo != null) {
				musicRead = stereo.read(bufStereoInByte);
				musicTotalReadInByte += musicRead;
				ByteBuffer.wrap(bufStereoInByte).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(pcmStereoIn);
				
				if (musicRead < 0) {
					return -1;
				}
			} else {
				musicTotalReadInByte += read;
			}
		} catch (IOException e) {
			e.printStackTrace();
			
			return -1;
		}
		
		return read;
	}

	@Override
	public void calculateBuffer() {
		int validRead = read / 2;
		
		for (int i = 0, l = pcmMonoIn.length; i < l; i++) {
			int sIndex = 2 * i;
			
			for (int j = 0, ll = 2 /* stereo */; j < ll; j++) {
				if (i > validRead) {
					bufOut[j][i] = 0f;
				} else if (stereo != null) {
					bufOut[j][i] = (float) (pcmMonoIn[i] + pcmStereoIn[sIndex + j]) / 32768.0F;
				} else {
					bufOut[j][i] = (float) (pcmMonoIn[i]) / 32768.0F;
				}
			}
		}
	}

	@Override
	public void destroy() {
		try {
			if (mono != null) {
				mono.close();
				mono = null;
			} 
			
			if (stereo != null) {
				stereo.close();
				stereo = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public FileInputStream getInputStream() {
		return mono;
	}
	
	private synchronized void performSync() {
		if (!hasAdjusted) {
			try {
				long position = mono.getChannel().position();
				position += adjustSizeInByte;
				
				if (position < 0) {
					adjustSizeInByte = position;
					position = 0;
				} else {
					adjustSizeInByte = 0;
					hasAdjusted = true;
				}
				
				mono.getChannel().position(position);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void adjustSync(long size) {
		adjustSizeInByte += size;
		hasAdjusted = false;
	}
	
	public void seekTo(int positionInSec) {
		long musicTargetBytePosition = positionInSec * AudioConfig.BYTE_PER_FRAME * AudioConfig.FRAME_PER_MS;
		if (stereo != null) {
			musicTargetBytePosition *= 2;
		}
		
		long offset = musicTargetBytePosition - musicTotalReadInByte;
		long currentPosition;
		long newPosition;
		
		try {
			synchronized (mono) {
				currentPosition = mono.getChannel().position();
				newPosition = currentPosition;
				if (stereo != null) {
					newPosition += offset / 2;
				} else {
					newPosition += offset;
				}
				
				if (newPosition < 0 || newPosition > mono.getChannel().size()) {
					return;
				} else {
					mono.getChannel().position(newPosition);
				}
			}
			
			if (stereo != null) {
				synchronized (stereo) {
					currentPosition = stereo.getChannel().position();
					newPosition = currentPosition + offset;
					stereo.getChannel().position(newPosition);
				}
			}
				
			musicTotalReadInByte = musicTargetBytePosition;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public long getCurrentReadByte() {
		if (stereo != null) {
			return musicTotalReadInByte;
		} else {
			return musicTotalReadInByte * 2;
		}
	}

}
