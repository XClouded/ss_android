package com.myandb.singsong.dialog;

import java.util.regex.Matcher;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.myandb.singsong.R;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JSONObjectSuccessListener;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.secure.Encryption;
import com.myandb.singsong.util.Utility;

public class JoinDialog extends BaseDialog {
	
	private OnJoinCompleteListener listener;
	private EditText etUsername;
	private EditText etPassword;
	private EditText etRePassword;
	private Button btnJoinComplete;
	private TextView tvValidUsername;
	private TextView tvValidPassword;
	private TextView tvValidRePassword;
	private TextView tvImportantTerms;
	private TextView tvPrivacyPurpose;
	private TextView tvCollections;
	private TextView tvShowAllTerms;
	private TextView tvShowAllPrivacy;
	private String lastInputUsername;
	private int colorGrey;
	private int colorPrimary;

	@Override
	protected void initialize(Activity activity) {
		setProgressDialogMessage(getString(R.string.progress_joining));
		
		colorGrey = getResources().getColor(R.color.font_grey);
		colorPrimary = getResources().getColor(R.color.primary);
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		etUsername = (EditText) view.findViewById(R.id.et_user_username);
		etPassword = (EditText) view.findViewById(R.id.et_user_password);
		etRePassword = (EditText) view.findViewById(R.id.et_user_password_re);
		btnJoinComplete = (Button) view.findViewById(R.id.btn_join_complete);
		tvValidUsername = (TextView) view.findViewById(R.id.tv_valid_username);
		tvValidPassword = (TextView) view.findViewById(R.id.tv_valid_password);
		tvValidRePassword = (TextView) view.findViewById(R.id.tv_valid_password_re);
		tvImportantTerms = (TextView) view.findViewById(R.id.tv_important_terms);
		tvPrivacyPurpose = (TextView) view.findViewById(R.id.tv_privacy_purpose);
		tvCollections = (TextView) view.findViewById(R.id.tv_collections);
		tvShowAllTerms = (TextView) view.findViewById(R.id.tv_show_all_terms);
		tvShowAllPrivacy = (TextView) view.findViewById(R.id.tv_show_all_privacy);
	}
	
	@Override
	protected LayoutParams getWindowLayoutParams() {
		LayoutParams layoutParams = super.getWindowLayoutParams();
		layoutParams.gravity = Gravity.TOP;
		layoutParams.y = 300;
		return layoutParams;
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_join;
	}

	@Override
	protected void setupViews() {
		etUsername.setTag(false);
		etPassword.setTag(false);
		etRePassword.setTag(false);
		
		etUsername.addTextChangedListener(usernameChangedListener);
		etPassword.addTextChangedListener(passwordChangedListener);
		etRePassword.addTextChangedListener(rePasswordChangedListener);
		
		btnJoinComplete.setEnabled(false);
		btnJoinComplete.setOnClickListener(joinClickListener);
		
		tvImportantTerms.setMovementMethod(new ScrollingMovementMethod());
		tvPrivacyPurpose.setMovementMethod(new ScrollingMovementMethod());
		tvCollections.setMovementMethod(new ScrollingMovementMethod());
		
		tvShowAllTerms.setMovementMethod(new LinkMovementMethod());
		tvShowAllPrivacy.setMovementMethod(new LinkMovementMethod());
		tvShowAllTerms.setText(Html.fromHtml(Utility.getHtmlAnchor(getTermsUrl(), "전문보기")));
		tvShowAllPrivacy.setText(Html.fromHtml(Utility.getHtmlAnchor(getPrivacyUrl(), "전문보기")));
	}
	
	private String getTermsUrl() {
		UrlBuilder urlBuilder = new UrlBuilder();
		return urlBuilder.s("w").s("terms").toString();
	}
	
	private String getPrivacyUrl() {
		UrlBuilder urlBuilder = new UrlBuilder();
		return urlBuilder.s("w").s("privacy-20150401").toString();
	}
	
	private TextWatcher usernameChangedListener = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(s);
			if (matcher.matches()) {
				validationSucceed(etUsername, tvValidUsername);
			} else {
				validationFailed(etUsername, tvValidUsername);
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		
		@Override
		public void afterTextChanged(Editable s) {}
		
	};
	
	private TextWatcher passwordChangedListener = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			String password = s.toString();
			if (password.contains(" ") || password.contains("\n") || password.length() < 4) {
				validationFailed(etPassword, tvValidPassword);
			} else {
				validationSucceed(etPassword, tvValidPassword);
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
					validationSucceed(etRePassword, tvValidRePassword);
				} else {
					validationFailed(etRePassword, tvValidRePassword);
				}
			} else {
				validationFailed(etRePassword, tvValidRePassword);
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		
		@Override
		public void afterTextChanged(Editable s) {}
		
	};
	
	private void validationSucceed(EditText editText, TextView validTextView) {
		editText.setTag(true);
		validTextView.setTextColor(colorPrimary);
		
		if ((Boolean) etUsername.getTag()
				&& (Boolean) etPassword.getTag()
				&& (Boolean) etRePassword.getTag()) {
			btnJoinComplete.setEnabled(true);
			if (editText.getId() == etRePassword.getId()) {
				hideSoftInput(editText);
			}
		}
	}
	
	private void hideSoftInput(EditText editText) {
		InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
	
	private void validationFailed(EditText editText, TextView validTextView) {
		editText.setTag(false);
		validTextView.setTextColor(colorGrey);
		btnJoinComplete.setEnabled(false);
	}
	
	private View.OnClickListener joinClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showProgressDialog();
			checkUsernameDuplication(etUsername.getText().toString());
		}
	};
	
	private void checkUsernameDuplication(String username) {
		lastInputUsername = username;
		Bundle params = new Bundle();
		params.putString("username", username);
		JSONObjectRequest request = new JSONObjectRequest(
				"users", params, null,
				new JSONObjectSuccessListener(this, "onUsernameFound"),
				new JSONErrorListener(this, "onUsernameNotFound")
		);
		addRequest(request);
	}
	
	public void onUsernameFound(JSONObject response) {
		dismissProgressDialog();
		String message = lastInputUsername;
		message += getString(R.string.t_alert_email_duplicated);
		makeToast(message);
	}
	
	public void onUsernameNotFound() {
		String username = etUsername.getText().toString();
		String password = etPassword.getText().toString();
		requestJoin(username, password);
	}
	
	private void requestJoin(String username, String password) {
		try {
			JSONObject message = new JSONObject();
			Encryption encryption = new Encryption();
			message.put("username", username);
			message.put("password", password);
			message.put("device_id", encryption.getDeviceId(getActivity()));
			
			JSONObjectRequest request = new JSONObjectRequest(
					"users", null, message,
					new JSONObjectSuccessListener(this, "onJoinComplete"),
					new JSONErrorListener(this, "onJoinError")
			);
			addRequest(request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void onJoinComplete(JSONObject response) {
		try {
			User user = extractUserFromResponse(response);
			String token = extractTokenFromResponse(response);
			saveUserOnLocal(user, token);
			onLoginComplete();
		} catch (JSONException e) {
			onJoinError();
		} catch (JsonSyntaxException e) {
			onJoinError();
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
	
	private void onLoginComplete() {
		if (listener != null) {
			listener.onJoin();
		}
		dismissProgressDialog();
		dismiss();
	}
	
	public void onJoinError() {
		makeToast(R.string.t_alert_join_failed);
		clearTextFromAllEditText();
		removeUserOnLocal();
		dismissProgressDialog();
	}
	
	private void clearTextFromAllEditText() {
		etUsername.setText("");
		etPassword.setText("");
		etRePassword.setText("");
	}
	
	public void setOnJoinCompleteListener(OnJoinCompleteListener listener) {
		this.listener = listener;
	}

	@Override
	public void dismiss() {
		super.dismiss();
		clearTextFromAllEditText();
	}
	
	public interface OnJoinCompleteListener {
		
		public void onJoin();
		
	}

}
