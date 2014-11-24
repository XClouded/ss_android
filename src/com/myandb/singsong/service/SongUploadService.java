package com.myandb.singsong.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.MainActivity;
import com.myandb.singsong.audio.Encoder;
import com.myandb.singsong.event.OnCompleteListener;
import com.myandb.singsong.event.OnProgressListener;
import com.myandb.singsong.image.BitmapBuilder;
import com.myandb.singsong.model.Model;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.net.OAuthJsonObjectRequest;
import com.myandb.singsong.net.UploadManager;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.StringFormatter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

public class SongUploadService extends Service {
	
	public static final int REQUEST_CODE = 10;
	
	public static final String INTENT_CREATOR_ID = "_creator_";
	public static final String INTENT_MUSIC_ID = "_mixed_with_";
	public static final String INTENT_PARENT_SONG_ID = "_parent_song_";
	public static final String INTENT_HEADSET_PLUGGED = "_record_mode_";
	public static final String EXTRA_MUSIC_OFFSET = "music_offset";
	public static final String EXTRA_RECORD_OFFSET = "record_offset";
	public static final String INTENT_LYRIC_PART = "_lyric_part_";
	public static final String EXTRA_IMAGE_ID = "_image_";
	public static final String EXTRA_SONG_MESSAGE = "_message_";
	
	private static boolean isRunning;
	private int lyricPart;
	private int thisMusicId;
	private int parentSongId;
	private int creatorId;
	private int imageId;
	private String message;
	private String audioFileName;
	private NotificationManager manager;
	private PendingIntent pendingIntent;
	private Notification noti;
	private RequestQueue requestQueue;

	@Override
	public void onCreate() {
		super.onCreate();
		
		manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		Intent intent = new Intent("com.myandb.singsong.activity.MainActivity");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(MainActivity.INTENT_PAGE_REQUEST, REQUEST_CODE);
		pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, intent, 0);
		
		requestQueue = ((App) getApplicationContext()).getQueueInstance();
		
		isRunning = false;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		isRunning = true;
		
		submitNotification();
		
		imageId = intent.getIntExtra(EXTRA_IMAGE_ID, -1);
		message = intent.getStringExtra(EXTRA_SONG_MESSAGE);
		lyricPart = intent.getIntExtra(INTENT_LYRIC_PART, Music.PART_MALE);
		thisMusicId = intent.getIntExtra(INTENT_MUSIC_ID, -1);
		creatorId = intent.getIntExtra(INTENT_CREATOR_ID, -1);
		parentSongId = intent.getIntExtra(INTENT_PARENT_SONG_ID, -1);
		
		final boolean headsetPlugged = intent.getBooleanExtra(INTENT_HEADSET_PLUGGED, false);
		final float syncAmount = intent.getFloatExtra(EXTRA_MUSIC_OFFSET, 0.0f);
		
		startEncoding(headsetPlugged, syncAmount);
		
		return START_NOT_STICKY;
	}
	
	private void startEncoding(boolean headsetPlugged, float syncAmount) {
		updateNotification(0, "노래를 압축하고 있습니다.");
		
		audioFileName = generateSongName(creatorId, thisMusicId);
		
		/*
		Encoder encoder = new Encoder();
		encoder.setCallback(new OnCompleteListener() {
			
			@Override
			public void done(Exception e) {
				try {
					updateNotification(90, "노래를 서버에 업로드 하고 있습니다.");
					
					audioFileName = generateSongName(creatorId, thisMusicId);
					UploadManager manager = new UploadManager();
					manager.start(
							SongUploadService.this, FileManager.getSecure(FileManager.SONG_OGG),
							"song", audioFileName, "application/ogg",
							new OnCompleteListener() {
								
								@Override
								public void done(Exception e) {
									if (e == null) {
										onFileUploadComplete();
									} else {
										errorNotification("파일 업로드에 실패하였습니다.");
									}
								}
							}
					);
				} catch (Exception e1) {
					errorNotification("노래 인코딩에 실패하였습니다.");
				}		
				
			}
		}, new OnProgressListener() {
			
			@Override
			public void done(Integer progress) {
				updateNotification(progress, "진행중 ...");
			}
		});
		
		encoder.start(syncAmount, headsetPlugged);
		*/
	}
	
	private String generateSongName(int key, int key2) {
		String keyStr = String.valueOf(key);
		keyStr += "_";
		keyStr += String.valueOf(key2);
		keyStr += "_";
		keyStr += String.valueOf(System.currentTimeMillis());
		keyStr += Model.SUFFIX_OGG;
		
		return keyStr;
	}
	
	public void onFileUploadComplete() {
		updateNotification(95, "노래정보를 갱신하고 있습니다.");

		final int duration = 0;/*(int) StringFormatter.getDuration(FileManager.getSecure(FileManager.SONG_OGG));*/
		
		try {
			JSONObject data = new JSONObject();
			data.put("user_id", creatorId);
			data.put("music_id", thisMusicId);
			data.put("file", audioFileName);
			data.put("duration", duration);
			data.put("lyric_part", lyricPart);
			data.put("message", message);
			
			if (parentSongId > 0) {
				data.put("song_id", parentSongId);
			}
			
			if (imageId > 0) {
				data.put("image_id", imageId);
			}
			
			UrlBuilder urlBuilder = new UrlBuilder();
			String url = urlBuilder.s("songs").toString();
			OAuthJsonObjectRequest request = new OAuthJsonObjectRequest(
					Method.POST, url, data,
					new Response.Listener<JSONObject>() {
						
						@Override
						public void onResponse(JSONObject response) {
							successNotification();
						}
					}, 
					new ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							errorNotification("노래정보 갱신에 실패하였습니다.");
						}
					}
			);
			
			requestQueue.add(request);
		} catch (JSONException e) {
			e.printStackTrace();
			errorNotification("업로드에 실패하였습니다.");
		}
	}
	
	private void submitNotification() {
		BitmapBuilder bitmapBuilder = new BitmapBuilder();
		Bitmap bitmap = null;
		
		/*
		try {
			if (FileManager.isExist(FileManager.USER_PHOTO)) {
				bitmap = bitmapBuilder.setSource(FileManager.get(FileManager.USER_PHOTO))
									  .enableCrop(true)
									  .build();
			} else {
				bitmap = bitmapBuilder.setSource(getResources(), R.drawable.user_default)
									  .enableCrop(true)
									  .build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		
		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_progressbar);
		contentView.setImageViewBitmap(R.id.iv_icon, bitmap);
		contentView.setTextViewText(R.id.tv_title, "서버에서 정보를 받아오고 있습니다.");
		contentView.setProgressBar(R.id.pb_status, 100, 0, false);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setAutoCancel(true)
			   .setContentIntent(pendingIntent)
			   .setSmallIcon(R.drawable.ic_upload_list)
			   .setTicker("노래를 업로드합니다.");
		
		if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
			noti = builder.setContent(contentView).build();
		} else {
			noti = builder.build();
			noti.contentView = contentView;
		}
		
		noti.icon = R.drawable.ic_upload_list;
		noti.iconLevel = 0;
		
		startForeground(App.NOTI_ID_SONG_UPLOAD, noti);
	}
	
	private void errorNotification(String str) {
		stopForeground(true);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setSmallIcon(R.drawable.ic_error)
			   .setContentTitle("에러가 발생하였습니다.")
			   .setContentText(str)
			   .setAutoCancel(true)
			   .setTicker("업로드에 실패하였습니다.")
			   .setContentIntent(pendingIntent);
		
		manager.notify(App.NOTI_ID_SONG_UPLOAD, builder.build());
		
		isRunning = false;
	}
	
	private void updateNotification(int progress, String str) {
		noti.contentView.setTextViewText(R.id.tv_title, String.valueOf(progress) + "% " + str);
		noti.contentView.setProgressBar(R.id.pb_status, 100, progress, false);
		
		startForeground(App.NOTI_ID_SONG_UPLOAD, noti);
	}
	
	private void successNotification() {
		stopForeground(true);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setSmallIcon(R.drawable.ic_note_symbol)
			   .setContentTitle("노래를 업로드하였습니다.")
			   .setContentText("지금 바로 들어보세요!")
			   .setAutoCancel(true)
			   .setTicker("업로드를 완료하였습니다.")
			   .setContentIntent(pendingIntent)
			   .setVibrate( new long[] { 300, 700 } )
			   .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		
		manager.notify(App.NOTI_ID_SONG_UPLOAD, builder.build());
		
		isRunning = false;
	}
	
	public static boolean isServiceRunning() {
		return isRunning;
	}

}
