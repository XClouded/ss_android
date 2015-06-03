package com.myandb.singsong.dialog;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Session;
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
import com.myandb.singsong.net.MelonResponseHooker;
import com.myandb.singsong.net.MelonResponseHooker.MelonResponseException;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.secure.MelOnAccountManager;
import com.myandb.singsong.secure.MelOnAccountManager.EasyLoginAccount;
import com.myandb.singsong.util.Utility;
import com.sromku.simple.fb.Permission.Type;
import com.sromku.simple.fb.listeners.OnLoginListener;

import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AuthenticationDialog extends BaseDialog {
	
	public enum AuthenticationType {
		
		MELON_EASY_LOGIN,
		
		MELON_PASSWORD_LOGIN,
		
		MELON_LOGIN_SINGSONG_INTEGRATION,
		
		SINGSONG_AUTHENTICATE,
		
		SINGSONG_AUTHENTICATE_MELON_INTEGRATION
		
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
	
	private Button btnLoginUsingPassword;
	private Button btnFacebook;
	private Button btnAuthentication;
	private Button btnAuthenticateSingSong;
	
	private EditText etUsername;
	private EditText etPassword;
	
	private ImageView ivWhetherAddEasyLogin; 
	
	private View vEasyLoginWrapper;
	private View vEasyLoginList;
	private View vEasyLoginGuideWrapper;
	private View vIntegratedAuthenticationWrapper;
	private View vFacebookWrapper;
	private View vSingSongAuthenticationWrapper;
	
	private Activity activity;
	private AuthenticationType type;
	private User authenticatedSingSongUser;
	private MelOnAccountManager accountManger;
	private boolean addEasyLogin;
	private boolean loggedInUsingPassword;
	private String loggedInUsername;

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		type = (AuthenticationType) bundle.getSerializable(AuthenticationType.class.getName());
	}

	@Override
	protected void initialize(Activity activity) {
		setProgressDialogMessage(getString(R.string.progress_logining));
		
		this.activity = activity;
		
		accountManger = new MelOnAccountManager(activity);
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
		
		btnLoginUsingPassword = (Button) view.findViewById(R.id.btn_login_using_password);
		btnFacebook = (Button) view.findViewById(R.id.btn_facebook);
		btnAuthentication = (Button) view.findViewById(R.id.btn_authentication);
		btnAuthenticateSingSong = (Button) view.findViewById(R.id.btn_authenticate_singsong);
		
		etUsername = (EditText) view.findViewById(R.id.et_username);
		etPassword = (EditText) view.findViewById(R.id.et_password);
		
		ivWhetherAddEasyLogin = (ImageView) view.findViewById(R.id.iv_whether_add_easy_login);
		
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
		
		enableAddEasyLogin(true);
		
		tvFindMelonUsername.setOnClickListener(webLinkClickListener);
		tvFindMelonPassword.setOnClickListener(webLinkClickListener);
		tvFindSingSongPassword.setOnClickListener(webLinkClickListener);
		tvJoinMelon.setOnClickListener(webLinkClickListener);
		btnLoginUsingPassword.setOnClickListener(showLoginFormUsingPasswordClickListener);
		btnAuthenticateSingSong.setOnClickListener(showSingSongAuthenticationFormClickListener);
		btnAuthentication.setOnClickListener(authenticateClickListener);
		btnFacebook.setOnClickListener(facebookClickListener);
		tvWhetherAddEasyLogin.setOnClickListener(enableAddEasyLoginClickListener);
		ivWhetherAddEasyLogin.setOnClickListener(enableAddEasyLoginClickListener);
		
		if (type == null) {
			if (accountManger.hasMelOnAccounts()) {
				setupViewsByType(AuthenticationType.MELON_EASY_LOGIN);
			} else {
				setupViewsByType(AuthenticationType.MELON_PASSWORD_LOGIN);
			}
		} else {
			setupViewsByType(type);
		}
	}
	
	private void listEasyLoginAccounts() {
		new ListEasyLoginTask().execute(accountManger);
	}
	
	private class ListEasyLoginTask extends AsyncTask<MelOnAccountManager, Integer, List<EasyLoginAccount>> {

		@Override
		protected List<EasyLoginAccount> doInBackground(MelOnAccountManager... params) {
			MelOnAccountManager manager = params[0];
			if (manager == null) {
				return null;
			}
			return manager.getEasyLoginAccounts();
		}

		@Override
		protected void onPostExecute(List<EasyLoginAccount> result) {
			super.onPostExecute(result);
			
			if (result != null) {
				for (EasyLoginAccount account : result) {
					Button button = new Button(getActivity());
					button.setText(account.getUsername());
					button.setTag(account);
					button.setOnClickListener(easyLoginClickListener);
					((ViewGroup) vEasyLoginList).addView(button);
				}
			}
		}
		
	}
	
	private void setupViewsByType(AuthenticationType type) {
		if (type == null) {
			return;
		}
		
		clearTextFromAllEditText();
		
		this.type = type;
		switch (type) {
		case MELON_EASY_LOGIN:
			setViewsVisible(vEasyLoginWrapper, vSingSongAuthenticationWrapper);
			setViewsGone(tvSubtitle, tvDescription, tvSingSongUsernameGuide, vIntegratedAuthenticationWrapper);
			break;
			
		case MELON_PASSWORD_LOGIN:
			setViewsVisible(vIntegratedAuthenticationWrapper, vEasyLoginGuideWrapper,
					tvFindMelonUsername, tvFindMelonPassword, vSingSongAuthenticationWrapper);
			setViewsGone(tvSubtitle, tvDescription, tvSingSongUsernameGuide, vEasyLoginWrapper, vFacebookWrapper,
					tvFindSingSongPassword);
			tvInputAuthenticationTitle.setText("멜론 아이디/비밀번호를 입력해주세요.");
			btnAuthentication.setText("로그인");
			etUsername.setHint("멜론 아이디");
			etPassword.setHint("멜론 비밀번호");
			break;
			
		case MELON_LOGIN_SINGSONG_INTEGRATION:
			setViewsVisible(tvDescription, vIntegratedAuthenticationWrapper, vFacebookWrapper,
					tvFindSingSongPassword);
			setViewsGone(tvSubtitle, tvSingSongUsernameGuide, vEasyLoginWrapper, vEasyLoginGuideWrapper,
					tvFindMelonUsername, tvFindMelonPassword, vSingSongAuthenticationWrapper);
			tvInputAuthenticationTitle.setText("콜라보 노래방 아이디/비밀번호를 입력해주세요.");
			btnAuthentication.setText("콜라보 회원인증");
			etUsername.setHint("이메일 또는 콜라보 아이디");
			etPassword.setHint("콜라보 비밀번호");
			break;
			
		case SINGSONG_AUTHENTICATE:
			setViewsVisible(tvSubtitle, tvDescription, vIntegratedAuthenticationWrapper, vFacebookWrapper,
					tvFindSingSongPassword);
			setViewsGone(tvSingSongUsernameGuide, vEasyLoginWrapper, vEasyLoginGuideWrapper,
					tvFindMelonUsername, tvFindMelonPassword, vSingSongAuthenticationWrapper);
			tvInputAuthenticationTitle.setText("콜라보 노래방 아이디/비밀번호를 입력해주세요.");
			btnAuthentication.setText("콜라보 회원인증");
			etUsername.setHint("이메일 또는 콜라보 아이디");
			etPassword.setHint("콜라보 비밀번호");
			break;
			
		case SINGSONG_AUTHENTICATE_MELON_INTEGRATION:
			setViewsVisible(tvDescription, tvSingSongUsernameGuide, vIntegratedAuthenticationWrapper, 
					tvFindMelonUsername, tvFindMelonPassword);
			setViewsGone(tvSubtitle, vEasyLoginWrapper, vEasyLoginGuideWrapper,
					tvFindSingSongPassword, vSingSongAuthenticationWrapper, vFacebookWrapper);
			tvInputAuthenticationTitle.setText("멜론 아이디/비밀번호를 입력해주세요.");
			btnAuthentication.setText("멜론 회원인증");
			etUsername.setHint("멜론 아이디");
			etPassword.setHint("멜론 비밀번호");
			break;

		default:
			break;
		}
		
		dismissProgressDialog();
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
	
	private OnClickListener showSingSongAuthenticationFormClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			setupViewsByType(AuthenticationType.SINGSONG_AUTHENTICATE);
		}
	};
	
	private OnClickListener showLoginFormUsingPasswordClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			setupViewsByType(AuthenticationType.MELON_PASSWORD_LOGIN);
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
	
	private OnClickListener authenticateClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showProgressDialog();
			
			String username = etUsername.getText().toString();
			String password = etPassword.getText().toString();
			
			switch (type) {
			default:
			case MELON_EASY_LOGIN:
				dismissProgressDialog();
				return;

			case MELON_PASSWORD_LOGIN:
				loginUsingPassword(username, password);
				break;
				
			case MELON_LOGIN_SINGSONG_INTEGRATION:
				mergeSingSongAccountIntoMelonAccount(username, password);
				break;
				
			case SINGSONG_AUTHENTICATE:
				authenticateSingSong(username, password);
				break;
				
			case SINGSONG_AUTHENTICATE_MELON_INTEGRATION:
				integrateSingSongAccountWithMelonAccount(username, password);
				break;
			}
		}
	};
	
	private boolean isEnabledAddEasyLogin() {
		return addEasyLogin;
	}
	
	private void enableAddEasyLogin(boolean enable) {
		this.addEasyLogin = enable;
		if (enable) {
			ivWhetherAddEasyLogin.setImageResource(R.drawable.ic_check);
		} else {
			ivWhetherAddEasyLogin.setImageDrawable(null);
		}
	}
	
	private OnClickListener enableAddEasyLoginClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			enableAddEasyLogin(!addEasyLogin);
		}
	};
	
	private OnClickListener easyLoginClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showProgressDialog();
			
			EasyLoginAccount account = (EasyLoginAccount) v.getTag();
			if (account != null) {
				String username = account.getUsername();
				String token = account.getAuthToken();
				
				loginUsingToken(username, token);
			}
		}
	};
	
	private void loginUsingPassword(String username, String password) {
		try {
			loggedInUsingPassword = true;
			loggedInUsername = username;
			
			int loginType = Authenticator.LOGIN_TYPE_PASSWORD;
			if (isEnabledAddEasyLogin()) {
				loginType = Authenticator.LOGIN_TYPE_PASSWORD_EASY_LOGIN;
			}
			
			JSONObject message = new JSONObject();
			message.put("memberId", username);
			message.put("memberPwd", password);
			message.put("loginType", loginType);
			login(message);
		} catch (Exception e) {
			e.printStackTrace();
			onLoginError();
		}
	}
	
	private void loginUsingToken(String username, String token) {
		try {
			loggedInUsingPassword = false;
			loggedInUsername = username;
			
			JSONObject message = new JSONObject();
			message.put("memberId", username);
			message.put("token", token);
			message.put("loginType", Authenticator.LOGIN_TYPE_TOKEN);
			login(message);
		} catch (Exception e) {
			e.printStackTrace();
			onLoginError();
		}
	}
	
	private void login(JSONObject loginData) {
		try {
			loginData.put("purpose", Authenticator.LOGIN_PURPOSE_LOGIN);
			JSONObjectRequest request = new JSONObjectRequest(
					"melon/login/melon", null, loginData,
					new JSONObjectSuccessListener(this, "onLoginSuccess"), 
					new JSONErrorListener(this, "onLoginError"));
			addRequest(request);
		} catch (Exception e) {
			e.printStackTrace();
			onLoginError();
		}
	}
	
	public void onLoginSuccess(JSONObject response) {
		try {
			MelonResponseHooker.hook(getActivity(), getChildFragmentManager(), response);
			
			User user = extractUserFromResponse(response);
			String token = extractTokenFromResponse(response);
			
			saveUserOnLocal(user, token);
			onLoginComplete();
		} catch (MelonResponseException e) {
			e.printStackTrace();
			removeUserOnLocal();
			dismissProgressDialog();
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
		if (isEnabledAddEasyLogin() && loggedInUsingPassword) {
			try {
				accountManger.addMelOnAccount(loggedInUsername, token);
			} catch (Exception e) {
				Toast.makeText(getActivity(), "간편로그인은 3개까지 지원됩니다", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}
	}
	
	private void removeUserOnLocal() {
		new Authenticator().logout();
		if (!loggedInUsingPassword) {
			new RemoveEasyLoginTask().execute(loggedInUsername);
		}
	}
	
	private class RemoveEasyLoginTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			String userId = params[0];
			try {
				accountManger.removeMelOnAccount(userId);
				return userId;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String userId) {
			super.onPostExecute(userId);
			
			if (userId != null) {
				int childCount = ((ViewGroup) vEasyLoginList).getChildCount();
				for (int i = 0; i < childCount; i++) {
					View child = ((ViewGroup) vEasyLoginList).getChildAt(i);
					EasyLoginAccount account = (EasyLoginAccount) child.getTag();
					if (account != null && account.getUsername().equalsIgnoreCase(userId)) {
						((ViewGroup) vEasyLoginList).removeView(child);
						break;
					}
				}
			}
		}
		
	}

	public void onLoginComplete() {
		if (activity instanceof RootActivity) {
			((RootActivity) activity).updateDrawer();
		}
		
		GCMIntentService.register(activity);
		
		if (Authenticator.getUser().isSingSongIntegrated()) {
			dismissProgressDialog();
			dismiss();
		} else {
			setupViewsByType(AuthenticationType.MELON_LOGIN_SINGSONG_INTEGRATION);
		}
	}
	
	public void onLoginError() {
		removeUserOnLocal();
		dismissProgressDialog();
		makeToast(R.string.t_alert_login_failed);
	}
	
	private void clearTextFromAllEditText() {
		etUsername.setText("");
		etPassword.setText("");
	}
	
	private void authenticateSingSong(String username, String password) {
		try {
			JSONObject message = new JSONObject();
			message.put("memberId", username);
			message.put("memberPwd", password);
			message.put("loginType", Authenticator.SINGSONG_LOGIN_TYPE_PASSWORD);
			authenticateSingSong(message);
		} catch (Exception e) {
			e.printStackTrace();
			onAuthenticateError();
		}
	}
	
	private void authenticateSingSong(String facebookAccessToken) {
		try {
			JSONObject message = new JSONObject();
			message.put("facebook_token", facebookAccessToken);
			message.put("loginType", Authenticator.SINGSONG_LOGIN_TYPE_FACEBOOK);
			authenticateSingSong(message);
		} catch (Exception e) {
			e.printStackTrace();
			onAuthenticateError();
		}
	}
	
	private void authenticateSingSong(JSONObject authenticateData) {
		try {
			authenticateData.put("purpose", Authenticator.LOGIN_PURPOSE_LOGIN);
			JSONObjectRequest request = new JSONObjectRequest(
					"melon/login/collabo", null, authenticateData,
					new JSONObjectSuccessListener(this, "onAuthenticateSuccess"), 
					new JSONErrorListener(this, "onAuthenticateError"));
			addRequest(request);
		} catch (Exception e) {
			e.printStackTrace();
			onAuthenticateError();
		}
	}
	
	public void onAuthenticateSuccess(JSONObject response) {
		try {
			MelonResponseHooker.hook(getActivity(), getChildFragmentManager(), response);
			
			User user = extractUserFromResponse(response);
			if (user.isSingSongIntegrated()) {
				dismissProgressDialog();
				makeToast("이미 통합된 아이디입니다.");
			} else {
				authenticatedSingSongUser = user;
				setupViewsByType(AuthenticationType.SINGSONG_AUTHENTICATE_MELON_INTEGRATION);
			}
		} catch (JSONException e) {
			onLoginError();
		} catch (JsonSyntaxException e) {
			onLoginError();
		} catch (MelonResponseException e) {
			e.printStackTrace();
			dismissProgressDialog();
		}
	}
	
	public void onAuthenticateError() {
		dismissProgressDialog();
		makeToast(R.string.t_alert_login_failed);
	}
	
	public void onIntegrateError() {
		onAuthenticateError();
	}
	
	private void mergeSingSongAccountIntoMelonAccount(String username, String password) {
		String melonMemberKey = Authenticator.getUser().getMelonId();
		
		try {
			JSONObject message = new JSONObject();
			message.put("memberId", username);
			message.put("memberPwd", password);
			message.put("memberKey", melonMemberKey);
			message.put("loginType", Authenticator.SINGSONG_LOGIN_TYPE_PASSWORD);
			message.put("purpose", Authenticator.LOGIN_PURPOSE_INTEGRATE);
			
			JSONObjectRequest request = new JSONObjectRequest(
					"melon/login/collabo", null, message,
					new JSONObjectSuccessListener(this, "onIntegrateSuccess"), 
					new JSONErrorListener(this, "onIntegrateError"));
			addRequest(request);
		} catch (Exception e) {
			e.printStackTrace();
			onIntegrateError();
		}
	}
	
	private void mergeSingSongAccountIntoMelonAccount(String facebookAccessToken) {
		String melonToken = Authenticator.getAccessToken();
		
		try {
			JSONObject message = new JSONObject();
			message.put("memberId", Authenticator.getUser().getId());
			message.put("token", melonToken);
			message.put("facebook_token", facebookAccessToken);
			message.put("loginType", Authenticator.LOGIN_TYPE_TOKEN);
			message.put("purpose", Authenticator.LOGIN_PURPOSE_INTEGRATE);
			
			JSONObjectRequest request = new JSONObjectRequest(
					"melon/login/melon", null, message,
					new JSONObjectSuccessListener(this, "onIntegrateSuccess"), 
					new JSONErrorListener(this, "onIntegrateError"));
			addRequest(request);
		} catch (Exception e) {
			e.printStackTrace();
			onIntegrateError();
		}
	}
	
	private void integrateSingSongAccountWithMelonAccount(String username, String password) {
		try {
			JSONObject message = new JSONObject();
			message.put("memberId", username);
			message.put("memberPwd", password);
			message.put("collaboKey", authenticatedSingSongUser.getId());
			message.put("loginType", Authenticator.LOGIN_TYPE_PASSWORD);
			message.put("purpose", Authenticator.LOGIN_PURPOSE_INTEGRATE);
			
			JSONObjectRequest request = new JSONObjectRequest(
					"melon/login/melon", null, message,
					new JSONObjectSuccessListener(this, "onIntegrateSuccess"), 
					new JSONErrorListener(this, "onIntegrateError"));
			addRequest(request);
		} catch (Exception e) {
			e.printStackTrace();
			onIntegrateError();
		}
	}
	
	public void onIntegrateSuccess(JSONObject response) {
		try {
			dismissProgressDialog();
			
			MelonResponseHooker.hook(getActivity(), getChildFragmentManager(), response);
			
			showIntegrateSuccessDialog();
		} catch (MelonResponseException e) {
			e.printStackTrace();
			dismissProgressDialog();
		}
	}
	
	private void showIntegrateSuccessDialog() {
		dismiss();
	}

	private View.OnClickListener facebookClickListener = new View.OnClickListener() {
		
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
					
					Session session = getSimpleFacebook().getSession();
					if (session != null) {
						switch (type) {
						case MELON_LOGIN_SINGSONG_INTEGRATION:
							mergeSingSongAccountIntoMelonAccount(session.getAccessToken());
							break;
							
						case SINGSONG_AUTHENTICATE:
							authenticateSingSong(session.getAccessToken());
							break;
							
						default:
							return;
						}
					}
				}
			});
		}
	};
	
	@Override
	public void dismiss() {
		super.dismiss();
		clearTextFromAllEditText();
	}

}
