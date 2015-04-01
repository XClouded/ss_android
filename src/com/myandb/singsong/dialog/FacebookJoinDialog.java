package com.myandb.singsong.dialog;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.myandb.singsong.R;
import com.myandb.singsong.dialog.JoinDialog.OnJoinCompleteListener;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JSONObjectSuccessListener;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.secure.Encryption;
import com.myandb.singsong.util.Utility;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FacebookJoinDialog extends BaseDialog {
	
	public static final String EXTRA_FACEBOOK_TOKEN = "facebook_token";

	private OnJoinCompleteListener listener;
	private String facebookToken;
	private TextView tvImportantTerms;
	private TextView tvPrivacyPurpose;
	private TextView tvShowAllTerms;
	private TextView tvShowAllPrivacy;
	private Button btnJoinComplete;

	@Override
	protected int getResourceId() {
		return R.layout.dialog_facebook_join;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		
		facebookToken = bundle.getString(EXTRA_FACEBOOK_TOKEN);
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		btnJoinComplete = (Button) view.findViewById(R.id.btn_join_complete);
		tvImportantTerms = (TextView) view.findViewById(R.id.tv_important_terms);
		tvPrivacyPurpose = (TextView) view.findViewById(R.id.tv_privacy_purpose);
		tvShowAllTerms = (TextView) view.findViewById(R.id.tv_show_all_terms);
		tvShowAllPrivacy = (TextView) view.findViewById(R.id.tv_show_all_privacy);
	}

	@Override
	protected void initialize(Activity activity) {}

	@Override
	protected void setupViews() {
		btnJoinComplete.setOnClickListener(joinClickListener);
		
		tvImportantTerms.setMovementMethod(new ScrollingMovementMethod());
		tvPrivacyPurpose.setMovementMethod(new ScrollingMovementMethod());
		
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
	
	private View.OnClickListener joinClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showProgressDialog();
			joinByFacebook(facebookToken);
		}
	};
	
	private void joinByFacebook(String token) {
		try {
			Encryption encryption = new Encryption();
			JSONObject message = new JSONObject();
			message.put("facebook_token", token);
			message.put("device_id", encryption.getDeviceId(getActivity()));
			requestLogin(message);		
		} catch (JSONException e) {
			onJoinError();
		} catch (NullPointerException e) {
			onJoinError();
		}
	}
	
	private void requestLogin(JSONObject message) {
		JSONObjectRequest request = new JSONObjectRequest(
				"token", null, message,
				new JSONObjectSuccessListener(this, "onJoinComplete"),
				new JSONErrorListener(this, "onJoinError")
		);
		addRequest(request);
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
		removeUserOnLocal();
		dismissProgressDialog();
	}
	
	public void setOnJoinCompleteListener(OnJoinCompleteListener listener) {
		this.listener = listener;
	}

}
