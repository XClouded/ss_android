package com.myandb.singsong.audio;

public class AutoGainWrapper {
	
	private static final int CREATE_FAILED = -1;
	private static final int DESTROY_COMPLETED = 1;
	
	private static AutoGainWrapper singleton;
	
	private boolean created;
	private boolean destroyed;
	
	static {
		System.loadLibrary("r128-stream");
	}
	
	private AutoGainWrapper() {
		created = false;
		destroyed = true; 
	}
	
	public void initialize(int channels, int resolution, int sampleRate) {
		if (!created && destroyed) {
			try {
				created = create(channels, resolution, sampleRate, 3) != CREATE_FAILED;
				destroyed = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void close() {
		if (created && !destroyed) {
			try {
				destroyed = destroy() == DESTROY_COMPLETED;
				created = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public float processSample(float[] buffer, int offset, int length) {
		return process(buffer, offset, length);
	}
	
	public boolean isAvailable() {
		return created;
	}
	
	public static AutoGainWrapper getInstance() {
		if (singleton == null) {
			singleton = new AutoGainWrapper();
		}
		return singleton;
	}
	
	private native int create(int channels, int resolution, int sampleRate, int mode);
	
	private native float process(float[] buffer, int offset, int length);
	
	private native int destroy();

}
