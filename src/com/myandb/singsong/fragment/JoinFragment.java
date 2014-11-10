package com.myandb.singsong.fragment;

import java.util.regex.Matcher;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.OldBaseActivity;
import com.myandb.singsong.activity.MainActivity;
import com.myandb.singsong.event.OnVolleyWeakError;
import com.myandb.singsong.event.OnVolleyWeakResponse;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Auth;
import com.myandb.singsong.secure.Encryption;
import com.myandb.singsong.util.Utility;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class JoinFragment extends Fragment {
	
	private EditText etUsername;
	private EditText etPassword;
	private EditText etRePassword;
	private Button btnComplete;
	private Button btnToLogin;
	private TextView tvValidUsername;
	private TextView tvValidPassword;
	private TextView tvValidRePassword;
	private TextView tvAgreementPolicy;
	private Handler handler;
	private RequestQueue requestQueue;
	private int colorWhite;
	private int colorGrey;
	private int colorPrimary;
	private String lastInputUsername;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (Auth.isLoggedIn()) {
			getActivity().finish();
			
			// Report to server
			// Logged in user can not access this page
		} else {
			requestQueue = ((App) getActivity().getApplicationContext()).getQueueInstance();
			handler = new Handler();
			
			colorWhite = getResources().getColor(R.color.white);
			colorGrey = getResources().getColor(R.color.font_grey);
			colorPrimary = getResources().getColor(R.color.primary);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_join, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		View view = getView();
		
		etUsername = (EditText) view.findViewById(R.id.et_user_username);
		etPassword = (EditText) view.findViewById(R.id.et_user_password);
		etRePassword = (EditText) view.findViewById(R.id.et_user_password_re);
		btnComplete = (Button) view.findViewById(R.id.btn_signup_complete);
		btnToLogin = (Button) view.findViewById(R.id.btn_to_login);
		tvValidUsername = (TextView) view.findViewById(R.id.tv_valid_username);
		tvValidPassword = (TextView) view.findViewById(R.id.tv_valid_password);
		tvValidRePassword = (TextView) view.findViewById(R.id.tv_valid_password_re);
		tvAgreementPolicy = (TextView) view.findViewById(R.id.tv_agreement_policy);
		
		etUsername.setTag(false);
		etPassword.setTag(false);
		etRePassword.setTag(false);
		
		etUsername.addTextChangedListener(usernameChangedListener);
		etPassword.addTextChangedListener(passwordChangedListener);
		etRePassword.addTextChangedListener(rePasswordChangedListener);
		
		btnComplete.setEnabled(false);
		btnComplete.setOnClickListener(signupClickListener);
		btnToLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				OldBaseActivity parent = (OldBaseActivity) getActivity();
				
				if (parent != null) {
					parent.replaceFragment(new LoginFragment());				
				}
			}
			
		});
		
		UrlBuilder urlBuilder = new UrlBuilder();
		String policyHtml = "가입하신 이메일로 인증 번호가 전송됩니다. 인증을 하셔야 많은 기능들이 이용가능하니 꼭 본인의 이메일을 입력해주세요 :) <br/>";
		policyHtml += "daum.net 또는 hanmail.net 계정은 이메일이 전송되지 않을 수 있습니다. <br/><br/>";
		policyHtml += "가입하기 버튼을 누르시면 자동으로 콜라보 노래방의 ";
		policyHtml += Utility.getHtmlAnchor(urlBuilder.s("w").s("terms").toString(), "이용 약관");
		policyHtml += "과 ";
		policyHtml += Utility.getHtmlAnchor(urlBuilder.s("w").s("privacy").toString(), "개인정보 보호정책");
		policyHtml += "에 동의하는 것으로 간주합니다.";
		
		tvAgreementPolicy.setMovementMethod(LinkMovementMethod.getInstance());
		tvAgreementPolicy.setText(Html.fromHtml(policyHtml));
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
		}
	}
	
	private TextWatcher usernameChangedListener = new TextWatcher() {
		
		private Matcher matcher;
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			handler.removeCallbacksAndMessages(null);
			validationFailed(tvValidUsername, etUsername);
			
			matcher = Patterns.EMAIL_ADDRESS.matcher(s);
			if (matcher.matches()) {
				Runnable runnable = new CheckUsernameRunnable(s.toString());
				
				handler.postDelayed(runnable, 800);
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		
		@Override
		public void afterTextChanged(Editable s) {}
		
	};

	private class CheckUsernameRunnable implements Runnable {
		
		private String username;

		public CheckUsernameRunnable(String username) {
			this.username = username;
		}
		
		@Override
		public void run() {
			UrlBuilder urlBuilder = new UrlBuilder();
			String url = urlBuilder.s("users").p("username", username).toString();
			lastInputUsername = username;
			
			JsonObjectRequest request = new JsonObjectRequest(
					url, null,
					new OnVolleyWeakResponse<JoinFragment, JSONObject>(JoinFragment.this, "onUsernameFound"),
					new OnVolleyWeakError<JoinFragment>(JoinFragment.this, "onUsernameNotFound")
			);
			
			if (requestQueue != null) {
				requestQueue.add(request);
			}
		}
		
	}
	
	public void onUsernameFound(JSONObject response) {
		Toast toast = Toast.makeText(getActivity(), lastInputUsername + getString(R.string.t_email_already_exist), Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP, 0, 100);
		toast.show();
		
		validationFailed(tvValidUsername, etUsername);
	}
	
	public void onUsernameNotFound() {
		String currentUsername = etUsername.getText().toString();
		
		if (currentUsername.equals(lastInputUsername)) {
			validationSucceed(tvValidUsername, etUsername);
		}
	}
	
	private TextWatcher passwordChangedListener = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			String password = s.toString();
			
			if (password.contains(" ") || password.contains("\n") || password.length() < 4) {
				validationFailed(tvValidPassword, etPassword);
			} else {
				validationSucceed(tvValidPassword, etPassword);
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		
		@Override
		public void afterTextChanged(Editable s) {}
		
	};
	
	private TextWatcher rePasswordChangedListener = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if ((Boolean) etPassword.getTag()) {
				String password = etPassword.getText().toString();
				String rePassword = s.toString();
				
				if (password.equals(rePassword)) {
					validationSucceed(tvValidRePassword, etRePassword);
				} else {
					validationFailed(tvValidRePassword, etRePassword);
				}
			} else {
				validationFailed(tvValidRePassword, etRePassword);
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		
		@Override
		public void afterTextChanged(Editable s) {}
		
	};
	
	private void validationSucceed(TextView textView, EditText editText) {
		textView.setBackgroundColor(colorPrimary);
		textView.setTextColor(colorWhite);
		editText.setTag(true);
		
		if ((Boolean) etUsername.getTag()
				&& (Boolean) etPassword.getTag()
				&& (Boolean) etRePassword.getTag()) {
			
			enableJoinButton();
			
			if (getActivity() != null) {
				InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
			}
		}
	}
	
	private void validationFailed(TextView textView, EditText editText) {
		textView.setBackgroundResource(R.drawable.btn_tranparent);
		textView.setTextColor(colorGrey);
		editText.setTag(false);
		
		disableJoinButton();
	}
	
	private void enableJoinButton() {
		btnComplete.setBackgroundResource(R.drawable.btn_primary_selector);
		btnComplete.setTextColor(colorWhite);
		btnComplete.setEnabled(true);
	}
	
	private void disableJoinButton() {
		btnComplete.setBackgroundResource(R.drawable.btn_tranparent);
		btnComplete.setTextColor(colorGrey);
		btnComplete.setEnabled(false);
	}
	
	private OnClickListener signupClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			OldBaseActivity parent = (OldBaseActivity) getActivity();
			
			try {
				UrlBuilder urlBuilder = new UrlBuilder();
				JSONObject message = new JSONObject();
				Encryption encryption = new Encryption();
				String username = etUsername.getText().toString();
				String password = etPassword.getText().toString();
				String url = urlBuilder.s("users").toString();
				
				message.put("username", username);
				message.put("password", password);
				message.put("device_id", encryption.getDeviceId(getActivity()));
				
				JsonObjectRequest request = new JsonObjectRequest(
						Method.POST, url, message,
						new OnVolleyWeakResponse<JoinFragment, JSONObject>(JoinFragment.this, "onJoinComplete"),
						new OnVolleyWeakError<JoinFragment>(JoinFragment.this, "onJoinError")
				);
				
				if (requestQueue != null) {
					requestQueue.add(request);
					
					parent.showProgressDialog();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};
	
	public void onJoinComplete(JSONObject response) {
		try {
			Gson gson = Utility.getGsonInstance();
			User user = gson.fromJson(response.getJSONObject("user").toString(), User.class);
			String token = response.getString("oauth-token");
			
			Auth auth = new Auth();
			auth.login(user, token);
			
			((OldBaseActivity) getActivity()).dismissProgressDialog();
			
			Intent intent = new Intent(getActivity(), MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			getActivity().startActivity(intent);
			getActivity().finish();
		} catch (JSONException e) {
			onJoinError();
		} catch (JsonSyntaxException e) {
			onJoinError();
		}
	}
	
	public void onJoinError() {
		((OldBaseActivity) getActivity()).dismissProgressDialog();
		
		etUsername.setText("");
		etPassword.setText("");
		etRePassword.setText("");

		Toast.makeText(getActivity(), getString(R.string.t_join_failed), Toast.LENGTH_SHORT).show();
	}
}
