package com.myandb.singsong.audio;

public class AutoGainWrapper {
	
	private static final int CREATE_FAILED = -1;
	private static final int DESTROY_COMPLETED = 1;
	
	private static AutoGainWrapper singleton;
	
	private boolean created;
	
	static {
		System.loadLibrary("r128-stream");
	}
	
	private AutoGainWrapper() {
		created = false;
	}
	
	public synchronized void initialize(int channels, int resolution, int sampleRate) {
		if (!created) {
			try {
				created = create(channels, resolution, sampleRate, 3) != CREATE_FAILED;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void close() {
		// due to native memory error
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
