package com.myandb.singsong.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import com.myandb.singsong.R;
import com.myandb.singsong.audio.OnPlayEventListener;
import com.myandb.singsong.audio.PcmPlayer;
import com.myandb.singsong.audio.PlayEvent;
import com.myandb.singsong.audio.Recorder;
import com.myandb.singsong.audio.Track;
import com.myandb.singsong.dialog.ImageSelectDialog;
import com.myandb.singsong.event.OnCompleteListener;
import com.myandb.singsong.event.WeakRunnable;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.image.ResizeAsyncTask;
import com.myandb.singsong.model.Image;
import com.myandb.singsong.model.Model;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.net.JSONObjectSuccessListener;
import com.myandb.singsong.net.UploadManager;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.service.SongUploadService;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class RecordSettingFragment extends BaseFragment {
	
	public static final int REQUEST_CODE_PHOTO_PICKER = 100;
	
	private static final String TRACK_RECORD = "record";
	private static final String TRACK_MUSIC = "music";
	
	private PcmPlayer player;
	private ImageSelectDialog dialog;
	private Handler handler;
	private Uri imageUri;
	private File imageFile;
	private Image image;
	private String imageName;
	private boolean localImageExist;
	private boolean headsetPlugged;
	
	private TextView tvSyncValue;
	private TextView tvVolumeValue;
	private ImageView ivPlayControl;
	private ImageView ivSyncBack;
	private ImageView ivSyncforward;
	private ImageView ivSongImage;
	private Button btnOtherImages;
	private Button btnDeletePhoto;
	private EditText etSongMessage;
	private SeekBar sbPlay;
	private SeekBar sbVolume;
	private View vRestart;
	private View vUpload;
	private View vExit;
	private View vMixer;
	private View vVolume;

	@Override
	protected int getResourceId() {
		return R.layout.fragment_record_setting;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		headsetPlugged = bundle.getBoolean(SongUploadService.EXTRA_HEADSET_PLUGGED);
		
		try {
			player = new PcmPlayer();
			player.setOnPlayEventListener(onPlayEventListener);
			
			String recordPcmFilePath = bundle.getString(SongUploadService.EXTRA_RECORD_PCM_FILE_PATH);
			Track recordTrack = new Track(new File(recordPcmFilePath), Recorder.CHANNELS);
			player.addTrack(TRACK_RECORD, recordTrack);
			if (headsetPlugged) {
				String musicPcmFilePath = bundle.getString(SongUploadService.EXTRA_MUSIC_PCM_FILE_PATH);
				Track musicTrack = new Track(new File(musicPcmFilePath), PcmPlayer.CHANNELS);
				player.addTrack(TRACK_MUSIC, musicTrack);
			}
			player.setLeadTrack(TRACK_RECORD);
		} catch (Exception e) {
			e.printStackTrace();
			// This cannot be happened
		}
	}
	
	private OnPlayEventListener onPlayEventListener = new OnPlayEventListener() {
		
		@Override
		public void onPlay(final PlayEvent event) {
			getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					switch (event) {
					case PLAY:
						enableSyncComponent();
						ivPlayControl.setImageResource(R.drawable.ic_pause_basic);
						ivPlayControl.setOnClickListener(stopClickListener);
						onProgressUpdated();
						break;
						
					case STOP:
						disableSyncComponent();
						ivPlayControl.setImageResource(R.drawable.ic_play_basic);
						ivPlayControl.setOnClickListener(playClickListener);
						break;
						
					default:
						break;
					}
				}
			});
		}
	};
	
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
			int position = (int) player.getCurrentPosition();
			sbPlay.setProgress(position);
			
			Runnable r = new WeakRunnable<RecordSettingFragment>(this, "onProgressUpdated");
			handler.postDelayed(r, 1000);
		}
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		ivPlayControl = (ImageView) view.findViewById(R.id.iv_play_control);
		ivSyncBack = (ImageView) view.findViewById(R.id.iv_sync_back);
		ivSyncforward = (ImageView) view.findViewById(R.id.iv_sync_forward);
		ivSongImage = (ImageView) view.findViewById(R.id.iv_song_image);
		
		tvSyncValue = (TextView) view.findViewById(R.id.tv_sync_value);
		tvVolumeValue = (TextView) view.findViewById(R.id.tv_volume_value);
		sbPlay = (SeekBar) view.findViewById(R.id.sb_play);
		sbVolume = (SeekBar) view.findViewById(R.id.sb_volume);
		etSongMessage = (EditText) view.findViewById(R.id.et_song_message);
		btnOtherImages = (Button) view.findViewById(R.id.btn_other_images);
		btnDeletePhoto = (Button) view.findViewById(R.id.btn_delete_image);
		
		vRestart = view.findViewById(R.id.ll_restart);
		vUpload = view.findViewById(R.id.ll_upload);
		vExit = view.findViewById(R.id.ll_exit);
		vMixer = view.findViewById(R.id.ll_mixer);
		vVolume = view.findViewById(R.id.ll_volume);
	}

	@Override
	protected void initialize(Activity activity) {
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		dialog = new ImageSelectDialog();
		
		handler = new Handler();
		
		try {
			imageFile = File.createTempFile("scaled_image", null, activity.getCacheDir());
			imageUri = Uri.fromFile(imageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		if (headsetPlugged) {
			vMixer.setVisibility(View.VISIBLE);
			vVolume.setVisibility(View.VISIBLE);
		} else {
			vMixer.setVisibility(View.GONE);
			vVolume.setVisibility(View.GONE);
		}
		
		ivPlayControl.setOnClickListener(playClickListener);
		vUpload.setOnClickListener(finishClickListener);
		vRestart.setOnClickListener(finishClickListener);
		vExit.setOnClickListener(finishClickListener);
		ivSyncBack.setOnClickListener(syncChangeClickListener);
		ivSyncforward.setOnClickListener(syncChangeClickListener);
		btnDeletePhoto.setOnClickListener(imageClickListener);
		ivSongImage.setOnClickListener(imageClickListener);
		btnOtherImages.setOnClickListener(imageClickListener);
		
		tvSyncValue.setText("0.0");
		sbPlay.setMax((int) player.getDuration());
		sbPlay.setOnSeekBarChangeListener(playSeekBarChangeListener);
		
		tvVolumeValue.setText("100%");
		sbVolume.setMax(200);
		sbVolume.setProgress(100);
		sbVolume.setOnSeekBarChangeListener(volumeSeekBarChangeListener);
	}
	
	private OnClickListener playClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			startPlayer();
		}
	};
	
	private void startPlayer() {
		if (player != null && !player.isPlaying()) {
			player.start();
		}
	}
	
	private OnClickListener stopClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			stopPlayer();
		}
	};
	
	private void stopPlayer() {
		if (player != null && player.isPlaying()) {
			player.stop();
		}
	}
	
	private OnClickListener finishClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ll_restart:
				getActivity().setResult(Activity.RESULT_OK);
				getActivity().finish();
				return;
				
			case R.id.ll_exit:
				getActivity().setResult(Activity.RESULT_CANCELED);
				getActivity().finish();
				return;
				
			case R.id.ll_upload:
				v.setEnabled(false);
				
				Track recordTrack = player.getTrack("record");
				if (Recorder.isValidRecordingTime(recordTrack.getSourceDuration())) {
					uploadImageIfExist();
				} else {
					Toast.makeText(getActivity(), getString(R.string.t_song_length_policy), Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};
	
	private void uploadImageIfExist() {
		if (localImageExist) {
			try {
				imageName = generateIamgeName();
				UploadManager manager = new UploadManager();
				manager.start(getActivity(), imageFile, "image", imageName, "image/jpeg", imageUploadCompleteListener);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			finishWithUploadInfo();
		}
	}
	
	private String generateIamgeName() {
		User user = Authenticator.getUser();
		return Image.generateName(user);
	}
	
	private OnCompleteListener imageUploadCompleteListener = new OnCompleteListener() {
		
		@Override
		public void done(Exception e) {
			if (e == null) {
				try {
					JSONObject message = new JSONObject();
					message.put("url", Model.STORAGE_HOST + Model.STORAGE_IMAGE + imageName);
					
					JSONObjectRequest request = new JSONObjectRequest(
							"images", message,
							new JSONObjectSuccessListener(RecordSettingFragment.this, "onUploadSuccess", Image.class),
							new JSONErrorListener(RecordSettingFragment.this, "onUploadError")
					);
					addRequest(request);
				} catch (JSONException e1) {
					onUploadError();
				}
			} else {
				onUploadError();
			}
		}
	};
	
	public void onUploadSuccess(Image image) {
		setImage(image);
		finishWithUploadInfo();
	}
	
	public void onUploadError() {
		Toast.makeText(getActivity(), getString(R.string.t_upload_failed), Toast.LENGTH_SHORT).show();
		vUpload.setEnabled(true);
	}
	
	private void finishWithUploadInfo() {
		Intent intent = new Intent();
		intent.putExtra(SongUploadService.EXTRA_RECORD_OFFSET, getTrackOffset(TRACK_RECORD));
		intent.putExtra(SongUploadService.EXTRA_MUSIC_OFFSET, getTrackOffset(TRACK_MUSIC));
		intent.putExtra(SongUploadService.EXTRA_IMAGE_ID, getImageId());
		intent.putExtra(SongUploadService.EXTRA_SONG_MESSAGE, getSongMessage());
		intent.putExtra(SongUploadService.EXTRA_RECORD_VOLUME, getTrackVolume(TRACK_RECORD));
		
		getActivity().setResult(Activity.RESULT_FIRST_USER, intent);
		getActivity().finish();
	}
	
	private int getImageId() {
		return image != null ? image.getId() : 0;
	}
	
	private int getTrackOffset(String key) {
		Track track = player.getTrack(key);
		if (track != null) {
			return track.getOffsetSize();
		} else {
			return 0;
		}
	}
	
	private float getTrackVolume(String key) {
		Track track = player.getTrack(key);
		if (track != null) {
			return track.getVolume();
		} else {
			return 0;
		}
	}
	
	private String getSongMessage() {
		return etSongMessage.getText().toString();
	}
	
	private OnClickListener syncChangeClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			try {
				float currentValue = Float.parseFloat(tvSyncValue.getText().toString());
				float adjust = (v.getId() == R.id.iv_sync_back) ? -0.05f : 0.05f;
				
				currentValue += adjust;
				tvSyncValue.setText(new DecimalFormat("##.##").format(currentValue));
				
				int offsetFrame = (int) (adjust * PcmPlayer.SAMPLERATE);
				Track recordTrack = player.getTrack(TRACK_RECORD);
				Track musicTrack = player.getTrack(TRACK_MUSIC);
				if (offsetFrame > 0) {
					if (musicTrack.getOffsetSize() > 0) {
						musicTrack.addOffsetFrame(-offsetFrame); 
					} else {
						recordTrack.addOffsetFrame(offsetFrame);
					}
				} else {
					if (recordTrack.getOffsetSize() > 0) {
						recordTrack.addOffsetFrame(offsetFrame);
					}  else {
						musicTrack.addOffsetFrame(-offsetFrame);
					}
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
	};

	private OnClickListener imageClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_song_image:
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				
				Intent takePickerIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				takePickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				Intent chooserIntent = Intent.createChooser(intent, "Select or take a new Picture");
				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { takePickerIntent });
				
				startActivityForResult(chooserIntent, REQUEST_CODE_PHOTO_PICKER);
				break;
				
			case R.id.btn_delete_image:
				setImage(null);
				break;
				
			case R.id.btn_other_images:
				if (dialog != null) {
					dialog.show(getChildFragmentManager(), "");
				}
				break;
				
			default:
				break;
			}
		}
	};
	
	private OnSeekBarChangeListener playSeekBarChangeListener = new OnSeekBarChangeListener() {
		
		private boolean trackManuallyChanged;
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			trackManuallyChanged = true;
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (trackManuallyChanged) {
				if (player != null && player.isPlaying()) {
					player.seekTo(progress);
					trackManuallyChanged = false;
				}
			}
		}
	};
	
	private OnSeekBarChangeListener volumeSeekBarChangeListener = new OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			final Track voiceTrack = player.getTrack(TRACK_RECORD);
			final float volume = (float) (progress / 100.f);
			voiceTrack.setVolume(volume);
			tvVolumeValue.setText(String.valueOf(progress) + "%");
		}
	};
	
	public void setImage(Image image) {
		this.image = image;
		this.localImageExist = false;
		
		if (image != null) {
			ImageHelper.displayPhoto(image.getUrl(), ivSongImage);
		} else {
			ivSongImage.setImageDrawable(null);
		}
	}

	@Override
	protected void onDataChanged() {
		startPlayer();
	}

	@Override
	public void onBackPressed() {}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case REQUEST_CODE_PHOTO_PICKER:
			if (resultCode == Activity.RESULT_OK) {
				try {
					ResizeAsyncTask asyncTask = new ResizeAsyncTask();
					asyncTask.setOutputFile(imageFile);
					asyncTask.setImageView(ivSongImage);
					
					Uri selectedImage = data != null ? data.getData() : imageUri;
					ContentResolver resolver = getActivity().getContentResolver();
					InputStream imageStream = resolver.openInputStream(selectedImage);
					asyncTask.execute(imageStream);
					
					localImageExist = true;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
			handler = null;
		}
		
		if (player != null) {
			player.release();
			player = null;
		}
	}

}
