package com.myandb.singsong.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.RecordMainActivity;
import com.myandb.singsong.audio.OnPlayEventListener;
import com.myandb.singsong.audio.PcmPlayer;
import com.myandb.singsong.audio.PlayEvent;
import com.myandb.singsong.audio.Recorder;
import com.myandb.singsong.audio.Track;
import com.myandb.singsong.dialog.ImageSelectDialog;
import com.myandb.singsong.event.WeakRunnable;
import com.myandb.singsong.file.FileManager;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.image.ResizeAsyncTask;
import com.myandb.singsong.model.Image;
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
	
	public static final String EXTRA_HEADSET_PLUGGED = "headset_plugged";
	public static final String EXTRA_RECORD_PCM_FILE_NAME = "record_pcm_file_name";
	public static final String EXTRA_MUSIC_PCM_FILE_NAME = "music_pcm_file_name";
	
	private PcmPlayer player;
	private ImageSelectDialog dialog;
	private Handler handler;
	private Uri tempUri;
	private boolean imageAdded;
	private boolean headsetPlugged;
	
	private TextView tvSyncValue;
	private ImageView ivPlayControl;
	private ImageView ivSyncBack;
	private ImageView ivSyncforward;
	private ImageView ivSongImage;
	private Button btnOtherImages;
	private Button btnDeletePhoto;
	private EditText etSongMessage;
	private SeekBar sbPlay;
	private View vRestart;
	private View vUpload;
	private View vExit;
	private View vMixer;

	@Override
	protected int getResourceId() {
		return R.layout.fragment_record_setting;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		headsetPlugged = bundle.getBoolean(EXTRA_HEADSET_PLUGGED);
		
		player = new PcmPlayer();
		player.setOnPlayEventListener(onPlayEventListener);
		
		String recordPcmFileName = bundle.getString(EXTRA_RECORD_PCM_FILE_NAME);
		Track recordTrack = new Track(new File(recordPcmFileName), 1);
		player.addTrack("record", recordTrack);
		if (headsetPlugged) {
			String musicPcmFileName = bundle.getString(EXTRA_MUSIC_PCM_FILE_NAME);
			Track musicTrack = new Track(new File(musicPcmFileName), 2);
			player.addTrack("music", musicTrack);
		}
		player.setLeadTrack("record");
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
		sbPlay = (SeekBar) view.findViewById(R.id.sb_play);
		etSongMessage = (EditText) view.findViewById(R.id.et_song_message);
		btnOtherImages = (Button) view.findViewById(R.id.btn_other_images);
		btnDeletePhoto = (Button) view.findViewById(R.id.btn_delete_image);
		
		vRestart = view.findViewById(R.id.ll_restart);
		vUpload = view.findViewById(R.id.ll_upload);
		vExit = view.findViewById(R.id.ll_exit);
		vMixer = view.findViewById(R.id.ll_mixer);
	}

	@Override
	protected void initialize(Activity activity) {
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		dialog = new ImageSelectDialog(this);
		
		handler = new Handler();
		
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

	@Override
	protected void setupViews() {
		if (headsetPlugged) {
			vMixer.setVisibility(View.VISIBLE);
		} else {
			vMixer.setVisibility(View.GONE);
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
		sbPlay.setOnSeekBarChangeListener(seekBarChangeListener);
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
					startUploadServiceAndFinish();
				} else {
					Toast.makeText(getActivity(), getString(R.string.t_song_length_policy), Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};
	
	private void startUploadServiceAndFinish() {
		Intent intent = new Intent();
		intent.putExtra(RecordMainActivity.EXTRA_UPLOAD_SONG, true);
		intent.putExtra(SongUploadService.EXTRA_SYNC_AMOUNT, getTrackOffset("record"));
		intent.putExtra(SongUploadService.EXTRA_SYNC_AMOUNT, getTrackOffset("music"));
		intent.putExtra(SongUploadService.EXTRA_IMAGE_ADDED, imageAdded);
		intent.putExtra(SongUploadService.INTENT_MESSAGE, getSongMessage());
		
		getActivity().setResult(Activity.RESULT_OK, intent);
		getActivity().finish();
	}
	
	private int getTrackOffset(String key) {
		Track track = player.getTrack(key);
		if (track != null) {
			return track.getOffsetSize();
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
				Track recordTrack = player.getTrack("record");
				Track musicTrack = player.getTrack("music");
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
				takePickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
				Intent chooserIntent = Intent.createChooser(intent, "Select or take a new Picture");
				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { takePickerIntent });
				
				getActivity().startActivityForResult(chooserIntent, REQUEST_CODE_PHOTO_PICKER);
				break;
				
			case R.id.btn_delete_image:
				setImage(null);
				imageAdded = false;
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
	
	private OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {
		
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
	
	public void setImage(Image image) {
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
					asyncTask.setImageView(ivSongImage);
					
					Uri selectedImage = data != null ? data.getData() : tempUri;
					ContentResolver resolver = getActivity().getContentResolver();
					InputStream imageStream = resolver.openInputStream(selectedImage);
					asyncTask.execute(imageStream);
					
					imageAdded = true;
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
		
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

}
