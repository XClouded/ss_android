package com.myandb.singsong.fragment;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session.Builder;
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
import com.myandb.singsong.activity.OldBaseActivity;
import com.myandb.singsong.activity.MainActivity;
import com.myandb.singsong.event.OnVolleyWeakError;
import com.myandb.singsong.event.OnVolleyWeakResponse;
import com.myandb.singsong.event.OnCompleteWeakListener;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.DownloadManager;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.secure.Encryption;
import com.myandb.singsong.util.Utility;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginFragment extends Fragment {
	
	public static final String FILE_USER_PHOTO = "user_photo";
	
	private EditText etUsername;
	private EditText etPassword;
	private Button btnLogin;
	private Button btnToJoin;
	private Button btnFacebook;
	private TextView tvFindPassword;
	private List<String> permissions;
	private RequestQueue requestQueue;
	private int colorWhite;
	private int colorGrey;
	private String facebookToken;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (Authenticator.isLoggedIn()) {
			getActivity().finish();
			
			// Report to server
			// Logged in user can not access this page
		} else {
			requestQueue = ((App) getActivity().getApplicationContext()).getQueueInstance();
			
			colorWhite = getResources().getColor(R.color.white);
			colorGrey = getResources().getColor(R.color.font_grey);
			
			permissions = Arrays.asList("email");
			Session session = Session.getActiveSession();
			if (session == null) {
				session = new Session(getActivity());
				Session.setActiveSession(session);
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_login, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		View view = getView();
		
		btnLogin = (Button) view.findViewById(R.id.btn_login);
		btnToJoin = (Button) view.findViewById(R.id.btn_signup);
		btnFacebook = (Button) view.findViewById(R.id.btn_facebook);
		etUsername = (EditText) view.findViewById(R.id.et_user_username);
		etPassword = (EditText) view.findViewById(R.id.et_user_password);
		tvFindPassword = (TextView) view.findViewById(R.id.tv_find_password);
		
		etUsername.setTag(false);
		etPassword.setTag(false);
		etUsername.addTextChangedListener(idChangeListener);
		etPassword.addTextChangedListener(passwordChangeListener);
		
		btnLogin.setEnabled(false);
		btnLogin.setOnClickListener(loginClickListener);
		btnFacebook.setOnClickListener(facebookLoginClickListener);
		btnToJoin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				OldBaseActivity parent = (OldBaseActivity) getActivity();
				
				if (parent != null) {
					parent.replaceFragment(new JoinFragment());				
				}
			}
		});
		
		UrlBuilder urlBuilder = new UrlBuilder();
		String findHtml = "비밀번호가 기억이 안나시나요? ";
		findHtml += Utility.getHtmlAnchor(urlBuilder.s("w").s("find_password").toString(), "비밀번호 찾기");
		
		tvFindPassword.setMovementMethod(LinkMovementMethod.getInstance());
		tvFindPassword.setText(Html.fromHtml(findHtml));
	}
	
	private TextWatcher idChangeListener = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			String username = s.toString();
			
			if (username.contains(" ") || username.contains("\n") || username.length() < 4) {
				etUsername.setTag(false);
				
				// Notify to user why it is invalid
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
				
				// Notify to user why it is invalid
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
			OpenRequest openRequest = new OpenRequest(LoginFragment.this);
			openRequest.setPermissions(permissions);
			openRequest.setCallback(statusCallback);
			openRequest.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
			
			Session session = new Builder(getActivity()).build();
			Session.setActiveSession(session);
			session.openForRead(openRequest);
		}
		
	};
	
	private Session.StatusCallback statusCallback = new StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			if (state.isOpened()) {
				com.facebook.Request.newMeRequest(session, new GetUserCallback(LoginFragment.this)).executeAsync();
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

	private OnClickListener loginClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			loginDirect();
		}
		
	};
	
	private void loginDirect() {
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
		OldBaseActivity parent = (OldBaseActivity) getActivity();
		parent.showProgressDialog();
		
		UrlBuilder urlBuilder = new UrlBuilder();
		JsonObjectRequest request = new JsonObjectRequest(
				Method.POST, urlBuilder.s("token").toString(), message,
				new OnVolleyWeakResponse<LoginFragment, JSONObject>(this, "onLoginSuccess"),
				new OnVolleyWeakError<LoginFragment>(this, "onLoginError")
		);
		
		if (requestQueue != null) {
			requestQueue.add(request);
		}
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
		((OldBaseActivity) getActivity()).dismissProgressDialog();
		
		Intent intent = new Intent(getActivity(), MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		getActivity().startActivity(intent);
		getActivity().finish();
	}
	
	public void onLoginError() {
		((OldBaseActivity) getActivity()).dismissProgressDialog();

		Toast.makeText(getActivity(), getString(R.string.t_login_failed), Toast.LENGTH_SHORT).show();
	}

}
