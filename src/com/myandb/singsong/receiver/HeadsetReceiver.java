package com.myandb.singsong.receiver;

import com.myandb.singsong.activity.RecordMainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HeadsetReceiver extends BroadcastReceiver {
	
	private boolean isPlugged = false;
	private RecordMainActivity parent;
	
	public HeadsetReceiver(Context context) {
		parent = (RecordMainActivity) context;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
			int state = intent.getIntExtra("state", -1);
			
			switch (state) {
			case 0:
				isPlugged = false;
				parent.onHeadsetUnplugged();
				
				break;
				
			case 1:
				isPlugged = true;
				parent.onHeadsetPlugged();
				
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