package com.myandb.singsong.audio;

public interface ISimplePlayCallback {
	
	public static final int START = 1;
	public static final int STOP = 2;
	
	public void onStatusChange(int status);
	
}
