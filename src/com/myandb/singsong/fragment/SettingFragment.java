package com.myandb.singsong.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionLoginBehavior;
import com.facebook.Session.OpenRequest;
import com.google.android.gcm.GCMRegistrar;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.dialog.BaseDialog;
import com.myandb.singsong.dialog.ChangeEmailDialog;
import com.myandb.singsong.dialog.ChangeKakaoDialog;
import com.myandb.singsong.dialog.ChangeNicknameDialog;
import com.myandb.singsong.dialog.ChangePasswordDialog;
import com.myandb.singsong.dialog.ChangeStatusDialog;
import com.myandb.singsong.dialog.WithdrawDialog;
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
	private int colorGrey;
	private int colorPrimary;
	
	private ImageView ivUserPhoto;
	private TextView tvUserUsername;
	private TextView tvUserNickname;
	private TextView tvUserEmail;
	private TextView tvUserKakao;
	private TextView tvUserStatus;
	private TextView tvIsFacebookActivated;
	private TextView tvIsNotificationEnabled;
	private Button btnLogout;
	private Button btnWithdraw;
	private Button btnChangePhoto;
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
		tvUserUsername = (TextView) view.findViewById(R.id.tv_user_username);
		tvUserNickname = (TextView) view.findViewById(R.id.tv_user_nickname);
		tvUserEmail = (TextView) view.findViewById(R.id.tv_user_email);
		tvUserKakao = (TextView) view.findViewById(R.id.tv_user_kakao);
		tvUserStatus = (TextView) view.findViewById(R.id.tv_user_status);
		tvIsFacebookActivated = (TextView) view.findViewById(R.id.tv_is_facebook_activated);
		tvIsNotificationEnabled = (TextView) view.findViewById(R.id.tv_is_notification_enabled);
		btnLogout = (Button) view.findViewById(R.id.btn_logout);
		btnWithdraw = (Button) view.findViewById(R.id.btn_withdraw);
		btnChangePhoto = (Button) view.findViewById(R.id.btn_change_photo);
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
	protected void setupViews(Bundle savedInstanceState) {
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
			User user = Authenticator.getUser();
			Profile profile = user.getProfile();
			BaseDialog dialog = null;
			Bundle bundle = new Bundle();
			
			switch (v.getId()) {
			case R.id.ll_change_nickname:
				bundle.putString(ChangeNicknameDialog.EXTRA_USER_NICKNAME, user.getNickname());
				dialog = new ChangeNicknameDialog();
				break;
				
			case R.id.ll_change_email:
				bundle.putString(ChangeEmailDialog.EXTRA_USER_EMAIL, profile.getEmail());
				dialog = new ChangeEmailDialog();
				break;
				
			case R.id.ll_change_kakao:
				bundle.putString(ChangeKakaoDialog.EXTRA_USER_KAKAO, profile.getKakaotalk());
				dialog = new ChangeKakaoDialog();
				break;
				
			case R.id.ll_change_status:
				bundle.putString(ChangeStatusDialog.EXTRA_USER_STATUS, profile.getStatusMessage());
				dialog = new ChangeStatusDialog();
				break;
				
			case R.id.ll_change_password:
				dialog = new ChangePasswordDialog();
				break;
				
			case R.id.btn_withdraw:
				dialog = new WithdrawDialog();
				break;

			default:
				return;
			}
			
			if (dialog != null) {
				dialog.setArguments(bundle);
				dialog.show(getChildFragmentManager(), "");
			}
		}
	};
	
	private OnClickListener connectFacebookClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			openFacebookSession();
		}
	};
	
	private void openFacebookSession() {
		OpenRequest request = new OpenRequest(this);
		request.setPermissions(Arrays.asList("email", "user_friends"));
		request.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
		
		Session session = Session.getActiveSession();
		if (session != null) {
			session.close();
		}
		session = new Session(getActivity().getApplicationContext());
		session.addCallback(statusCallback);
		session.openForRead(request);
		Session.setActiveSession(session);
	}
	
	private StatusCallback statusCallback = new StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			if (session.isOpened()) {
				showProgressDialog();
				updateUserFacebookId(session.getAccessToken());
			}
		}
	};
	
	private void updateUserFacebookId(String token) {
		try {
			JSONObject message = new JSONObject();
			message.put("facebook_token", token);
			JSONObjectRequest request = new JSONObjectRequest(
					Method.PUT, "users", message,
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
		makeToast(R.string.t_poor_network_connection);
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
					Method.PUT, "users", message,
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
				Method.DELETE, "token", null,
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
		removeFacebookToken();
		restartApplication();
	}
	
	private void removeFacebookToken() {
		Session session = Session.getActiveSession();
		if (session != null) {
			session.closeAndClearTokenInformation();
			session.close();
		}
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
					Method.PUT, "users", message,
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
		makeToast("사진이 변경 되었습니다.");
		dismissProgressDialog();
	}
	
	public void onUploadError() {
		dismissProgressDialog();
		makeToast("사진 업로드에 실패하였습니다.");
	}

	@Override
	protected void onDataChanged() {
		User user = Authenticator.getUser();
		Profile profile = user.getProfile();
		
		updateFacebookActivatedView(user.isFacebookActivated());
		
		tvUserUsername.setText(user.getUsername());
		tvUserNickname.setText(user.getNickname());
		
		if (profile.getEmail().length() > 0) {
			tvUserEmail.setText(profile.getEmail());
		} else {
			tvUserEmail.setText(getString(R.string.hint_email));
		}
		
		if (profile.getKakaotalk().length() > 0) {
			tvUserKakao.setText(profile.getKakaotalk());
		} else {
			tvUserKakao.setText(getString(R.string.hint_kakao));
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
		Session session = Session.getActiveSession();
		if (session != null) {
			session.onActivityResult(getActivity(), requestCode, resultCode, data);
		}
		
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
					
					btnChangePhoto.setOnClickListener(uploadPhotoClickListener);
					btnChangePhoto.setVisibility(View.VISIBLE);
				} catch (FileNotFoundException e) {
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
