package com.myandb.singsong.fragment;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session.OpenRequest;
import com.facebook.Session.StatusCallback;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.event.OnVolleyWeakError;
import com.myandb.singsong.event.OnVolleyWeakResponse;
import com.myandb.singsong.event.OnCompleteWeakListener;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.DownloadManager;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.secure.Encryption;
import com.myandb.singsong.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginFragment extends BaseFragment {
	
	public static final String FILE_USER_PHOTO = "user_photo";
	
	private EditText etUsername;
	private EditText etPassword;
	private Button btnLogin;
	private Button btnToJoin;
	private Button btnFacebook;
	private TextView tvFindPassword;
	private String facebookToken;
	private int colorWhite;
	private int colorGrey;
	
	@Override
	protected int getResourceId() {
		return R.layout.fragment_login;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		// No argument
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		btnLogin = (Button) view.findViewById(R.id.btn_login);
		btnToJoin = (Button) view.findViewById(R.id.btn_signup);
		btnFacebook = (Button) view.findViewById(R.id.btn_facebook);
		etUsername = (EditText) view.findViewById(R.id.et_user_username);
		etPassword = (EditText) view.findViewById(R.id.et_user_password);
		tvFindPassword = (TextView) view.findViewById(R.id.tv_find_password);
	}

	@Override
	protected void initialize(Activity activity) {
		if (Authenticator.isLoggedIn()) {
			// Report to server
			// Logged in user can not access this page
			getActivity().finish();
			return;
		}
		
		colorWhite = getResources().getColor(R.color.white);
		colorGrey = getResources().getColor(R.color.font_grey);
	}

	@Override
	protected void setupViews() {
		etUsername.setTag(false);
		etPassword.setTag(false);
		etUsername.addTextChangedListener(usernameChangeListener);
		etPassword.addTextChangedListener(passwordChangeListener);
		
		btnLogin.setEnabled(false);
		btnLogin.setOnClickListener(loginClickListener);
		btnFacebook.setOnClickListener(facebookLoginClickListener);
		btnToJoin.setOnClickListener(toJoinClickListener);
		
		tvFindPassword.setMovementMethod(LinkMovementMethod.getInstance());
		tvFindPassword.setText(getFindPasswordHtml());
	}
	
	private Spanned getFindPasswordHtml() {
		UrlBuilder urlBuilder = new UrlBuilder();
		String findHtml = "비밀번호가 기억이 안나시나요? ";
		findHtml += Utility.getHtmlAnchor(urlBuilder.s("w").s("find_password").toString(), "비밀번호 찾기");
		return Html.fromHtml(findHtml);
	}

	@Override
	protected void onDataChanged() {
		// Nothing to implement
	}
	
	private TextWatcher usernameChangeListener = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			String username = s.toString();
			
			if (username.contains(" ") || username.contains("\n") || username.length() < 4) {
				etUsername.setTag(false);
			} else {
				etUsername.setTag(true);
			}
			
			validateUserForm();
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		
		@Override
		public void afterTextChanged(Editable s) {}
		
	};
	
	private TextWatcher passwordChangeListener = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			String password = s.toString();
			
			if (password.contains(" ") || password.contains("\n") || password.length() < 4) {
				etPassword.setTag(false);
			} else {
				etPassword.setTag(true);
			}
			
			validateUserForm();
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		
		@Override
		public void afterTextChanged(Editable s) {}
		
	};
	
	private void validateUserForm() {
		if ((Boolean) etUsername.getTag() && (Boolean) etPassword.getTag()) {
			enableLoginButton();
		} else {
			disableLoginButton();
		}
	}
	
	private void enableLoginButton() {
		btnLogin.setBackgroundResource(R.drawable.btn_primary_selector);
		btnLogin.setTextColor(colorWhite);
		btnLogin.setEnabled(true);
	}
	
	private void disableLoginButton() {
		btnLogin.setBackgroundResource(R.drawable.btn_tranparent);
		btnLogin.setTextColor(colorGrey);
		btnLogin.setEnabled(false);
	}
    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
	}
	
	private OnClickListener facebookLoginClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			OpenRequest request = new OpenRequest(LoginFragment.this);
			request.setPermissions(Arrays.asList("email"));
			request.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
			
			Session session = Session.getActiveSession();
			if (session == null) {
				session = new Session(getActivity().getApplicationContext());
			}
			session.addCallback(statusCallback);
			session.openForRead(request);
		}
		
	};
	
	private Session.StatusCallback statusCallback = new StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			if (state.isOpened()) {
				Request.newMeRequest(session, new GetUserCallback(LoginFragment.this)).executeAsync();
				facebookToken = session.getAccessToken();
			}
		}
	};
	
	private static class GetUserCallback implements GraphUserCallback {
		
		private WeakReference<LoginFragment> weakReference;
		
		public GetUserCallback(LoginFragment reference) {
			weakReference = new WeakReference<LoginFragment>(reference);
		}

		@Override
		public void onCompleted(GraphUser user, Response response) { 
			LoginFragment reference = weakReference.get();
			
			if (reference != null && reference.isAdded() && user != null) {
				reference.loginByFacebook(user, reference.getFacebookToken());
			}
		}
	}
	
	public String getFacebookToken() {
		return facebookToken;
	}
	
	private OnClickListener toJoinClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, JoinFragment.class.getName());
			if (getActivity() instanceof BaseActivity) {
				((BaseActivity) getActivity()).onPageChanged(intent);
			}
		}
	};

	private OnClickListener loginClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			loginByEmail();
		}
		
	};
	
	private void loginByEmail() {
		try {
			JSONObject message = new JSONObject();
			Encryption encryption = new Encryption();
			String username = etUsername.getText().toString();
			String password = etPassword.getText().toString();
			
			message.put("username", username);
			message.put("password", encryption.getSha512Convert(password));
			
			requestLogin(message);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
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
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	private void requestLogin(JSONObject message) {
		// show progress bar
		
		UrlBuilder urlBuilder = new UrlBuilder();
		JsonObjectRequest request = new JsonObjectRequest(
				Method.POST, urlBuilder.s("token").toString(), message,
				new OnVolleyWeakResponse<LoginFragment, JSONObject>(this, "onLoginSuccess"),
				new OnVolleyWeakError<LoginFragment>(this, "onLoginError")
		);
		
		RequestQueue queue = ((App) getActivity().getApplicationContext()).getQueueInstance();
		queue.add(request);
	}
	
	public void onLoginSuccess(JSONObject response) {
		try {
			Gson gson = Utility.getGsonInstance();
			User user = gson.fromJson(response.getJSONObject("user").toString(), User.class);
			String token = response.getString("oauth-token");

			Authenticator auth = new Authenticator();
			auth.login(user, token);
			
			File userPhoto = new File(getActivity().getFilesDir(), FILE_USER_PHOTO);
			if (user.hasPhoto()) {
				DownloadManager networkFile = new DownloadManager(); 
				networkFile.start(
						user.getPhotoUrl(), userPhoto, 
						new OnCompleteWeakListener<LoginFragment>(this, "onLoginComplete")
				);
			} else {
				if (userPhoto.exists()) {
					userPhoto.delete();
				}
				
				onLoginComplete();
			}
		} catch (JSONException e) {
			onLoginError();
		} catch (JsonSyntaxException e) {
			onLoginError();
		}
	}
	
	public void onLoginComplete() {
		// dismiss progress bar
		
		Intent intent = new Intent(getActivity(), RootActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		getActivity().startActivity(intent);
		getActivity().finish();
	}
	
	public void onLoginError() {
		// dismiss progress bar
		
		Toast.makeText(getActivity(), getString(R.string.t_login_failed), Toast.LENGTH_SHORT).show();
	}

}
