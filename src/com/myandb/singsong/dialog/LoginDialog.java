package com.myandb.singsong.dialog;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Session.OpenRequest;
import com.facebook.Session.StatusCallback;
import com.facebook.model.GraphUser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.net.JSONObjectSuccessListener;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.secure.Encryption;
import com.myandb.singsong.util.Utility;

import android.app.Activity;
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
	private String facebookToken;
	private RootActivity activity;
	private JoinDialog joinDialog;

	@Override
	protected void initialize(Activity activity) {
		setProgressDialogMessage("로그인 중입니다.");
		
		if (activity instanceof RootActivity) {
			this.activity = (RootActivity) activity;
		}
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
	
	private Session.StatusCallback statusCallback = new StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			if (state.isOpened()) {
				showProgressDialog();
				
				Request.newMeRequest(session, new GetUserCallback(LoginDialog.this)).executeAsync();
				facebookToken = session.getAccessToken();
			}
		}
	};
	
	private static class GetUserCallback implements GraphUserCallback {
		
		private WeakReference<LoginDialog> weakReference;
		
		public GetUserCallback(LoginDialog reference) {
			weakReference = new WeakReference<LoginDialog>(reference);
		}

		@Override
		public void onCompleted(GraphUser user, Response response) { 
			LoginDialog reference = weakReference.get();
			reference.loginByFacebook(user, reference.getFacebookToken());
		}
	}
	
	public String getFacebookToken() {
		return facebookToken;
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
			JSONObject message = new JSONObject();
			Encryption encryption = new Encryption();
			
			message.put("username", username);
			message.put("password", encryption.getSha512Convert(password));
			
			requestLogin(message);
		} catch (JSONException e) {
			onLoginError();
		} catch (NullPointerException e) {
			onLoginError();
		}
	}
	
	private void loginByFacebook(GraphUser user, String token) {
		try {
			JSONObject message = new JSONObject();
			Encryption encryption = new Encryption();
			
			message.put("facebook_token", token);
			message.put("facebook_id", user.getId());
			message.put("facebook_name", user.getName());
			message.put("facebook_photo", "https://graph.facebook.com/" + user.getId() + "/picture?type=large");
			message.put("device_id", encryption.getDeviceId(getActivity()));
			
			Object email = user.asMap().get("email");
			if (email != null) {
				message.put("facebook_email", email.toString());
			}
			
			requestLogin(message);		
		} catch (JSONException e) {
			onLoginError();
		} catch (NullPointerException e) {
			onLoginError();
		}
	}
	
	private void requestLogin(JSONObject message) {
		JSONObjectRequest request = new JSONObjectRequest(
				"token", message,
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
		dismissProgressDialog();
		activity.restartActivity();
	}
	
	public void onLoginError() {
		dismissProgressDialog();
		makeToast(R.string.t_login_failed);
		removeUserOnLocal();
	}
	
	private void clearTextFromAllEditText() {
		etUsername.setText("");
		etPassword.setText("");
	}
	
	private void dismissJoinDialog() {
		if (joinDialog != null) {
			joinDialog.dismiss();
		}
	}
	
	@Override
	public void dismiss() {
		super.dismiss();
		clearTextFromAllEditText();
		dismissJoinDialog();
	}

}
