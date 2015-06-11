package com.myandb.singsong.receiver;

import com.myandb.singsong.service.TokenValidationService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RestartReceiver extends BroadcastReceiver {
	
	public static final String ACTION_RESTART_SERVICE = "RestartReceiver.Restart";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION_RESTART_SERVICE)) {
			Intent i = new Intent(context, TokenValidationService.class);
			context.startService(i);
		}
	}

}
