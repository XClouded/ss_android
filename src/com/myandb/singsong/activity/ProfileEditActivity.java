package com.myandb.singsong.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.event.OnCompleteListener;
import com.myandb.singsong.event.OnVolleyWeakError;
import com.myandb.singsong.event.OnVolleyWeakResponse;
import com.myandb.singsong.fragment.UserHomeFragment;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.image.ResizeAsyncTask;
import com.myandb.singsong.model.Model;
import com.myandb.singsong.model.Profile;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JustRequest;
import com.myandb.singsong.net.UploadManager;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.secure.Encryption;
import com.myandb.singsong.util.Utility;

public class ProfileEditActivity extends Activity {
	
	public static final int R_CODE_PHOTO_PICKER = 100;
	
	private ImageView ivUserPhoto;
	private Button btnSubmitChange;
	private Button btnCancelChange;
	private Button btnChangePassword;
	private EditText etKakaotalk;
	private EditText etNickname;
	private EditText etEmail;
	private EditText etStatus;
	private EditText etOldPassword;
	private EditText etNewPassword;
	private EditText etNewPasswordRe;
	private TextView tvValidNickname;
	private Handler handler;
	private SimpleTextWatcher kakaotalkTextWatcher;
	private SimpleTextWatcher emailTextWatcher;
	private SimpleTextWatcher statusTextWatcher;
	private String lastInputNickname;
	private User currentUser;
	private Profile profile;
	private boolean isPhotoChange 		 = false;
	private boolean isValidNickname 	 = false;
	private int colorPrimary;
	private int colorRed;
	private Uri tempUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (Authenticator.isLoggedIn()) {
			handler = new Handler();
			
			colorPrimary = getResources().getColor(R.color.font_highlight);
			colorRed = getResources().getColor(R.color.red);
			
			ivUserPhoto = (ImageView) findViewById(R.id.iv_user_photo);
			btnSubmitChange = (Button) findViewById(R.id.btn_submit_change);
			btnCancelChange = (Button) findViewById(R.id.btn_cancel_change);
			btnChangePassword = (Button) findViewById(R.id.btn_change_password);
			
			etNickname = (EditText) findViewById(R.id.et_nickname);
			etEmail = (EditText) findViewById(R.id.et_email);
			etKakaotalk = (EditText) findViewById(R.id.et_kakaotalk);
			etStatus = (EditText) findViewById(R.id.et_status);
			etOldPassword = (EditText) findViewById(R.id.et_old_password);
			etNewPassword = (EditText) findViewById(R.id.et_new_password);
			etNewPasswordRe = (EditText) findViewById(R.id.et_new_password_re);
			tvValidNickname = (TextView) findViewById(R.id.tv_valid_nickname);

			currentUser = Authenticator.getUser();
			profile = currentUser.getProfile();
			
			etNickname.setText(currentUser.getNickname());
			etEmail.setText(profile.getEmail());
			etKakaotalk.setText(profile.getKakaotalk());
			etStatus.setText(profile.getStatusMessage());
			
			kakaotalkTextWatcher = new SimpleTextWatcher(profile.getKakaotalk());
			emailTextWatcher = new SimpleTextWatcher(profile.getEmail());
			statusTextWatcher = new SimpleTextWatcher(profile.getStatusMessage());
			etNickname.addTextChangedListener(nicknameChangedListener);
			etEmail.addTextChangedListener(emailTextWatcher);
			etKakaotalk.addTextChangedListener(kakaotalkTextWatcher);
			etStatus.addTextChangedListener(statusTextWatcher);
			
			ivUserPhoto.setOnClickListener(photoPickerClickListener);
			btnSubmitChange.setOnClickListener(finishClickListener);
			btnCancelChange.setOnClickListener(finishClickListener);
			btnChangePassword.setOnClickListener(changePasswordClickListener);
			
			ImageHelper.displayPhoto(currentUser, ivUserPhoto);
			
			try {
				File file = File.createTempFile("scaled_image", null, getCacheDir());
				tempUri = Uri.fromFile(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			finish();
		}
	}
	
	private static class SimpleTextWatcher implements TextWatcher {
		
		private String initialString;
		private boolean hasStringChanged = false;
		
		public SimpleTextWatcher(String initialString) {
			if (initialString != null) {
				this.initialString = initialString;
			} else {
				this.initialString = "";
			}
		}

		@Override
		public void afterTextChanged(Editable s) { }

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (!initialString.equals(s.toString())) {
				hasStringChanged = true;
			} else {
				hasStringChanged = false;
			}
		}
		
		public boolean hasStringChanged() {
			return hasStringChanged;
		}
		
	}
	
	private TextWatcher nicknameChangedListener = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			handler.removeCallbacksAndMessages(null);
			
			String nickname = s.toString();
			tvValidNickname.setTextColor(colorPrimary);
			tvValidNickname.setText("잠시만 기다려주세요..");
			isValidNickname = false;
			
			if (nickname.equals(currentUser.getNickname())) {
				tvValidNickname.setText("");
				isValidNickname = false;
			} else if (nickname.length() >= 2) {
				Runnable r = new CheckNicknameRunnable(nickname);
				
				handler.postDelayed(r, 1000);
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		
		@Override
		public void afterTextChanged(Editable s) {}
		
	};
	
	private class CheckNicknameRunnable implements Runnable {
		
		private String nickname;

		public CheckNicknameRunnable(String nickname) {
			this.nickname = nickname;
		}
		
		@Override
		public void run() {
			UrlBuilder urlBuilder = new UrlBuilder();
			String url = urlBuilder.s("users").p("nickname", nickname).toString();
			lastInputNickname = nickname;
			
			JsonObjectRequest request = new JsonObjectRequest(
					url, null,
					new OnVolleyWeakResponse<ProfileEditActivity, JSONObject>(ProfileEditActivity.this, "onNicknameFound"),
					new OnVolleyWeakError<ProfileEditActivity>(ProfileEditActivity.this, "onNicknameNotFound")
			);
			
			RequestQueue queue = ((App) getApplicationContext()).getQueueInstance();
			queue.add(request);
		}
		
	}
	
	public void onNicknameFound(JSONObject response) {
		turnOffValidView();
	}
	
	public void onNicknameNotFound() {
		if (lastInputNickname.equals(etNickname.getText().toString())) {
			turnOnValidView();
		}
	}
	
	public String getCurrentInputNickname() {
		return etNickname.getText().toString();
	}
	
	public void turnOnValidView() {
		tvValidNickname.setTextColor(colorPrimary);
		tvValidNickname.setText("사용하셔도 좋은 닉네임입니다.");
		isValidNickname = true;
	}
	
	public void turnOffValidView() {
		tvValidNickname.setTextColor(colorRed);
		tvValidNickname.setText("사용하실 수 없는 닉네임입니다.");
		isValidNickname = false;
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
		}
	}
	
	private OnClickListener photoPickerClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent photoPickerIntent = new Intent();
			photoPickerIntent.setType("image/*");
			photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
			
			Intent takePickerIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			takePickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
			Intent chooserIntent = Intent.createChooser(photoPickerIntent, "Select or take a new Picture");
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { takePickerIntent });
			
			startActivityForResult(chooserIntent, R_CODE_PHOTO_PICKER);
		}
	};
	
	private OnClickListener finishClickListener = new OnClickListener() {
		
		private boolean changedProfile;
		
		@Override
		public void onClick(View v) {
			Intent intent = getIntent();
			
			switch (v.getId()) {
			case R.id.btn_submit_change:
				Gson gson = Utility.getGsonInstance();
				
//				intent.putExtra(UserHomeFragment.INTENT_IS_EDIT_PHOTO, isPhotoChange);
				
				if (isValidNickname) {
//					intent.putExtra(UserHomeFragment.INTENT_NICKNAME, etNickname.getText().toString());
				}
				
				String email = etEmail.getText().toString();
				if (emailTextWatcher.hasStringChanged() && email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
					profile.setEmail(email);
					changedProfile = true;
				}
				
				String kakaotalk = etKakaotalk.getText().toString();
				if (kakaotalkTextWatcher.hasStringChanged()) {
					profile.setKakaotalk(kakaotalk);
					changedProfile = true;
				}
				
				String statusMessage = etStatus.getText().toString();
				if (statusTextWatcher.hasStringChanged()) {
					profile.setStatusMessage(statusMessage);
					changedProfile = true;
				}
				
				if (changedProfile) {
//					intent.putExtra(UserHomeFragment.INTENT_PROFILE, gson.toJson(profile, Profile.class));
				}
				
				setResult(RESULT_OK, intent);
				
				break;

			case R.id.btn_cancel_change:
				setResult(RESULT_CANCELED, intent);
				
				break;
			}
			
			finish();
		}
	};
	
	private OnClickListener changePasswordClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String oldPassword = etOldPassword.getText().toString();
			String newPassword = etNewPassword.getText().toString();
			String newPasswordRe = etNewPasswordRe.getText().toString();
			
			if (oldPassword.length() >= 4 && newPassword.length() >= 4 && newPasswordRe.length() >= 4) {
				if (newPassword.equals(newPasswordRe)) {
					if (!oldPassword.equals(newPassword)) {
						JSONObject message = new JSONObject();
						try {
							Encryption encryption = new Encryption();
							message.put("old_password", encryption.getSha512Convert(oldPassword));
							message.put("new_password", newPassword);
							
							UrlBuilder urlBuilder = new UrlBuilder();
							String url = urlBuilder.s("users").toString();
							JSONObjectRequest request = new JSONObjectRequest(
									Method.PUT, url, message,
									new OnVolleyWeakResponse<ProfileEditActivity, JSONObject>(ProfileEditActivity.this, "onChangePasswordSuccess"),
									new OnVolleyWeakError<ProfileEditActivity>(ProfileEditActivity.this, "onChangePasswordError")
							);
							
							RequestQueue queue = ((App) getApplicationContext()).getQueueInstance();
							queue.add(request);
							
//							ProfileEditActivity.this.showProgressDialog();
						} catch (JSONException e) {
							// unhandled json exception
						}
					} else {
						Toast.makeText(ProfileEditActivity.this, getString(R.string.t_new_password_must_changed), Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(ProfileEditActivity.this, getString(R.string.t_password_confirm_failed), Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(ProfileEditActivity.this, getString(R.string.t_password_length_policy), Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	public void onChangePasswordSuccess(JSONObject response) {
		try {
			Authenticator auth = new Authenticator();
			auth.update(response.getString("oauth-token"));
			
			etOldPassword.setText("");
			etNewPassword.setText("");
			etNewPasswordRe.setText("");
			
			Toast.makeText(ProfileEditActivity.this, getString(R.string.t_password_has_changed), Toast.LENGTH_SHORT).show();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
//			dismissProgressDialog();
		}
	}
	
	public void onChangePasswordError() {
		Toast.makeText(ProfileEditActivity.this, getString(R.string.t_wrong_password), Toast.LENGTH_SHORT).show();
		
//		dismissProgressDialog();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case R_CODE_PHOTO_PICKER:
			if (resultCode == Activity.RESULT_OK) {
				try {
					ResizeAsyncTask asyncTask = new ResizeAsyncTask();
					asyncTask.setImageView(ivUserPhoto);
					
					Uri selectedImage = data != null ? data.getData() : tempUri;
					InputStream imageStream = getContentResolver().openInputStream(selectedImage);
					asyncTask.execute(imageStream);
					
					isPhotoChange = true;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			
			break;
		}
	}
	
	/*
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case R_CODE_EDIT_PROFILE:
			if (resultCode == Activity.RESULT_OK) {
				try {
					boolean hasPhotoChanged = data.getBooleanExtra(INTENT_IS_EDIT_PHOTO, false);
					String nickname = data.getStringExtra(INTENT_NICKNAME);
					String profileInJson = data.getStringExtra(INTENT_PROFILE);
//					MainActivity home = (MainActivity) getActivity();
					currentUser = Authenticator.getUser();
					
					if (hasPhotoChanged) {
						UploadManager manager = new UploadManager();
//						manager.start(
//								getActivity(), FileManager.get(FileManager.TEMP),
//								"user_photo", currentUser.getUsername() + Model.SUFFIX_JPG, "image/jpeg",
//								photoUploadCompleteListener
//						);
					}
					
					if (nickname != null) {
						currentUser.setNickname(nickname);
						
						tvNickname.setText(nickname);
						
//						if (home != null) {
//							home.updateNickname(nickname);
//						}
						
						requestUpdateNickname(nickname);
					}
					
					if (profileInJson != null) {
						Gson gson = Utility.getGsonInstance();
						Profile profile = gson.fromJson(profileInJson, Profile.class);
						
						currentUser.setProfile(profile);
						
						displayStatusMessage(tvUserStatus, profile.getStatusMessage());
						requestUpdateProfile(profileInJson);
					}
					
					Authenticator auth = new Authenticator();
					auth.update(currentUser);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			break;
		}
	}
	
	private OnCompleteListener photoUploadCompleteListener = new OnCompleteListener() {
		
		@Override
		public void done(Exception e) {
			if (getActivity() != null) {
				if (e == null) {
					try {
						String photoUrl = Model.STORAGE_HOST + Model.STORAGE_USER + currentUser.getUsername() + Model.SUFFIX_JPG; 
						JSONObject message = new JSONObject();
						message.put("main_photo_url", photoUrl);
						
						UrlBuilder urlBuilder = new UrlBuilder();
						String url = urlBuilder.s("users").toString();
						OAuthJsonObjectRequest request = new OAuthJsonObjectRequest(
								Method.PUT, url, message,
								new OnVolleyWeakResponse<UserHomeFragment, JSONObject>(UserHomeFragment.this, "onDataUploadSuccess", User.class),
								new OnVolleyWeakError<UserHomeFragment>(UserHomeFragment.this, "onUploadError")
								);
						
						RequestQueue queue = ((App) getActivity().getApplicationContext()).getQueueInstance();
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
	
	public void onDataUploadSuccess(User user) {
//		try {
//			FileUtils.copyFile(FileManager.get(FileManager.TEMP), FileManager.get(FileManager.USER_PHOTO));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		currentUser.setPhotoUrl(user.getPhotoUrl(), user.getPhotoUpdatedAt());
		Authenticator auth = new Authenticator();
		auth.update(currentUser);
	}
	
	private void onUploadError() {
		Toast.makeText(getActivity(), getString(R.string.t_upload_failed), Toast.LENGTH_SHORT).show();
	}
	
	private void requestUpdateNickname(String nickname) {
		try {
			UrlBuilder urlBuilder = new UrlBuilder();
			String url = urlBuilder.s("users").toString();
			
			JSONObject message = new JSONObject();
			message.put("nickname", nickname);
			
			OAuthJustRequest request = new OAuthJustRequest(Method.PUT, url, message);
			requestQueue.add(request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void requestUpdateProfile(String profileInJson) {
		try {
			UrlBuilder urlBuilder = new UrlBuilder();
			String url = urlBuilder.s("profile").toString();
			JSONObject message = new JSONObject(profileInJson);
			
			OAuthJustRequest request = new OAuthJustRequest(Method.PUT, url, message);
			requestQueue.add(request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	*/

}
