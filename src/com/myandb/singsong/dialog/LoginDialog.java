package com.myandb.singsong.dialog;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Session.OpenRequest;
import com.facebook.Session.StatusCallback;
import com.facebook.model.GraphUser;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.event.OnVolleyWeakError;
import com.myandb.singsong.event.OnVolleyWeakResponse;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Encryption;
import com.myandb.singsong.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginDialog extends BaseDialog {
	
	public static final String FILE_USER_PHOTO = "user_photo";
	
	private EditText etUsername;
	private EditText etPassword;
	private Button btnLogin;
	private Button btnToJoin;
	private Button btnFacebook;
	private TextView tvFindPassword;
	private String facebookToken;
	private Activity activity;
	private ProgressDialog progressDialog;

	public LoginDialog(Context context) {
		super(context);
		this.activity = (Activity) context;
	}

	@Override
	protected LayoutParams getWindowLayoutParams() {
		LayoutParams layoutParams = super.getWindowLayoutParams();
		layoutParams.gravity = Gravity.TOP;
		layoutParams.y = 150;
		return layoutParams;
	}

	@Override
	protected void initialize() {
		progressDialog = new ProgressDialog(getContext());
		progressDialog.setIndeterminate(true);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("로그인 중입니다.");
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_login;
	}

	@Override
	protected void onViewInflated() {
		btnLogin = (Button) findViewById(R.id.btn_login);
		btnToJoin = (Button) findViewById(R.id.btn_to_join);
		btnFacebook = (Button) findViewById(R.id.btn_facebook);
		etUsername = (EditText) findViewById(R.id.et_user_username);
		etPassword = (EditText) findViewById(R.id.et_user_password);
		tvFindPassword = (TextView) findViewById(R.id.tv_find_password);
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
			OpenRequest request = new OpenRequest(activity);
			request.setPermissions(Arrays.asList("email"));
			request.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
			
			Session session = Session.getActiveSession();
			if (session != null) {
				session.close();
			}
			session = new Session(getContext().getApplicationContext());
			session.addCallback(statusCallback);
			session.openForRead(request);
		}
		
	};
	
	private Session.StatusCallback statusCallback = new StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			if (state.isOpened()) {
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
			
			if (reference != null && user != null) {
				reference.loginByFacebook(user, reference.getFacebookToken());
			}
		}
	}
	
	public String getFacebookToken() {
		return facebookToken;
	}
	
	private View.OnClickListener toJoinClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// show dialog_login
		}
	};

	private View.OnClickListener loginClickListener = new View.OnClickListener() {
		
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
			message.put("device_id", encryption.getDeviceId(getContext()));
			
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
		if (progressDialog != null) {
			progressDialog.show();
		}
		
		UrlBuilder urlBuilder = new UrlBuilder();
		JsonObjectRequest request = new JsonObjectRequest(
				Method.POST, urlBuilder.s("token").toString(), message,
				new OnVolleyWeakResponse<LoginDialog, JSONObject>(this, "onLoginSuccess"),
				new OnVolleyWeakError<LoginDialog>(this, "onLoginError")
		);
		
		RequestQueue queue = ((App) getContext().getApplicationContext()).getQueueInstance();
		queue.add(request);
	}
	
	public void onLoginSuccess(JSONObject response) {
		/*
		try {
			Gson gson = Utility.getGsonInstance();
			User user = gson.fromJson(response.getJSONObject("user").toString(), User.class);
			String token = response.getString("oauth-token");

			Authenticator auth = new Authenticator();
			auth.login(user, token);
			
			File userPhoto = new File(getContext().getFilesDir(), FILE_USER_PHOTO);
			if (user.hasPhoto()) {
				DownloadManager networkFile = new DownloadManager(); 
				networkFile.start(
						user.getPhotoUrl(), userPhoto, 
						new OnCompleteWeakListener<LoginDialog>(this, "onLoginComplete")
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
		*/
		
		onLoginComplete();
	}
	
	@Override
	public void dismiss() {
		super.dismiss();
		etUsername.setText("");
		etPassword.setText("");
	}

	public void onLoginComplete() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
		dismiss();
	}
	
	public void onLoginError() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
		Toast.makeText(getContext(), getContext().getString(R.string.t_login_failed), Toast.LENGTH_SHORT).show();
	}

}
