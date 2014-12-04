package com.myandb.singsong.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.audio.Encoder;
import com.myandb.singsong.audio.PcmPlayer;
import com.myandb.singsong.audio.Recorder;
import com.myandb.singsong.audio.Track;
import com.myandb.singsong.event.OnCompleteListener;
import com.myandb.singsong.event.OnProgressListener;
import com.myandb.singsong.image.BitmapBuilder;
import com.myandb.singsong.model.Model;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.UploadManager;
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
	
	public static final String EXTRA_HEADSET_PLUGGED = "headset_plugged";
	public static final String EXTRA_RECORD_PCM_FILE_PATH = "record_pcm_file_name";
	public static final String EXTRA_RECORD_OFFSET = "record_offset";
	public static final String EXTRA_MUSIC_PCM_FILE_PATH = "music_pcm_file_name";
	public static final String EXTRA_MUSIC_OFFSET = "music_offset";
	public static final String EXTRA_CREATOR_ID = "creator_id";
	public static final String EXTRA_MUSIC_ID = "music_id";
	public static final String EXTRA_PARENT_SONG_ID = "parent_song_id";
	public static final String EXTRA_LYRIC_PART = "lyric_part";
	public static final String EXTRA_IMAGE_ID = "image_id";
	public static final String EXTRA_SONG_MESSAGE = "song_message";
	public static final int REQUEST_CODE = 10;
	
	private static boolean isRunning;
	private int lyricPart;
	private int musicId;
	private int parentSongId;
	private int creatorId;
	private int imageId;
	private String message;
	private String songAudioName;
	private String sampleAudioName;
	private File songOggFile;
	private File sampleOggFile;
	private NotificationManager manager;
	private PendingIntent pendingIntent;
	private Notification noti;
	private RequestQueue requestQueue;
	private List<Track> tracks;

	@Override
	public void onCreate() {
		super.onCreate();
		
		manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		Intent intent = new Intent(this, RootActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
		
		final boolean headsetPlugged = intent.getBooleanExtra(EXTRA_HEADSET_PLUGGED, false);
		final int recordOffset = intent.getIntExtra(EXTRA_RECORD_OFFSET, 0);
		final int musicOffset = intent.getIntExtra(EXTRA_MUSIC_OFFSET, 0);
		final String recordPcmFilePath = intent.getStringExtra(EXTRA_RECORD_PCM_FILE_PATH);
		final String musicPcmFilePath = intent.getStringExtra(EXTRA_MUSIC_PCM_FILE_PATH);
		final File recordPcmFile = new File(recordPcmFilePath);
		final File musicPcmFile = new File(musicPcmFilePath);
		
		creatorId = intent.getIntExtra(EXTRA_CREATOR_ID, 0);
		musicId = intent.getIntExtra(EXTRA_MUSIC_ID, 0);
		parentSongId = intent.getIntExtra(EXTRA_PARENT_SONG_ID, 0);
		lyricPart = intent.getIntExtra(EXTRA_LYRIC_PART, Music.PART_MALE);
		imageId = intent.getIntExtra(EXTRA_IMAGE_ID, 0);
		message = intent.getStringExtra(EXTRA_SONG_MESSAGE);
		
		try {
			tracks = new ArrayList<Track>();
			Track recordTrack = new Track(recordPcmFile, Recorder.CHANNELS);
			recordTrack.addOffsetFrame(recordOffset);
			tracks.add(recordTrack);
			if (headsetPlugged) {
				Track musicTrack = new Track(musicPcmFile, PcmPlayer.CHANNELS);
				musicTrack.addOffsetFrame(musicOffset);
				tracks.add(musicTrack);
			}
			
			startNormalEncoding();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return START_NOT_STICKY;
	}
	
	private void startNormalEncoding() {
		songOggFile = new File(getFilesDir(), "song.ogg");
		
		Encoder encoder = new Encoder();
		encoder.setOutputFileName(songOggFile.getAbsolutePath());
		encoder.disableSampleMode();
		encoder.setOnProgressListener(new OnProgressListener() {
			
			@Override
			public void done(Integer progress) {
				final int maxProgress = 50;
				int convertedProgress = maxProgress * progress / 100; 
				updateNotification(convertedProgress, "진행중 ...");
			}
		});
		encoder.setOnCompleteListener(new OnCompleteListener() {
			
			@Override
			public void done(Exception e) {
				if (e == null) {
					startSampleEncoding();
				} else {
					errorNotification("노래 인코딩에 실패하였습니다.");
				}
			}
		});
		encoder.execute((Track[]) tracks.toArray());
	}
	
	private void startSampleEncoding() {
		sampleOggFile = new File(getFilesDir(), "sample.ogg");
		
		Encoder encoder = new Encoder();
		encoder.setOutputFileName(sampleOggFile.getAbsolutePath());
		encoder.enableSampleMode(44100 * 10);
		encoder.setOnProgressListener(new OnProgressListener() {
			
			@Override
			public void done(Integer progress) {
				final int maxProgress = 20;
				int convertedProgress = 50 + maxProgress * progress / 100; 
				updateNotification(convertedProgress, "진행중 ...");
			}
		});
		encoder.setOnCompleteListener(new OnCompleteListener() {
			
			@Override
			public void done(Exception e) {
				if (e == null) {
					uploadNormalSongFile();
				} else {
					errorNotification("노래 인코딩에 실패하였습니다.");
				}
			}
		});
	}
	
	private void uploadNormalSongFile() {
		updateNotification(70, "업로드 중입니다.");
		
		songAudioName = generateSongName(creatorId, musicId);
		songAudioName += Model.SUFFIX_OGG;
		UploadManager manager = new UploadManager();
		try {
			manager.start(
					SongUploadService.this, songOggFile,
					"song", songAudioName, "application/ogg",
					new OnCompleteListener() {
						
						@Override
						public void done(Exception e) {
							if (e == null) {
								uploadSampleSongFile();
							} else {
								errorNotification("파일 업로드에 실패하였습니다.");
							}
						}
					}
			);
		} catch (Exception e) {
			errorNotification("파일 업로드에 실패하였습니다.");
		}
	}
	
	private void uploadSampleSongFile() {
		updateNotification(80, "업로드 중입니다.");
		
		sampleAudioName = songAudioName + ".sample" + Model.SUFFIX_OGG;
		UploadManager manager = new UploadManager();
		try {
			manager.start(
					SongUploadService.this, sampleOggFile,
					"song", sampleAudioName, "application/ogg",
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
		} catch (Exception e) {
			errorNotification("파일 업로드에 실패하였습니다.");
		}
	}
	
	private String generateSongName(int userId, int musicId) {
		String keyStr = String.valueOf(userId);
		keyStr += "_";
		keyStr += String.valueOf(musicId);
		keyStr += "_";
		keyStr += String.valueOf(System.currentTimeMillis());
		return keyStr;
	}
	
	public void onFileUploadComplete() {
		updateNotification(90, "노래정보를 갱신하고 있습니다.");

		final int duration = StringFormatter.getDuration(songOggFile);
		
		try {
			JSONObject data = new JSONObject();
			data.put("user_id", creatorId);
			data.put("music_id", musicId);
			data.put("file", songAudioName);
			data.put("sample", sampleAudioName);
			data.put("duration", duration);
			data.put("lyric_part", lyricPart);
			data.put("message", message);
			
			if (parentSongId > 0) {
				data.put("song_id", parentSongId);
			}
			
			if (imageId > 0) {
				data.put("image_id", imageId);
			}
			
			JSONObjectRequest request = new JSONObjectRequest(
					"songs", data,
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
