package com.myandb.singsong.audio.io;

import java.io.IOException;

import com.myandb.singsong.libogg.VorbisFileOutputStream;

public class AudioOutputOgg extends AudioOutput {

	private VorbisFileOutputStream oggOut;

	public AudioOutputOgg(VorbisFileOutputStream oggOut) {
		super(true);
		
		this.oggOut = oggOut;
	}

	@Override
	protected boolean updateOutput() {
		if (!super.updateOutput()) {
			return false;
		}
		
		try {
			if (oggOut != null) {
				oggOut.write(finalOut);
			} else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			
			return false;
		}
		
		return true;
	}

	@Override
	public void destroy() {
		super.destroy();
		
		try {
			if (oggOut != null) {
				oggOut.close();
				oggOut = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
