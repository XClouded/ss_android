package com.myandb.singsong.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Request.Method;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.audio.ISimplePlayCallback;
import com.myandb.singsong.audio.Player;
import com.myandb.singsong.audio.Recorder;
import com.myandb.singsong.dialog.ImageSelectDialog;
import com.myandb.singsong.event.OnCompleteListener;
import com.myandb.singsong.event.OnVolleyWeakError;
import com.myandb.singsong.event.OnVolleyWeakResponse;
import com.myandb.singsong.event.WeakRunnable;
import com.myandb.singsong.file.FileManager;
import com.myandb.singsong.model.Image;
import com.myandb.singsong.model.Model;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.OAuthJsonObjectRequest;
import com.myandb.singsong.net.UploadManager;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.service.SongUploadService;
import com.myandb.singsong.util.ImageHelper;
import com.myandb.singsong.util.ResizeAsyncTask;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class RecordSettingActivity extends OldBaseActivity {
	
	public static final int R_CODE_PHOTO_PICKER = 100;
	
	private boolean headsetPlugged;
	private View vRestart;
	private View vUpload;
	private View vExit;
	private View vMixer;
	private ImageView ivPlayControl;
	private ImageView ivSyncBack;
	private ImageView ivSyncforward;
	private ImageView ivSongImage;
	private TextView tvSyncValue;
	private SeekBar sbPlay;
	private EditText etSongMessage;
	private Button btnOtherImages;
	private Button btnDeletePhoto;
	private Player player;
	private Handler handler;
	private Image image;
	private String imageName;
	private boolean newImageAdded;
	private ImageSelectDialog dialog;
	private Uri tempUri;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_record_setting);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		handler = new Handler();
		
		player = new Player();
		player.setCallback(new PlayerStatusCallback(this));
		
		dialog = new ImageSelectDialog(this);
		
		ivPlayControl = (ImageView) findViewById(R.id.iv_play_control);
		ivSyncBack = (ImageView) findViewById(R.id.iv_sync_back);
		ivSyncforward = (ImageView) findViewById(R.id.iv_sync_forward);
		ivSongImage = (ImageView) findViewById(R.id.iv_song_image);
		
		tvSyncValue = (TextView) findViewById(R.id.tv_sync_value);
		sbPlay = (SeekBar) findViewById(R.id.sb_play);
		etSongMessage = (EditText) findViewById(R.id.et_song_message);
		btnOtherImages = (Button) findViewById(R.id.btn_other_images);
		btnDeletePhoto = (Button) findViewById(R.id.btn_delete_image);
		
		vRestart = findViewById(R.id.ll_restart);
		vUpload = findViewById(R.id.ll_upload);
		vExit = findViewById(R.id.ll_exit);
		vMixer = findViewById(R.id.ll_mixer);
		
		ivPlayControl.setOnClickListener(playClickListener);
		vUpload.setOnClickListener(uploadClickListener);
		vRestart.setOnClickListener(onClickListener);
		vExit.setOnClickListener(onClickListener);
		ivSyncBack.setOnClickListener(onClickListener);
		ivSyncforward.setOnClickListener(onClickListener);
		btnDeletePhoto.setOnClickListener(onClickListener);
		ivSongImage.setOnClickListener(onClickListener);
		btnOtherImages.setOnClickListener(onClickListener);

		headsetPlugged = getIntent().getBooleanExtra(SongUploadService.INTENT_HEADSET_PLUGGED, false);
		if (headsetPlugged) {
			vMixer.setVisibility(View.VISIBLE);
		} else {
			vMixer.setVisibility(View.GONE);
		}
		
		tvSyncValue.setText("0.0");
		sbPlay.setMax(player.getDuration());
		sbPlay.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				seekChange(v);
				
				return false;
			}
		});
		ivPlayControl.performClick();
		
		try {
			File tempFile = FileManager.get(FileManager.TEMP_2);
			if (!tempFile.exists()) {
				tempFile.createNewFile();
			}
			
			tempUri = Uri.fromFile(tempFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private OnClickListener uploadClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			vUpload.setEnabled(false);
			
			if (Recorder.isValidRecordingTime(FileManager.getSecure(FileManager.VOICE_RAW))) {
				if (newImageAdded) {
					UploadManager manager = new UploadManager();
					try {
						imageName = generateFileName();
						
						manager.start(
								RecordSettingActivity.this, FileManager.get(FileManager.TEMP),
								"image", imageName, "image/jpeg",
								imageUploadCompleteListener
						);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				} else {
					finishWithSongUploadResult();
				}
			} else {
				Toast.makeText(RecordSettingActivity.this, getString(R.string.t_song_length_policy), Toast.LENGTH_SHORT).show();
			}
		}
		
		private String generateFileName() {
			String result = "";
			
			User currentUser = Authenticator.getUser();
			result += currentUser.getUsername();
			result += "_";
			result += String.valueOf(System.currentTimeMillis());
			result += Model.SUFFIX_JPG;
			
			return result;
		}
	};
	
	private OnCompleteListener imageUploadCompleteListener = new OnCompleteListener() {
		
		@Override
		public void done(Exception e) {
			if (this != null) {
				if (e == null) {
					try {
						JSONObject message = new JSONObject();
						message.put("url", Model.STORAGE_HOST + Model.STORAGE_IMAGE + imageName);
						
						UrlBuilder urlBuilder = new UrlBuilder();
						String url = urlBuilder.s("images").toString();
						OAuthJsonObjectRequest request = new OAuthJsonObjectRequest(
								Method.POST, url, message,
								new OnVolleyWeakResponse<RecordSettingActivity, JSONObject>(RecordSettingActivity.this, "onUploadSuccess", Image.class),
								new OnVolleyWeakError<RecordSettingActivity>(RecordSettingActivity.this, "onUploadError")
						);
						
						RequestQueue queue = ((App) getApplicationContext()).getQueueInstance();
						queue.add(request);
					} catch (JSONException e1) {
						onUploadError();
					}
				} else {
					onUploadError();
				}
			}
		}
	};
	
	public void onUploadSuccess(Image image) {
		setImage(image);
		
		finishWithSongUploadResult();
	}
	
	public void onUploadError() {
		Toast.makeText(this, getString(R.string.t_upload_failed), Toast.LENGTH_SHORT).show();
		vUpload.setEnabled(true);
	}
	
	private void finishWithSongUploadResult() {
		EasyTracker easyTracker = EasyTracker.getInstance(RecordSettingActivity.this);
		easyTracker.send(
				MapBuilder.createEvent(
						"ui_action",
						"button_press",
						"upload",
						(long) RecordMainActivity.getRestartNum()
				).build()
		);
		
		Intent intent = new Intent();
		intent.putExtra(RecordMainActivity.INTENT_RESULT_UPLOAD, true);
		intent.putExtra(SongUploadService.INTENT_SYNC_AMOUNT, getCurrentSync());
		
		if (image != null) {
			intent.putExtra(SongUploadService.INTENT_IMAGE_ID, image.getId());
		}
		
		String message = etSongMessage.getText().toString();
		if (message != null && !message.isEmpty()) {
			intent.putExtra(SongUploadService.INTENT_MESSAGE, message);
		}
		
		setResult(Activity.RESULT_OK, intent);
		finish();
	}
	
	private static class PlayerStatusCallback implements ISimplePlayCallback {
		
		private WeakReference<RecordSettingActivity> weakRef;
		
		public PlayerStatusCallback(RecordSettingActivity ref) {
			weakRef = new WeakReference<RecordSettingActivity>(ref);
		}
		
		@Override
		public void onStatusChange(int status) {
			RecordSettingActivity ref = weakRef.get();
			if (ref != null) {
				ref.onPlayerStatusChange(status);
			}
		}
		
	}
	
	public void onPlayerStatusChange(final int status) {
		if (!isFinishing()) {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					switch (status) {
					case ISimplePlayCallback.START:
						enableSyncComponent();
						ivPlayControl.setImageResource(R.drawable.ic_pause_basic);
						ivPlayControl.setOnClickListener(stopClickListener);
						onProgressUpdated();
						
						break;
						
					case ISimplePlayCallback.STOP:
						disableSyncComponent();
						ivPlayControl.setImageResource(R.drawable.ic_play_basic);
						ivPlayControl.setOnClickListener(playClickListener);
						
						break;
					}
				}
			});
		}
	}
	
	private OnClickListener playClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			player.start(getCurrentSync(), headsetPlugged);
		}
	};
	
	private OnClickListener stopClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			stopPlayer();
		}
	};
	
	private float getCurrentSync() {
		float currentSync = 0;
		
		try {
			currentSync = Float.parseFloat(tvSyncValue.getText().toString());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			currentSync = 0;
		}
		
		return currentSync;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
		}
		
		if (dialog != null) {
			dialog.dismiss();
		}
		
		stopPlayer();
		releasePlayer();
	}
	
	private void stopPlayer() {
		if (player != null) {
			player.stop();
		}
	}
	
	private void releasePlayer() {
		if (player != null) {
			player.destroy();
			player = null;
		}
	}

	private void seekChange(View v) {
		if (player != null && player.isPlaying()) {
			player.seekTo(sbPlay.getProgress());
		}
	}
	
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent;
			
			switch (v.getId()) {
			case R.id.ll_restart:
				RecordMainActivity.incrementRestartNum();
				
				intent = new Intent();
				intent.putExtra(RecordMainActivity.INTENT_RESULT_UPLOAD, false);
				setResult(Activity.RESULT_OK, intent);
				finish();
				
				break;
				
			case R.id.ll_exit:
				EasyTracker easyTracker = EasyTracker.getInstance(RecordSettingActivity.this);
				easyTracker.send(
						MapBuilder.createEvent(
								"ui_action",
								"button_press",
								"exit",
								(long) RecordMainActivity.getRestartNum()
						).build()
				);
				
				intent = new Intent();
				setResult(Activity.RESULT_CANCELED);
				finish();
				
				break;
				
			case R.id.iv_sync_back:
			case R.id.iv_sync_forward:
				try {
					float currentValue = Float.parseFloat(tvSyncValue.getText().toString());
					float adjust = (v.getId() == R.id.iv_sync_back) ? -0.05f : 0.05f;
					
					currentValue += adjust;
					player.adjustSync(adjust);
					tvSyncValue.setText(new DecimalFormat("##.##").format(currentValue));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				
				break;
				
			case R.id.iv_song_image:
				intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				
				Intent takePickerIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				takePickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
				Intent chooserIntent = Intent.createChooser(intent, "Select or take a new Picture");
				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { takePickerIntent });
				
				startActivityForResult(chooserIntent, R_CODE_PHOTO_PICKER);
				
				break;
				
			case R.id.btn_delete_image:
				setImage(null);
				newImageAdded = false;
				
				break;
				
			case R.id.btn_other_images:
				if (dialog != null) {
					dialog.show();
				}
				
				break;
				
			default:
				break;
			}
		}
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case R_CODE_PHOTO_PICKER:
			if (resultCode == Activity.RESULT_OK) {
				try {
					ResizeAsyncTask asyncTask = new ResizeAsyncTask();
					asyncTask.setImageView(ivSongImage);
					
					Uri selectedImage = data != null ? data.getData() : tempUri;
					InputStream imageStream = getContentResolver().openInputStream(selectedImage);
					asyncTask.execute(imageStream);
					
					newImageAdded = true;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			break;
		}
	}
	
	public void setImage(Image image) {
		this.image = image;
		
		if (image != null) {
			ImageHelper.displayPhoto(image.getUrl(), ivSongImage);
		} else {
			ivSongImage.setImageDrawable(null);
		}
	}
	
	private void enableSyncComponent() {
		ivSyncBack.setEnabled(true);
		ivSyncforward.setEnabled(true);
	}
	
	private void disableSyncComponent() {
		ivSyncBack.setEnabled(false);
		ivSyncforward.setEnabled(false);
	}
	
	public void onProgressUpdated() {
		if (player != null && player.isPlaying()) {
			int position = player.getCurrentPosition();
			sbPlay.setProgress(position);
			
			Runnable r = new WeakRunnable<RecordSettingActivity>(this, "onProgressUpdated");
			handler.postDelayed(r, 1000);
		}
	}

	@Override
	public void onBackPressed() {}

	@Override
	protected int getChildLayoutResourceId() {
		return NOT_USE_ACTION_BAR;
	}

	@Override
	protected boolean isRootActivity() {
		return false;
	}

	@Override
	protected boolean enablePlayingThumb() {
		return false;
	}

}
