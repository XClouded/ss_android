package com.myandb.singsong.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.facebook.Session;
import com.google.android.gcm.GCMRegistrar;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.dialog.AuthenticationDialog;
import com.myandb.singsong.dialog.BaseDialog;
import com.myandb.singsong.dialog.ChangeNicknameDialog;
import com.myandb.singsong.dialog.ChangeStatusDialog;
import com.myandb.singsong.dialog.AuthenticationDialog.AuthenticationType;
import com.myandb.singsong.event.OnCompleteListener;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.image.ResizeAsyncTask;
import com.myandb.singsong.model.Model;
import com.myandb.singsong.model.Profile;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JSONObjectSuccessListener;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.net.UploadManager;
import com.myandb.singsong.secure.Authenticator;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.Permission.Type;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
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
	
	private ImageView ivUserPhoto;
	private TextView tvUserUsername;
	private TextView tvUserNickname;
	private TextView tvUserStatus;
	private ImageView ivIsFacebookActivated;
	private ImageView ivIsNotificationEnabled;
	private Button btnChangePhoto;
	private View vUsername;
	private View vChangeNickname;
	private View vChangeStatus;
	private View vConnectFacebook;
	private View vEnableNotification;
	private View vMergeSingSongAccount;
	private View vMergeSingSongAccountBanner;

	@Override
	protected int getResourceId() {
		return R.layout.fragment_setting;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		ivUserPhoto = (ImageView) view.findViewById(R.id.iv_user_photo);
		tvUserUsername = (TextView) view.findViewById(R.id.tv_user_username);
		tvUserNickname = (TextView) view.findViewById(R.id.tv_user_nickname);
		tvUserStatus = (TextView) view.findViewById(R.id.tv_user_status);
		ivIsFacebookActivated = (ImageView) view.findViewById(R.id.iv_is_facebook_activated);
		ivIsNotificationEnabled = (ImageView) view.findViewById(R.id.iv_is_notification_enabled);
		btnChangePhoto = (Button) view.findViewById(R.id.btn_change_photo);
		vUsername = view.findViewById(R.id.ll_username);
		vChangeNickname = view.findViewById(R.id.ll_change_nickname);
		vChangeStatus = view.findViewById(R.id.ll_change_status);
		vConnectFacebook = view.findViewById(R.id.rl_connect_facebook);
		vEnableNotification = view.findViewById(R.id.rl_enable_notification);
		vMergeSingSongAccount = view.findViewById(R.id.rl_merge_singsong_account);
		vMergeSingSongAccountBanner = view.findViewById(R.id.rl_merge_singsong_account_banner);
	}

	@Override
	protected void initialize(Activity activity) {
		try {
			scaledImageFile = File.createTempFile("scaled_image", null, activity.getCacheDir());
			File file = File.createTempFile("user_selected", null, activity.getCacheDir());
			tempUri = Uri.fromFile(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		registerSharedPreferenceChangeListener();
		
		ivUserPhoto.setOnClickListener(pickPhotoClickListener);
		vEnableNotification.setOnClickListener(enableNotificationClickListener);
		vUsername.setOnClickListener(logoutClickListener);
		vChangeNickname.setOnClickListener(showDialogClickListener);
		vChangeStatus.setOnClickListener(showDialogClickListener);
		
		if (Authenticator.getUser().isSingSongIntegrated()) {
			vMergeSingSongAccount.setVisibility(View.GONE);
			vMergeSingSongAccountBanner.setVisibility(View.GONE);
		} else {
			Calendar today = Calendar.getInstance(Locale.KOREA);
			
			if (today.before(getMergeBannerDueDate())) {
				vMergeSingSongAccountBanner.setVisibility(View.VISIBLE);
				vMergeSingSongAccountBanner.setOnClickListener(mergeSingSongAccountClickListener);
			} else {
				vMergeSingSongAccountBanner.setVisibility(View.GONE);
			}
			
			if (today.before(getMergeMenuDueDate())) {
				vMergeSingSongAccount.setVisibility(View.VISIBLE);
				vMergeSingSongAccount.setOnClickListener(mergeSingSongAccountClickListener);
			} else {
				vMergeSingSongAccount.setVisibility(View.GONE);
			}
		}
		
		ImageHelper.displayPhoto(Authenticator.getUser(), ivUserPhoto);
	}
	
	private Calendar getMergeBannerDueDate() {
		Calendar date = Calendar.getInstance(Locale.KOREA);
		date.set(2015, 12, 31);
		return date;
	}
	
	private Calendar getMergeMenuDueDate() {
		Calendar date = Calendar.getInstance(Locale.KOREA);
		date.set(2016, 8, 1);
		return date;
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
			ivIsNotificationEnabled.setImageResource(R.drawable.ic_check);
		} else {
			ivIsNotificationEnabled.setImageDrawable(null);
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
			ivIsFacebookActivated.setImageResource(R.drawable.ic_check);
			vConnectFacebook.setOnClickListener(null);
			vConnectFacebook.setClickable(false);
		} else {
			ivIsFacebookActivated.setImageDrawable(null);
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
			User user = Authenticator.getUser();
			Profile profile = user.getProfile();
			
			if (profile == null) {
				makeToast("프로필 정보를 불러들이는데 실패했습니다. 다시 로그인해주세요 :");
				return;
			}
			
			BaseDialog dialog = null;
			Bundle bundle = new Bundle();
			
			switch (v.getId()) {
			case R.id.ll_change_nickname:
				bundle.putString(ChangeNicknameDialog.EXTRA_USER_NICKNAME, user.getNickname());
				dialog = new ChangeNicknameDialog();
				break;
				
			case R.id.ll_change_status:
				bundle.putString(ChangeStatusDialog.EXTRA_USER_STATUS, profile.getStatusMessage());
				dialog = new ChangeStatusDialog();
				break;

			default:
				return;
			}
			
			if (dialog != null) {
				dialog.setArguments(bundle);
				try {
					dialog.show(getChildFragmentManager(), "");
				} catch (IllegalStateException e) {
					e.printStackTrace();
					// Can not perform this action after onSaveInstanceState
					// Android bug
				}
			}
		}
	};
	
	private OnClickListener connectFacebookClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			final SimpleFacebook simpleFacebook = getSimpleFacebook();
			simpleFacebook.login(new OnLoginListener() {
				
				@Override
				public void onFail(String arg0) {}
				
				@Override
				public void onException(Throwable arg0) {}
				
				@Override
				public void onThinking() {}
				
				@Override
				public void onNotAcceptingPermissions(Type arg0) {}
				
				@Override
				public void onLogin() {
					showProgressDialog();
					Session session = simpleFacebook.getSession();
					updateUserFacebookId(session.getAccessToken());
				}
			});
		}
	};
	
	private void updateUserFacebookId(String token) {
		try {
			JSONObject message = new JSONObject();
			message.put("facebook_token", token);
			JSONObjectRequest request = new JSONObjectRequest(
					Method.PUT, "users", null, message,
					new JSONObjectSuccessListener(this, "onUpdateUserFacebookIdSuccess", User.class),
					new JSONErrorListener(this, "onUpdateUserFacebookIdError"));
			addRequest(request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void onUpdateUserFacebookIdSuccess(User user) {
		new Authenticator().update(user);
		updateFacebookActivatedView(true);
		dismissProgressDialog();
	}
	
	public void onUpdateUserFacebookIdError() {
		dismissProgressDialog();
		makeToast(R.string.t_critical_poor_network_connection);
	}
	
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
					Method.PUT, "users", null, message,
					new JSONObjectSuccessListener(this, "onRemovePushIdSuccess"),
					new JSONErrorListener(this, "onRemovePushIdError"));
			addRequest(request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void onRemovePushIdSuccess(JSONObject response) {
		removeAccessTokenOnServer();
	}
	
	public void onRemovePushIdError() {
		removeAccessTokenOnServer();
	}
	
	public void removeAccessTokenOnServer() {
		JSONObjectRequest request = new JSONObjectRequest(
				Method.DELETE, "token", null, null,
				new JSONObjectSuccessListener(this, "onRemoveAccessTokenSuccess"),
				new JSONErrorListener(this, "onRemoveAccessTokenError"));
		addRequest(request);
	}
	
	public void onRemoveAccessTokenSuccess(JSONObject response) {
		clearSharedPreferences();
	}
	
	public void onRemoveAccessTokenError() {
		clearSharedPreferences();
	}
	
	public void clearSharedPreferences() {
		new Authenticator().logout();
		PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().clear().commit();
		logoutFacebook();
		restartApplication();
	}
	
	private void logoutFacebook() {
		getSimpleFacebook().logout(new OnLogoutListener() {
			
			@Override
			public void onFail(String arg0) {}
			
			@Override
			public void onException(Throwable arg0) {}
			
			@Override
			public void onThinking() {}
			
			@Override
			public void onLogout() {}
		});
	}
	
	private void restartApplication() {
		Intent intent = new Intent(getActivity(), RootActivity.class);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, HomeFragment.class.getName());
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_ROOT, true);
		startFragment(intent);
	}
	
	private OnClickListener uploadPhotoClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			try {
				setProgressDialogMessage("사진을 업로드 중입니다.");
				showProgressDialog();
				uploadPhotoFile();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				onUploadError();
			}
		}
	};
	
	private void uploadPhotoFile() throws FileNotFoundException {
		UploadManager manager = new UploadManager();
		User user = Authenticator.getUser();
		manager.start(
				getActivity(), scaledImageFile,
				"user_photo", user.getUsername() + Model.SUFFIX_JPG, "image/jpeg",
				photoUploadCompleteListener
		);
	}
	
	private OnCompleteListener photoUploadCompleteListener = new OnCompleteListener() {
		
		@Override
		public void done(Exception e) {
			if (e == null) {
				updatePhotoMetaData();
			} else {
				onUploadError();
			}
		}
	};
	
	private void updatePhotoMetaData() {
		try {
			String username = Authenticator.getUser().getUsername();
			String photoUrl = Model.STORAGE_HOST + Model.STORAGE_USER + username + Model.SUFFIX_JPG;
			JSONObject message = new JSONObject();
			message.put("main_photo_url", photoUrl);
			
			JSONObjectRequest request = new JSONObjectRequest(
					Method.PUT, "users", null, message,
					new JSONObjectSuccessListener(this, "onUpdateSuccess", User.class),
					new JSONErrorListener(this, "onUploadError"));
			addRequest(request);
		} catch (JSONException e) {
			e.printStackTrace();
			onUploadError();
		}
	}
	
	public void onUpdateSuccess(User user) {
		new Authenticator().update(user);
		btnChangePhoto.setVisibility(View.GONE);
		makeToast(getString(R.string.t_notify_upload_photo_succeed));
		dismissProgressDialog();
	}
	
	public void onUploadError() {
		dismissProgressDialog();
		makeToast(getString(R.string.t_alert_upload_failed));
	}
	
	private OnClickListener mergeSingSongAccountClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Bundle bundle = new Bundle();
			bundle.putSerializable(AuthenticationType.class.getName(), AuthenticationType.MELON_LOGIN_SINGSONG_INTEGRATION);
			
			AuthenticationDialog dialog = new AuthenticationDialog();
			dialog.setArguments(bundle);
			dialog.show(getFragmentManager(), "");
		}
	};

	@Override
	protected void onDataChanged() {
		User user = Authenticator.getUser();
		tvUserUsername.setText(user.getUsername());
		tvUserNickname.setText(user.getNickname());
		
		updateFacebookActivatedView(user.isFacebookActivated());
		
		Profile profile = user.getProfile();
		if (profile == null) {
			makeToast("프로필 정보를 불러들이는데 실패했습니다. 다시 로그인해주세요 :");
			return;
		}
		
		if (profile.getStatusMessage().length() > 0) {
			tvUserStatus.setText(profile.getStatusMessage());
		} else {
			tvUserStatus.setText(getString(R.string.hint_status));
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
					if (selectedImage != null) {
						InputStream imageStream = getActivity().getContentResolver().openInputStream(selectedImage);
						asyncTask.execute(imageStream);
						btnChangePhoto.setOnClickListener(uploadPhotoClickListener);
						btnChangePhoto.setVisibility(View.VISIBLE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		}
	}

	@Override
	public void onBackPressed() {
		getActivity().setResult(Activity.RESULT_OK);
		super.onBackPressed();
	}

}
