package com.myandb.singsong.receiver;

import com.myandb.singsong.fragment.KaraokeFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HeadsetReceiver extends BroadcastReceiver {
	
	private boolean isPlugged = false;
	private KaraokeFragment fragment;
	
	public HeadsetReceiver(KaraokeFragment fragment) {
		this.fragment = fragment;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
			int state = intent.getIntExtra("state", -1);
			
			switch (state) {
			case 0:
				isPlugged = false;
				fragment.onHeadsetUnplugged();
				
				break;
				
			case 1:
				isPlugged = true;
				fragment.onHeadsetPlugged();
				
				break;
				
			default:
				break;
			}
		}
	}
	
	public boolean isPlugged() {
		return isPlugged;
	}
	
}