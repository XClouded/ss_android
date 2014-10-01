package com.myandb.singsong.audio.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AudioOutputRaw extends AudioOutput {
	
	private FileOutputStream voiceRawOut;
	private byte[] fileOut;

	public AudioOutputRaw(FileOutputStream rawOut) {
		super(false);
		
		this.voiceRawOut = rawOut;
	}

	@Override
	protected void initSetup() {
		super.initSetup();
		
		fileOut = new byte[finalOut.length * 2];
	}

	@Override
	protected boolean updateOutput() {
		if (voiceRawOut != null) {
			ByteBuffer.wrap(fileOut).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(finalOut);
			try {
				voiceRawOut.write(fileOut);
			} catch (IOException e) {
				e.printStackTrace();
				
				return false;
			}
		} else {
			return false;
		}
		
		return true;
	}

	@Override
	public void destroy() {
		super.destroy();
		
		if (voiceRawOut != null) {
			try {
				voiceRawOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			voiceRawOut = null;
		}
	}
	
}
