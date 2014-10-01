package com.myandb.singsong;

import java.io.File;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.gson.Gson;
import com.myandb.singsong.activity.MainActivity;
import com.myandb.singsong.activity.RecordMainActivity;
import com.myandb.singsong.event.OnCompleteListener;
import com.myandb.singsong.file.Storage;
import com.myandb.singsong.model.Activity;
import com.myandb.singsong.model.Notification;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.OAuthJustRequest;
import com.myandb.singsong.net.DownloadManager;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Auth;
import com.myandb.singsong.util.ImageHelper;
import com.myandb.singsong.util.ImageHelper.BitmapBuilder;
import com.myandb.singsong.util.Utility;

/**
 * GCM server에 단말 등록, 해제, 오류 시 callback 실행 되는 곳
 * push 메세지 왔을 때 처리해줌
 * 
 * @author mhdjang
 *
 */
public class GCMIntentService extends GCMBaseIntentService {
	
	public static final String PROJECT_ID = "1079233079703";
	
	private Handler handler;
	private File tempFile;
	
	public GCMIntentService() {
		super(PROJECT_ID);
		
		try {
			tempFile = File.createTempFile("_user_photo_", ".tmp");
		} catch (IOException e) {
			// permission denied
			tempFile = null;
		}
	}
	
	@Override
	protected void onMessage(Context context, Intent intent) {
		Storage storage = new Storage();
		storage.arriveNewPush();
		
		if (intent != null && storage.isAllowPush()) {
			Gson gson = Utility.getGsonInstance();
			Activity activity = gson.fromJson(intent.getStringExtra("activity"), Activity.class);
			
			if (activity != null && Auth.isLoggedIn()) {
				Notification notification = new Notification(activity);
				User creator = activity.getCreator();
				User currentUser = Auth.getUser();
				String message = notification.getContent(currentUser);
				
				if (creator.hasPhoto()) {
					downloadPhoto(creator, message);
				} else {
					BitmapBuilder builder = new BitmapBuilder();
					Bitmap bitmap = builder.setSource(getResources(), R.drawable.user_default)
							.enableCrop(true)
							.build();
					
					if (bitmap != null) {
						submitNotification(bitmap, creator, message);
					}
				}
				
				if (!RecordMainActivity.isActivityRunning()) {
					showToast(creator, message);
				}
			}
		}
	}
	
	private void showToast(final User user, final String message) {
		if (handler == null) {
			handler = new Handler();
		}
		
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				PushToast pushToast = new PushToast(getApplicationContext(), user, message);
				pushToast.show();
			}
		});
	}
	
	private void downloadPhoto(final User user, final String message) {
		DownloadManager networkFile = new DownloadManager();
		networkFile.start(user.getPhotoUrl(), tempFile, new OnCompleteListener() {
			
			@Override
			public void done(Exception e) {
				if (e == null) {
					BitmapBuilder builder = new BitmapBuilder();
					Bitmap bitmap = builder.setSource(tempFile)
										   .enableCrop(true)
										   .build();
					
					if (bitmap != null) {
						submitNotification(bitmap, user, message);
					}
				} else {
					e.printStackTrace();
				}
			}
			
		});
	}
	
	private void submitNotification(Bitmap largeIcon, User user, String message) {
		Intent intent = new Intent("com.myandb.singsong.activity.MainActivity");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(MainActivity.INTENT_PAGE_REQUEST, App.REQUEST_NOTIFICATION_ACTIVITY);
		
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setSmallIcon(R.drawable.ic_launcher)
			   .setContentTitle(user.getNickname())
			   .setContentText(message)
			   .setAutoCancel(true)
			   .setLargeIcon(largeIcon)
			   .setTicker(message)
			   .setContentIntent(pendingIntent);
		
		if (!RecordMainActivity.isActivityRunning()) {
			builder.setVibrate(new long[] { 300, 700 });
		}
		
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.notify(App.NOTI_ID_PUSH, builder.build());
	}
	
	private static class PushToast extends Toast {

		public PushToast(Context context, User user, String message) {
			super(context);
			
			View view = View.inflate(context, R.layout.toast_push, null);
			
			ImageView ivUserPhoto = (ImageView) view.findViewById(R.id.iv_user_photo);
			TextView tvUserName = (TextView) view.findViewById(R.id.tv_user_name);
			TextView tvMessage = (TextView) view.findViewById(R.id.tv_message);
			
			ImageHelper.displayPhoto(user, ivUserPhoto);
			tvUserName.setText(user.getNickname());
			tvMessage.setText(message);
			
			setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL, 0, 100);
			setDuration(Toast.LENGTH_LONG);
			setView(view);
		}
		
	}

	@Override
	protected void onError(Context context, String errorId) {}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		updateRegistrationId(registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		updateRegistrationId("");
	}
	
	private void updateRegistrationId(String registrationId) {
		if (Auth.isLoggedIn()) {
			try {
				UrlBuilder urlBuilder = UrlBuilder.getInstance();
				String url = urlBuilder.l("users").build();
				
				JSONObject message = new JSONObject();
				message.put("push_id", registrationId);
				
				OAuthJustRequest request = new OAuthJustRequest(Method.PUT, url, message);
				RequestQueue queue = ((App) getApplicationContext()).getQueueInstance();
				queue.add(request);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
