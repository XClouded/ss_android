package com.myandb.singsong.dialog;

import java.io.File;

import com.myandb.singsong.R;
import com.myandb.singsong.util.ExternalStorage;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PhotoIntentDialog extends BaseDialog {
	
	public static final String EXTRA_REQUEST_CODE = "request_code";
	
	public static final int DEFAULT_REQUEST_CODE_PHOTO_PICKER = 200;
	
	private static int requestCodePhotoPicker = DEFAULT_REQUEST_CODE_PHOTO_PICKER;
	private static Uri shotUri;
	
	private Button btnGallery;
	private Button btnCamera;
	private Button btnDefault;
	private OnClickListener defaultImageClickListener;

	@Override
	protected int getResourceId() {
		return R.layout.dialog_photo_intent;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		
		requestCodePhotoPicker = bundle.getInt(EXTRA_REQUEST_CODE); 
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		btnGallery = (Button) view.findViewById(R.id.btn_gallery);
		btnCamera = (Button) view.findViewById(R.id.btn_camera);
		btnDefault = (Button) view.findViewById(R.id.btn_default);
	}

	@Override
	protected void initialize(Activity activity) {}

	@Override
	protected void setupViews() {
		btnGallery.setOnClickListener(pickerClickListener);
		btnCamera.setOnClickListener(pickerClickListener);
		btnDefault.setOnClickListener(pickerClickListener);
	}
	
	private OnClickListener pickerClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = null;
			
			switch (v.getId()) {
			case R.id.btn_gallery:
				intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				break;
				
			case R.id.btn_camera:
				shotUri = makeShotUri();
				intent = new Intent();
				intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, shotUri);
				break;
				
			case R.id.btn_default:
				if (defaultImageClickListener != null) {
					defaultImageClickListener.onClick(v);
				}
				break;

			default:
				break;
			}
			
			if (intent != null) {
				startActivityForResult(intent);
			}
			
			dismiss();
		}
		
	};
	
	private void startActivityForResult(Intent intent) {
		if (getParentFragment() != null) {
			getParentFragment().startActivityForResult(intent, requestCodePhotoPicker);
		} else {
			getActivity().startActivityForResult(intent, requestCodePhotoPicker);
		}
	}
	
	public void setOnDefaultImageClickListener(OnClickListener listener) {
		defaultImageClickListener = listener;
	}
	
	private Uri makeShotUri() {
		return makeUriFromFileName(generateShotFileName());
	}
	
	private Uri makeUriFromFileName(String fileName) {
		File root = ExternalStorage.getRootDirectory();
		File shotFile = new File(root, fileName);
		return Uri.fromFile(shotFile);
	}
	
	private String generateShotFileName() {
		return "shot." + System.currentTimeMillis() + ".jpg";
	}
	
	public static Uri getUriOnActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode != requestCodePhotoPicker) {
			return null;
		}
		
		if (resultCode != Activity.RESULT_OK) {
			return null;
		}
		
		return getUriOnActivityResult(data);
	}
	
	private static Uri getUriOnActivityResult(Intent data) {
		return data != null ? data.getData() : shotUri;
	}

}
