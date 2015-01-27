package com.myandb.singsong;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.gson.Gson;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.UpActivity;
import com.myandb.singsong.fragment.KaraokeFragment;
import com.myandb.singsong.fragment.NotificationFragment;
import com.myandb.singsong.image.BitmapBuilder;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.UserActivity;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.DownloadManager.OnDownloadListener;
import com.myandb.singsong.net.JustRequest;
import com.myandb.singsong.net.DownloadManager;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.util.Utility;

public class GCMIntentService extends GCMBaseIntentService {
	
	public static final String PROJECT_ID = "1079233079703";
	
	private volatile static Toast previousToast = null;
	
	private Handler handler = new Handler();
	
	public GCMIntentService() {
		super(PROJECT_ID);
	}
	
	@Override
	protected void onMessage(Context context, Intent intent) {
		if (Authenticator.isLoggedIn()) {
			increaseNotificationCount();
			if (isEnabledNotification()) {
				try {
					notifyUser(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private boolean increaseNotificationCount() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		final String key = getString(R.string.key_notification_count);
		final int currentCount = getCurrentNotificationCount(preferences);
		return preferences.edit().putInt(key, (currentCount + 1)).commit();
	}
	
	private int getCurrentNotificationCount(SharedPreferences preferences) {
		final int defaultValue = 0;
		try {
			final String key = getString(R.string.key_notification_count);
			return preferences.getInt(key, defaultValue);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return defaultValue;
	}
	
	private boolean isEnabledNotification() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		final boolean defaultValue = true;
		try {
			final String key = getString(R.string.key_notification);
			return preferences.getBoolean(key, defaultValue);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return defaultValue;
	}
	
	private void notifyUser(Intent intent) {
		UserActivity activity = getUserActivityFromIntent(intent);
		prepareAndSubmitNotification(activity);
		showToast(activity);
	}
	
	private UserActivity getUserActivityFromIntent(Intent intent) {
		Gson gson = Utility.getGsonInstance();
		String activityJson = intent.getStringExtra("activity");
		return gson.fromJson(activityJson, UserActivity.class);
	}
	
	private void prepareAndSubmitNotification(UserActivity activity) {
		final User creator = activity.getCreator();
		final String message = activity.getMessage();
		if (creator.hasPhoto()) {
			DownloadManager manager = new DownloadManager();
			manager.start(creator.getPhotoUrl(), new OnDownloadListener() {

				@Override
				public void onComplete(File file) {
					super.onComplete(file);
					Bitmap bitmap = getIconBitmap(file);
					submitNotification(bitmap, creator, message);
				}
				
			});
		} else {
			Bitmap bitmap = getIconBitmap(R.drawable.user_character);
			submitNotification(bitmap, creator, message);
		}
	}
	
	private Bitmap getIconBitmap(int drawableResId) {
		BitmapBuilder builder = new BitmapBuilder();
		int size = getNotificationIconSize();
		return builder.setSource(getResources(), drawableResId)
				.setOutputSize(size)
				.enableCrop(true)
				.build();
	}
	
	private Bitmap getIconBitmap(File file) {
		BitmapBuilder builder = new BitmapBuilder();
		int size = getNotificationIconSize();
		return builder.setSource(file)
				.setOutputSize(size)
				.enableCrop(true)
				.build();
	}
	
	private int getNotificationIconSize() {
		int width = getNotificationLargeIconWidth(getResources());
		int height = getNotificationLargeIconHeight(getResources());
		return Math.max(width, height);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private int getNotificationLargeIconWidth(Resources res) {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
			return (int) res.getDimensionPixelSize(android.R.dimen.notification_large_icon_width);
		}
		return (int) res.getDimensionPixelSize(R.dimen.notification_icon_width);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private int getNotificationLargeIconHeight(Resources res) {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
			return (int) res.getDimensionPixelSize(android.R.dimen.notification_large_icon_height);
		}
		return (int) res.getDimensionPixelSize(R.dimen.notification_icon_height);
	}
	
	private void showToast(final UserActivity activity) {
		if (!KaraokeFragment.isRunning()) {
			return;
		}
		
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				if (previousToast != null) {
					previousToast.cancel();
				}
				
				PushToast pushToast = new PushToast(getApplicationContext(), activity);
				previousToast = pushToast;
				pushToast.show();
			}
		});
	}
	
	private void submitNotification(Bitmap largeIcon, User user, String message) {
		Intent intent = new Intent(this, UpActivity.class);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, NotificationFragment.class.getName());
		
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setSmallIcon(R.drawable.ic_launcher)
			   .setContentTitle(user.getNickname())
			   .setContentText(message)
			   .setAutoCancel(true)
			   .setLargeIcon(largeIcon)
			   .setTicker(message)
			   .setContentIntent(pendingIntent);
		
		if (!KaraokeFragment.isRunning()) {
			builder.setVibrate(new long[] { 300, 700 });
		}
		
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.notify(App.NOTI_ID_GCM, builder.build());
	}
	
	private static class PushToast extends Toast {

		public PushToast(Context context, UserActivity activity) {
			super(context);
			
			View view = View.inflate(context, R.layout.toast_push, null);
			
			ImageView ivUserPhoto = (ImageView) view.findViewById(R.id.iv_user_photo);
			TextView tvMessage = (TextView) view.findViewById(R.id.tv_message);
			
			ImageHelper.displayPhoto(activity.getCreator(), ivUserPhoto);
			tvMessage.setText(activity.getMessage());
			
			setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL, 0, (int) pixelFromDp(context, 100));
			setDuration(Toast.LENGTH_LONG);
			setView(view);
		}
		
		private float pixelFromDp(Context context, float dp) {
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
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
		if (Authenticator.isLoggedIn()) {
			try {
				final JSONObject message = new JSONObject();
				message.put("push_id", registrationId);
				
				JustRequest request = new JustRequest(Method.PUT, "users", null, message);
				((App) getApplicationContext()).addLongLivedRequest(request);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
