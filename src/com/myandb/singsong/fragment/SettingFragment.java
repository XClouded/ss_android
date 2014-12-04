package com.myandb.singsong.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.google.android.gcm.GCMRegistrar;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.image.ResizeAsyncTask;
import com.myandb.singsong.model.Profile;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JSONObjectSuccessListener;
import com.myandb.singsong.net.OnFailListener;
import com.myandb.singsong.secure.Authenticator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingFragment extends BaseFragment {
	
	public static final int REQUEST_CODE_PHOTO_PICKER = 200;
	
	private Uri tempUri;
	private File scaledImageFile;
	private int colorGrey;
	private int colorPrimary;
	
	private ImageView ivUserPhoto;
	private TextView tvUserNickname;
	private TextView tvUserEmail;
	private TextView tvUserKakao;
	private TextView tvUserStatus;
	private TextView tvIsFacebookActivated;
	private TextView tvIsNotificationEnabled;
	private Button btnLogout;
	private Button btnWithdraw;
	private Button btnUpdatePhoto;
	private View vChangeNickname;
	private View vChangeEmail;
	private View vChangeKakao;
	private View vChangeStatus;
	private View vChangePassword;
	private View vConnectFacebook;
	private View vEnableNotification;

	@Override
	protected int getResourceId() {
		return R.layout.fragment_setting;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		ivUserPhoto = (ImageView) view.findViewById(R.id.iv_user_photo);
		tvUserNickname = (TextView) view.findViewById(R.id.tv_user_nickname);
		tvUserEmail = (TextView) view.findViewById(R.id.tv_user_email);
		tvUserKakao = (TextView) view.findViewById(R.id.tv_user_kakao);
		tvUserStatus = (TextView) view.findViewById(R.id.tv_user_status);
		tvIsFacebookActivated = (TextView) view.findViewById(R.id.tv_is_facebook_activated);
		tvIsNotificationEnabled = (TextView) view.findViewById(R.id.tv_is_notification_enabled);
		btnLogout = (Button) view.findViewById(R.id.btn_logout);
		btnWithdraw = (Button) view.findViewById(R.id.btn_withdraw);
		btnUpdatePhoto = (Button) view.findViewById(R.id.btn_update_photo);
		vChangeNickname = view.findViewById(R.id.ll_change_nickname);
		vChangeEmail = view.findViewById(R.id.ll_change_email);
		vChangeKakao = view.findViewById(R.id.ll_change_kakao);
		vChangeStatus = view.findViewById(R.id.ll_change_status);
		vChangePassword = view.findViewById(R.id.ll_change_password);
		vConnectFacebook = view.findViewById(R.id.rl_connect_facebook);
		vEnableNotification = view.findViewById(R.id.rl_enable_notification);
	}

	@Override
	protected void initialize(Activity activity) {
		colorGrey = getResources().getColor(R.color.font_grey);
		colorPrimary = getResources().getColor(R.color.primary);
		
		try {
			scaledImageFile = File.createTempFile("scaled_image", null, activity.getCacheDir());
			File file = File.createTempFile("user_selected", null, activity.getCacheDir());
			tempUri = Uri.fromFile(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setupViews() {
		registerSharedPreferenceChangeListener();
		
		ivUserPhoto.setOnClickListener(pickPhotoClickListener);
		vEnableNotification.setOnClickListener(enableNotificationClickListener);
		vChangeNickname.setOnClickListener(showDialogClickListener);
		vChangeEmail.setOnClickListener(showDialogClickListener);
		vChangeKakao.setOnClickListener(showDialogClickListener);
		vChangeStatus.setOnClickListener(showDialogClickListener);
		vChangePassword.setOnClickListener(showDialogClickListener);
		btnWithdraw.setOnClickListener(showDialogClickListener);
		btnLogout.setOnClickListener(logoutClickListener);
		
		ImageHelper.displayPhoto(Authenticator.getUser(), ivUserPhoto);
	}
	
	private void registerSharedPreferenceChangeListener() {
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		final String keyNotification = getString(R.string.key_notification);
		boolean enabled = preferences.getBoolean(keyNotification, true);
		
		updateNotificationStatusView(enabled);
		preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
	}
	
	private void updateNotificationStatusView(boolean enabled) {
		if (enabled) {
			tvIsNotificationEnabled.setTextColor(colorPrimary);
		} else {
			tvIsNotificationEnabled.setTextColor(colorGrey);
		}
	}
	
	private OnSharedPreferenceChangeListener preferenceChangeListener = new OnSharedPreferenceChangeListener() {
		
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			if (isAdded()) {
				final String keyNotification = getString(R.string.key_notification);
				
				if (key.equals(keyNotification)) {
					boolean enabled = sharedPreferences.getBoolean(keyNotification, true);
					updateNotificationStatusView(enabled);
				}
			}
		}
	};
	
	private void updateFacebookActivatedView(boolean activated) {
		if (activated) {
			tvIsFacebookActivated.setTextColor(colorPrimary);
			vConnectFacebook.setOnClickListener(null);
			vConnectFacebook.setClickable(false);
		} else {
			tvIsFacebookActivated.setTextColor(colorGrey);
			vConnectFacebook.setOnClickListener(connectFacebookClickListener);
			vConnectFacebook.setClickable(true);
		}
	}
	
	private OnClickListener enableNotificationClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
			final String key = getString(R.string.key_notification);
			boolean enabled = preferences.getBoolean(key, true);
			preferences.edit().putBoolean(key, !enabled).commit();
		}
	};
	
	private OnClickListener pickPhotoClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent photoPickerIntent = new Intent();
			photoPickerIntent.setType("image/*");
			photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
			
			Intent takePickerIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			takePickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
			Intent chooserIntent = Intent.createChooser(photoPickerIntent, "Select or take a new Picture");
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { takePickerIntent });
			
			startActivityForResult(chooserIntent, REQUEST_CODE_PHOTO_PICKER);
		}
	};
	
	private OnClickListener showDialogClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
		}
	};
	
	private OnClickListener connectFacebookClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
		}
	};
	
	private OnClickListener logoutClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			try {
				if (getPlayerService().getPlayer().isPlaying()) {
					getPlayerService().getPlayer().pause();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			unregisterGcm();
			removePushIdOnServer();
		}
	};
	
	private void unregisterGcm() {
		if (GCMRegistrar.isRegistered(getActivity())) {
			GCMRegistrar.unregister(getActivity());
		}
	}
	
	private void removePushIdOnServer() {
		try {
			JSONObject message = new JSONObject();
			message.put("push_id", "");
			
			JSONObjectRequest request = new JSONObjectRequest(
					Method.PUT, "users", message,
					new JSONObjectSuccessListener(this, "onRemovePushIdSuccess"),
					new OnFailListener(this, "onRemovePushIdError"));
			addRequest(request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void onRemovePushIdSuccess(JSONObject response) {
		deleteAccessTokenOnServer();
	}
	
	public void onRemovePushIdError() {
		deleteAccessTokenOnServer();
	}
	
	public void deleteAccessTokenOnServer() {
		JSONObjectRequest request = new JSONObjectRequest(
				Method.DELETE, "token", null,
				new JSONObjectSuccessListener(this, "onRemoveAccessTokenSuccess"),
				new OnFailListener(this, "onRemoveAccessTokenError"));
		addRequest(request);
	}
	
	public void onRemoveAccessTokenSuccess(JSONObject response) {
		restartApplication();
	}
	
	public void onRemoveAccessTokenError() {
		restartApplication();
	}
	
	public void restartApplication() {
		new Authenticator().logout();
		
		Intent intent = new Intent(getActivity(), RootActivity.class);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, HomeFragment.class.getName());
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_ROOT, true);
		startFragment(intent);
	}
	
	private OnClickListener updatePhotoClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
		}
	};

	@Override
	protected void onDataChanged() {
		User user = Authenticator.getUser();
		Profile profile = user.getProfile();
		
		updateFacebookActivatedView(user.isFacebookActivated());
		
		tvUserNickname.setText(user.getNickname());
		
		if (profile.getEmail().length() > 0) {
			tvUserEmail.setText(profile.getEmail());
		} else {
			tvUserEmail.setText("이메일을 입력해주세요.");
		}
		
		if (profile.getKakaotalk().length() > 0) {
			tvUserKakao.setText(profile.getKakaotalk());
		} else {
			tvUserKakao.setText("이벤트 응모 또는 인터뷰 참여에 필요합니다.");
		}
		
		if (profile.getStatusMessage().length() > 0) {
			tvUserStatus.setText(profile.getStatusMessage());
		} else {
			tvUserStatus.setText("지금 기분을 친구들에게 알리세요. :)");
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case REQUEST_CODE_PHOTO_PICKER:
			if (resultCode == Activity.RESULT_OK) {
				try {
					ResizeAsyncTask asyncTask = new ResizeAsyncTask();
					asyncTask.setImageView(ivUserPhoto);
					asyncTask.setOutputFile(scaledImageFile);
					
					Uri selectedImage = data != null ? data.getData() : tempUri;
					InputStream imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
					asyncTask.execute(imageStream);
					
					btnUpdatePhoto.setOnClickListener(updatePhotoClickListener);
					btnUpdatePhoto.setVisibility(View.VISIBLE);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			break;
		}
	}

}
