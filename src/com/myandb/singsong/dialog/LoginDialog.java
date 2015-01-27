package com.myandb.singsong.dialog;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Session;
import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.myandb.singsong.GCMIntentService;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.dialog.JoinDialog.OnJoinCompleteListener;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.net.JSONObjectSuccessListener;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.secure.Encryption;
import com.myandb.singsong.util.Utility;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.Permission.Type;
import com.sromku.simple.fb.listeners.OnLoginListener;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginDialog extends BaseDialog {
	
	private EditText etUsername;
	private EditText etPassword;
	private Button btnLogin;
	private Button btnToJoin;
	private Button btnFacebook;
	private TextView tvFindPassword;
	private JoinDialog joinDialog;
	private SimpleFacebook simpleFacebook;
	private Activity activity;

	@Override
	protected void initialize(Activity activity) {
		setProgressDialogMessage(getString(R.string.progress_logining));
		this.activity = activity;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		btnLogin = (Button) view.findViewById(R.id.btn_login);
		btnToJoin = (Button) view.findViewById(R.id.btn_to_join);
		btnFacebook = (Button) view.findViewById(R.id.btn_facebook);
		etUsername = (EditText) view.findViewById(R.id.et_user_username);
		etPassword = (EditText) view.findViewById(R.id.et_user_password);
		tvFindPassword = (TextView) view.findViewById(R.id.tv_find_password);
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
		return R.layout.dialog_login;
	}

	@Override
	protected void setupViews() {
		btnLogin.setOnClickListener(loginClickListener);
		btnFacebook.setOnClickListener(facebookLoginClickListener);
		btnToJoin.setOnClickListener(toJoinClickListener);
		
		tvFindPassword.setMovementMethod(LinkMovementMethod.getInstance());
		tvFindPassword.setText(getFindPasswordHtml());
	}
	
	private Spanned getFindPasswordHtml() {
		UrlBuilder urlBuilder = new UrlBuilder();
		String findHtml = "비밀번호가 기억이 안나시나요?<br/>";
		findHtml += Utility.getHtmlAnchor(urlBuilder.s("w").s("find_password").toString(), "비밀번호 찾기");
		return Html.fromHtml(findHtml);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		simpleFacebook = SimpleFacebook.getInstance(getActivity());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		simpleFacebook.onActivityResult(getActivity(), requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	private View.OnClickListener facebookLoginClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
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
					loginByFacebook(session.getAccessToken());
				}
			});
		}
	};
	
	private View.OnClickListener toJoinClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showJoinDialog();
		}
	};
	
	private void showJoinDialog() {
		if (joinDialog == null) {
			joinDialog = new JoinDialog();
			joinDialog.setOnJoinCompleteListener(new OnJoinCompleteListener() {
				
				@Override
				public void onJoin() {
					onLoginComplete();
				}
			});
		}
		joinDialog.show(getChildFragmentManager(), "");
	}

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
		registerGcm();
		dismissProgressDialog();
		dismiss();
	}
	
	private void registerGcm() {
		try {
			GCMRegistrar.checkDevice(activity);
			GCMRegistrar.checkManifest(activity);
			final String registrationId = GCMRegistrar.getRegistrationId(activity);
			
			if ("".equals(registrationId)) {
				GCMRegistrar.register(activity, GCMIntentService.PROJECT_ID);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Device does not have package com.google.android.gsf
			// This will not happened
		}
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
