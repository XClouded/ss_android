package com.myandb.singsong.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.google.gson.Gson;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.audio.Encoder;
import com.myandb.singsong.audio.PcmPlayer;
import com.myandb.singsong.audio.Recorder;
import com.myandb.singsong.audio.Track;
import com.myandb.singsong.event.OnCompleteListener;
import com.myandb.singsong.event.OnProgressListener;
import com.myandb.singsong.fragment.KaraokeFragment;
import com.myandb.singsong.model.Model;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.UploadManager;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.StringFormatter;
import com.myandb.singsong.util.Utility;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Feed;
import com.sromku.simple.fb.listeners.OnPublishListener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
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
	public static final String EXTRA_RECORD_VOLUME = "record_volume";
	public static final String EXTRA_FACEBOOK_POSTING = "facebook_posting";
	public static final String EXTRA_SAMPLE_SKIP_SECOND = "sample_skip_second";
	public static final int REQUEST_CODE = 10;
	
	private static boolean isRunning;
	private int lyricPart;
	private int musicId;
	private int parentSongId;
	private int creatorId;
	private int imageId;
	private boolean isFacebookPosting;
	private boolean teamCollabo;
	private float sampleSkipSecond;
	private String message;
	private String audioName;
	private String songAudioName;
	private File songOggFile;
	private File sampleOggFile;
	private NotificationManager manager;
	private PendingIntent pendingIntent;
	private Notification noti;
	private List<Track> tracks;

	@Override
	public void onCreate() {
		super.onCreate();
		
		manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		Intent intent = new Intent(this, RootActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, intent, 0);
		
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
		final float recordVolume = intent.getFloatExtra(EXTRA_RECORD_VOLUME, 1f);
		
		teamCollabo = intent.getBooleanExtra(KaraokeFragment.EXTRA_TEAM_COLLABO, false);
		creatorId = intent.getIntExtra(EXTRA_CREATOR_ID, 0);
		musicId = intent.getIntExtra(EXTRA_MUSIC_ID, 0);
		parentSongId = intent.getIntExtra(EXTRA_PARENT_SONG_ID, 0);
		lyricPart = intent.getIntExtra(EXTRA_LYRIC_PART, Music.PART_MALE);
		imageId = intent.getIntExtra(EXTRA_IMAGE_ID, 0);
		message = intent.getStringExtra(EXTRA_SONG_MESSAGE);
		isFacebookPosting = intent.getBooleanExtra(EXTRA_FACEBOOK_POSTING, false);
		sampleSkipSecond = intent.getFloatExtra(EXTRA_SAMPLE_SKIP_SECOND, 0f);
		audioName = generateSongName(creatorId, musicId);
		
		try {
			tracks = new ArrayList<Track>();
			Track recordTrack = new Track(recordPcmFile, Recorder.CHANNELS);
			recordTrack.addOffsetFrame(recordOffset);
			recordTrack.setVolume(recordVolume);
			tracks.add(recordTrack);
			if (headsetPlugged) {
				Track musicTrack = new Track(musicPcmFile, PcmPlayer.CHANNELS);
				musicTrack.addOffsetFrame(musicOffset);
				tracks.add(musicTrack);
			}
			
			startNormalEncoding();
		} catch (Exception e) {
			e.printStackTrace();
			errorNotification("노래 인코딩에 실패하였습니다.");
		}
		
		return START_NOT_STICKY;
	}
	
	private String generateSongName(int userId, int musicId) {
		String keyStr = String.valueOf(userId);
		keyStr += "_";
		keyStr += String.valueOf(musicId);
		keyStr += "_";
		keyStr += String.valueOf(System.currentTimeMillis());
		return keyStr;
	}
	
	private void startNormalEncoding() {
		songOggFile = new File(getFilesDir(), "song.ogg");
		
		Encoder encoder = new Encoder();
		encoder.setOutputFileName(songOggFile.getAbsolutePath());
		encoder.disableSampleMode();
		encoder.setOnProgressListener(new OnProgressListener() {
			
			@Override
			public void onProgress(Integer progress) {
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
		encoder.execute(tracks.toArray(new Track[tracks.size()]));
	}
	
	private void startSampleEncoding() {
		sampleOggFile = new File(getFilesDir(), "sample.ogg");
		
		Encoder encoder = new Encoder();
		encoder.setOutputFileName(sampleOggFile.getAbsolutePath());
		encoder.enableSampleMode((int) (44100 * sampleSkipSecond));
		encoder.setOnProgressListener(new OnProgressListener() {
			
			@Override
			public void onProgress(Integer progress) {
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
		encoder.execute(tracks.toArray(new Track[tracks.size()]));
	}
	
	private void uploadNormalSongFile() {
		updateNotification(70, "업로드 중입니다.");
		
		songAudioName = audioName + Model.SUFFIX_OGG;
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
		
		String sampleAudioName = audioName + ".sample" + Model.SUFFIX_OGG;
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
	
	public void onFileUploadComplete() {
		updateNotification(90, "노래정보를 갱신하고 있습니다.");

		final int duration = StringFormatter.getDuration(songOggFile);
		
		try {
			JSONObject data = new JSONObject();
			data.put("user_id", creatorId);
			data.put("music_id", musicId);
			data.put("file", songAudioName);
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
					"songs", null, data,
					new Response.Listener<JSONObject>() {
						
						@Override
						public void onResponse(JSONObject response) {
							try {
								Gson gson = Utility.getGsonInstance();
								Song song = gson.fromJson(response.toString(), Song.class);
								if (isFacebookPosting) {
									postOnFacebook(song);
								}
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								successNotification();
							}
						}
					}, 
					new ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							errorNotification("노래정보 갱신에 실패하였습니다.");
						}
					}
			);
			
			((App) getApplicationContext()).addLongLivedRequest(request);
		} catch (JSONException e) {
			e.printStackTrace();
			errorNotification("업로드에 실패하였습니다.");
		}
	}
	
	private void submitNotification() {
		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_progressbar);
		contentView.setImageViewResource(R.id.iv_icon, R.drawable.ic_launcher);
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
	
	private void postOnFacebook(Song song) {
		final Feed feed = new Feed.Builder()
			.setName(song.getMusic().getTitle())
			.setDescription(song.getMusic().getSingerName())
			.setCaption(song.getMessage())
			.setPicture(song.getMusic().getAlbumPhotoUrl())
			.setLink(new UrlBuilder().s("w").s("p").s(song.getId()).toString())
			.build();
		
		final SimpleFacebook simpleFacebook = SimpleFacebook.getInstance();
		if (simpleFacebook != null) {
			simpleFacebook.publish(feed, new OnPublishListener() {
			});
		}
	}
	
	public static boolean isServiceRunning() {
		return isRunning;
	}

}
