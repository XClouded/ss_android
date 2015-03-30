package com.myandb.singsong.dialog;

import org.json.JSONException;
import org.json.JSONObject;

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
import com.sromku.simple.fb.Permission.Type;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.entities.Profile.Properties;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
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
	private FacebookJoinDialog facebookJoinDialog;
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
		if (facebookJoinDialog == null) {
			facebookJoinDialog = new FacebookJoinDialog();
			facebookJoinDialog.setOnJoinCompleteListener(new OnJoinCompleteListener() {
				
				@Override
				public void onJoin() {
					onLoginComplete();
				}
			});
		}
		String accessToken = getSimpleFacebook().getSession().getAccessToken();
		Bundle bundle = new Bundle();
		bundle.putString(FacebookJoinDialog.EXTRA_FACEBOOK_TOKEN, accessToken);
		facebookJoinDialog.show(getChildFragmentManager(), "");
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN) {
			getChildFragmentManager().executePendingTransactions();
		}
	}
	
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
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN) {
			getChildFragmentManager().executePendingTransactions();
		}
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
