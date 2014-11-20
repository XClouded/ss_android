package com.myandb.singsong.audio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import net.beadsproject.beads.core.AudioIO;
import net.beadsproject.beads.core.UGen;

public class AudioOutput extends AudioIO {
	
	private File file;
	private OutputStream outputStream;
	private Thread writeThread;
	
	public AudioOutput(File file) throws IllegalArgumentException {
		if (file != null && file.exists()) {
			this.file = file;
			this.writeThread = instantiateWriteThread();
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	private Thread instantiateWriteThread() {
		Thread thread = new Thread(writeRunnable);
		thread.setPriority(Thread.MAX_PRIORITY);
		return thread;
	}
	
	private Runnable writeRunnable = new Runnable() {
		
		@Override
		public void run() {
			try {
				writeToFile();
			} catch (FileNotFoundException e) {
				// This cannot be happened
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				release();
			}
		}
	};

	@Override
	protected boolean start() {
		writeThread.start();
		return true;
	}

	private void writeToFile() throws FileNotFoundException, IOException {
		outputStream = new FileOutputStream(file);
		short[] pcm = new short[context.getBufferSize()];
		byte[] data = new byte[pcm.length * 2];
		
		while (context.isRunning()) {
			update();
			
			readFromContext(pcm);
			
			convertShortToByteArray(pcm, data);
			
			outputStream.write(data);
		}
	}
	
	private void readFromContext(short[] buffer) {
		for (int i = 0, l = buffer.length; i < l; i++) {
			buffer[i] = (short) ((Short.MAX_VALUE * Math.min(Math.max(context.out.getValue(0, i), -1.0f), 1.0f)));
		}
	}
	
	private void convertShortToByteArray(short[] pcm, byte[] data) {
		ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(pcm);
	}
	
	private void release() {
		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			outputStream = null;
		}
	}

	@Override
	protected UGen getAudioInput(int[] channels) {
		return null;
	}

}