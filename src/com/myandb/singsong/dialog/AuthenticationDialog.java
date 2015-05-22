package com.myandb.singsong.dialog;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.myandb.singsong.GCMIntentService;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.activity.UpActivity;
import com.myandb.singsong.fragment.BaseFragment;
import com.myandb.singsong.fragment.WebViewFragment;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.net.JSONObjectSuccessListener;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.secure.Encryption;
import com.myandb.singsong.util.Utility;
import com.sromku.simple.fb.Permission.Type;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.entities.Profile.Properties;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AuthenticationDialog extends BaseDialog {
	
	public enum AuthenticationType {
		
		MELON_EASY_LOGIN,
		
		MELON_USERNAME_LOGIN,
		
		MELON_LOGIN_SINGSONG_INTEGRATION,
		
		SINGSONG_LOGIN,
		
		SINGSONG_LOGIN_MELON_INTEGRATION
		
	}
	
	private TextView tvTitle;
	private TextView tvSubtitle;
	private TextView tvDescription;
	private TextView tvSingSongUsernameGuide;
	private TextView tvInputAuthenticationTitle;
	private TextView tvWhetherAddEasyLogin;
	private TextView tvEasyLoginGuide;
	private TextView tvFindMelonUsername;
	private TextView tvFindMelonPassword;
	private TextView tvFindSingSongPassword;
	private TextView tvJoinMelon;
	
	private Button btnLoginWithMelonUsername;
	private Button btnFacebook;
	private Button btnAuthentication;
	private Button btnAuthenticateSingSong;
	
	private EditText etUsername;
	private EditText etPassword;
	
	private View vEasyLoginWrapper;
	private View vEasyLoginList;
	private View vEasyLoginGuideWrapper;
	private View vIntegratedAuthenticationWrapper;
	private View vFacebookWrapper;
	private View vSingSongAuthenticationWrapper;
	
	private Activity activity;
	private AuthenticationType type;

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		type = (AuthenticationType) bundle.getSerializable(AuthenticationType.class.getName());
	}

	@Override
	protected void initialize(Activity activity) {
		setProgressDialogMessage(getString(R.string.progress_logining));
		this.activity = activity;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		tvTitle = (TextView) view.findViewById(R.id.tv_title);
		tvSubtitle = (TextView) view.findViewById(R.id.tv_subtitle);
		tvDescription = (TextView) view.findViewById(R.id.tv_description);
		tvSingSongUsernameGuide = (TextView) view.findViewById(R.id.tv_singsong_username_guide);
		tvInputAuthenticationTitle = (TextView) view.findViewById(R.id.tv_input_authentication_title);
		tvWhetherAddEasyLogin = (TextView) view.findViewById(R.id.tv_whether_add_easy_login);
		tvEasyLoginGuide = (TextView) view.findViewById(R.id.tv_easy_login_guide);
		tvFindMelonUsername = (TextView) view.findViewById(R.id.tv_find_melon_username);
		tvFindMelonPassword = (TextView) view.findViewById(R.id.tv_find_melon_password);
		tvFindSingSongPassword = (TextView) view.findViewById(R.id.tv_find_singsong_password);
		tvJoinMelon = (TextView) view.findViewById(R.id.tv_join_melon);
		
		btnLoginWithMelonUsername = (Button) view.findViewById(R.id.btn_login_with_melon_username);
		btnFacebook = (Button) view.findViewById(R.id.btn_facebook);
		btnAuthentication = (Button) view.findViewById(R.id.btn_authentication);
		btnAuthenticateSingSong = (Button) view.findViewById(R.id.btn_authenticate_singsong);
		
		etUsername = (EditText) view.findViewById(R.id.et_username);
		etPassword = (EditText) view.findViewById(R.id.et_password);
		
		vEasyLoginWrapper = view.findViewById(R.id.ll_easy_login_wrapper);
		vEasyLoginList = view.findViewById(R.id.ll_easy_login_list);
		vEasyLoginGuideWrapper = view.findViewById(R.id.rl_easy_login_guide_wrapper);
		vIntegratedAuthenticationWrapper = view.findViewById(R.id.ll_integrated_authentication_wrapper);
		vFacebookWrapper = view.findViewById(R.id.ll_facebook_wrapper);
		vSingSongAuthenticationWrapper = view.findViewById(R.id.rl_singsong_authentication_wrapper);
	}

	@Override
	protected LayoutParams getWindowLayoutParams() {
		LayoutParams layoutParams = super.getWindowLayoutParams();
		layoutParams.gravity = Gravity.TOP;
		layoutParams.y = 150;
		return layoutParams;
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_authentication;
	}

	@Override
	protected void setupViews() {
		listEasyLoginAccounts();
		
		tvFindMelonUsername.setOnClickListener(webLinkClickListener);
		tvFindMelonPassword.setOnClickListener(webLinkClickListener);
		tvFindSingSongPassword.setOnClickListener(webLinkClickListener);
		tvJoinMelon.setOnClickListener(webLinkClickListener);
		btnLoginWithMelonUsername.setOnClickListener(loginWithMelonUsernameClickListener);
		btnAuthenticateSingSong.setOnClickListener(loginWithSingSongClickListener);
		
		if (type == null) {
			if (hasEasyLogin()) {
				displayViewsByType(AuthenticationType.MELON_EASY_LOGIN);
			} else {
				displayViewsByType(AuthenticationType.MELON_USERNAME_LOGIN);
			}
		} else {
			displayViewsByType(type);
		}
	}
	
	private void listEasyLoginAccounts() {
		
	}
	
	private boolean hasEasyLogin() {
		return true;
	}
	
	private void displayViewsByType(AuthenticationType type) {
		if (type == null) {
			return;
		}
		
		this.type = type;
		switch (type) {
		case MELON_EASY_LOGIN:
			setViewsVisible(vEasyLoginWrapper, vSingSongAuthenticationWrapper);
			setViewsGone(tvSubtitle, tvDescription, tvSingSongUsernameGuide, vIntegratedAuthenticationWrapper);
			break;
			
		case MELON_USERNAME_LOGIN:
			setViewsVisible(vIntegratedAuthenticationWrapper, vEasyLoginGuideWrapper,
					tvFindMelonUsername, tvFindMelonPassword, vSingSongAuthenticationWrapper);
			setViewsGone(tvSubtitle, tvDescription, tvSingSongUsernameGuide, vEasyLoginWrapper, vFacebookWrapper,
					tvFindSingSongPassword);
			tvInputAuthenticationTitle.setText("멜론 아이디/비밀번호를 입력해주세요.");
			btnAuthentication.setText("로그인");
			break;
			
		case MELON_LOGIN_SINGSONG_INTEGRATION:
			setViewsVisible(tvDescription, vIntegratedAuthenticationWrapper, vFacebookWrapper,
					tvFindSingSongPassword);
			setViewsGone(tvSubtitle, tvSingSongUsernameGuide, vEasyLoginWrapper, vEasyLoginGuideWrapper,
					tvFindMelonUsername, tvFindMelonPassword);
			tvInputAuthenticationTitle.setText("콜라보 노래방 아이디/비밀번호를 입력해주세요.");
			btnAuthentication.setText("콜라보 회원인증");
			break;
			
		case SINGSONG_LOGIN:
			setViewsVisible(tvSubtitle, tvDescription, vIntegratedAuthenticationWrapper, vFacebookWrapper,
					tvFindSingSongPassword);
			setViewsGone(tvSingSongUsernameGuide, vEasyLoginWrapper, vEasyLoginGuideWrapper,
					tvFindMelonUsername, tvFindMelonPassword, vSingSongAuthenticationWrapper);
			tvInputAuthenticationTitle.setText("콜라보 노래방 아이디/비밀번호를 입력해주세요.");
			btnAuthentication.setText("콜라보 회원인증");
			break;
			
		case SINGSONG_LOGIN_MELON_INTEGRATION:
			setViewsVisible(tvDescription, tvSingSongUsernameGuide, vIntegratedAuthenticationWrapper, 
					tvFindMelonUsername, tvFindMelonPassword);
			setViewsGone(tvSubtitle, vEasyLoginWrapper, vEasyLoginGuideWrapper,
					tvFindSingSongPassword, vSingSongAuthenticationWrapper);
			tvInputAuthenticationTitle.setText("멜론 아이디/비밀번호를 입력해주세요.");
			btnAuthentication.setText("멜론 회원인증");
			break;

		default:
			break;
		}
	}
	
	private void setViewsVisible(View... views) {
		if (views != null) {
			for (View view : views) {
				view.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private void setViewsGone(View... views) {
		if (views != null) {
			for (View view : views) {
				view.setVisibility(View.GONE);
			}
		}
	}
	
	private OnClickListener loginWithSingSongClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			displayViewsByType(AuthenticationType.SINGSONG_LOGIN);
		}
	};
	
	private OnClickListener loginWithMelonUsernameClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			displayViewsByType(AuthenticationType.MELON_USERNAME_LOGIN);
		}
	};
	
	private OnClickListener webLinkClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String url = "";
			String title = "";
			
			switch (v.getId()) {
			case R.id.tv_find_melon_username:
				url = "https://m.melon.com:4554/muid/search/android2/idsearch_inform.htm";
				title = "아이디 찾기";
				break;
				
			case R.id.tv_find_melon_password:
				url = "https://m.melon.com:4554/muid/search/android2/passwordsearch_inform.htm";
				title = "비밀번호 찾기";
				break;
				
			case R.id.tv_find_singsong_password:
				url = new UrlBuilder().s("w").s("find_password").toString();
				title = "비밀번호 찾기";
				break;
				
			case R.id.tv_join_melon:
				url = "https://m.melon.com:4554/muid/join/android2/stipulationagreement_inform.htm";
				title = "회원가입";
				break;

			default:
				return;
			}
			
			Bundle bundle = new Bundle();
			bundle.putString(BaseFragment.EXTRA_TITLE, title);
			bundle.putString(WebViewFragment.EXTRA_URL, url);
			
			Intent intent = new Intent(getActivity(), UpActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, WebViewFragment.class.getName());
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			
			startActivity(intent);
		}
	};

	private View.OnClickListener facebookLoginClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			getSimpleFacebook().login(new OnLoginListener() {
				
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
					getFacebookProfile();
				}
			});
		}
	};
	
	private void getFacebookProfile() {
		Profile.Properties properties = new Profile.Properties.Builder().add(Properties.ID).build();
		getSimpleFacebook().getProfile(properties, new OnProfileListener() {

			@Override
			public void onComplete(Profile response) {
				super.onComplete(response);
				checkFacebookIdDuplication(response.getId());
			}

			@Override
			public void onException(Throwable throwable) {
				super.onException(throwable);
				onLoginError();
			}

			@Override
			public void onFail(String reason) {
				super.onFail(reason);
				onLoginError();
			}
			
		});
	}
	
	private void checkFacebookIdDuplication(String facebookId) {
		Bundle params = new Bundle();
		params.putString("facebook_id", facebookId);
		JSONObjectRequest request = new JSONObjectRequest(
				"users", params, null,
				new JSONObjectSuccessListener(this, "onFacebookIdFound"),
				new JSONErrorListener(this, "onFacebookIdNotFound")
		);
		addRequest(request);
	}
	
	public void onFacebookIdFound(JSONObject response) {
		dismissProgressDialog();
		String accessToken = getSimpleFacebook().getSession().getAccessToken();
		loginByFacebook(accessToken);
	}
	
	public void onFacebookIdNotFound() {
		dismissProgressDialog();
		
	}
	
	private View.OnClickListener toJoinClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
		}
	};

	private View.OnClickListener loginClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showProgressDialog();
			
			String username = etUsername.getText().toString();
			String password = etPassword.getText().toString();
			loginByEmail(username, password);
		}
	};
	
	private void loginByEmail(String username, String password) {
		try {
			Encryption encryption = new Encryption();
			JSONObject message = new JSONObject();
			message.put("username", username);
			message.put("password", encryption.getSha512Convert(password));
			requestLogin(message);
		} catch (JSONException e) {
			onLoginError();
		} catch (NullPointerException e) {
			onLoginError();
		}
	}
	
	private void loginByFacebook(String token) {
		try {
			Encryption encryption = new Encryption();
			JSONObject message = new JSONObject();
			message.put("facebook_token", token);
			message.put("device_id", encryption.getDeviceId(getActivity()));
			requestLogin(message);		
		} catch (JSONException e) {
			onLoginError();
		} catch (NullPointerException e) {
			onLoginError();
		}
	}
	
	private void requestLogin(JSONObject message) {
		JSONObjectRequest request = new JSONObjectRequest(
				"token", null, message,
				new JSONObjectSuccessListener(this, "onLoginSuccess"),
				new JSONErrorListener(this, "onLoginError")
		);
		addRequest(request);
	}
	
	public void onLoginSuccess(JSONObject response) {
		try {
			User user = extractUserFromResponse(response);
			String token = extractTokenFromResponse(response);
			saveUserOnLocal(user, token);
			onLoginComplete();
		} catch (JSONException e) {
			onLoginError();
		} catch (JsonSyntaxException e) {
			onLoginError();
		}
	}
	
	private User extractUserFromResponse(JSONObject response) throws JSONException {
		Gson gson = Utility.getGsonInstance();
		return gson.fromJson(response.getJSONObject("user").toString(), User.class);
	}
	
	private String extractTokenFromResponse(JSONObject response) throws JSONException {
		return response.getString("oauth-token");
	}
	
	private void saveUserOnLocal(User user, String token) {
		new Authenticator().login(user, token);
	}
	
	private void removeUserOnLocal() {
		new Authenticator().logout();
	}

	public void onLoginComplete() {
		if (activity instanceof RootActivity) {
			((RootActivity) activity).updateDrawer();
		}
		GCMIntentService.register(activity);
		dismissProgressDialog();
		dismiss();
	}
	
	public void onLoginError() {
		dismissProgressDialog();
		makeToast(R.string.t_alert_login_failed);
		removeUserOnLocal();
	}
	
	private void clearTextFromAllEditText() {
		etUsername.setText("");
		etPassword.setText("");
	}
	
	@Override
	public void dismiss() {
		super.dismiss();
		clearTextFromAllEditText();
	}

}
