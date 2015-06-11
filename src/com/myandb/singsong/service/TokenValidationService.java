package com.myandb.singsong.service;

import org.json.JSONObject;

import com.myandb.singsong.App;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JSONObjectSuccessListener;
import com.myandb.singsong.net.MelonResponseHooker;
import com.myandb.singsong.net.MelonResponseHooker.MelonResponseException;
import com.myandb.singsong.receiver.RestartReceiver;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.util.Logger;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

public class TokenValidationService extends Service {
	
	private static final int PERIOD_TIME = 60 * 60 * 1000;

	@Override
	public void onCreate() {
		super.onCreate();
		registerRestartAlarm(getRestartIntent());
	}
	
	private PendingIntent getRestartIntent() {
		Intent intent = new Intent(this, RestartReceiver.class);
		intent.setAction(RestartReceiver.ACTION_RESTART_SERVICE);
		return PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
	}
	
	private void registerRestartAlarm(PendingIntent restart) {
		AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarm.cancel(restart);
		alarm.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + PERIOD_TIME, PERIOD_TIME, restart);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (Authenticator.isLoggedIn()) {
			validateToken();
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void validateToken() {
		try {
			User user = Authenticator.getUser();
			JSONObject message = new JSONObject();
			message.put("memberId", user.getMelonId());
			message.put("token", Authenticator.getAccessToken());
			
			JSONObjectRequest request = new JSONObjectRequest(
					"melon/check/token", null, message,
					new JSONObjectSuccessListener(this, "onValidationSuccess"), 
					new JSONErrorListener(this, "onValidationError"));
			
			App app = (App) getApplicationContext();
			app.addShortLivedRequest(this, request);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onValidationSuccess(JSONObject response) {
		try {
			MelonResponseHooker.hook(this, null, response);
			
			Logger.log("token validation success");
		} catch (MelonResponseException e) {
			e.printStackTrace();
			onValidationError();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onValidationError() {
		new Authenticator().logout(this);
		
		Logger.log("token validation error");
	}

}
