package com.myandb.singsong.dialog;

import java.util.regex.Matcher;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.myandb.singsong.R;
import com.myandb.singsong.fragment.BaseFragment;
import com.myandb.singsong.model.Profile;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JSONObjectSuccessListener;
import com.myandb.singsong.secure.Authenticator;

import android.app.Activity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ChangeEmailDialog extends BaseDialog {
	
	public static final String EXTRA_USER_EMAIL = "user_email";
	
	private String originalEmail;
	private EditText etUserEmail;
	private Button btnChangeEmail;

	@Override
	protected int getResourceId() {
		return R.layout.dialog_change_email;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		originalEmail = bundle.getString(EXTRA_USER_EMAIL);
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		etUserEmail = (EditText) view.findViewById(R.id.et_user_email);
		btnChangeEmail = (Button) view.findViewById(R.id.btn_change_email);
	}

	@Override
	protected void initialize(Activity activity) {
		setProgressDialogMessage("변경 중입니다.");
		originalEmail = originalEmail != null ? originalEmail : "";
	}

	@Override
	protected void setupViews() {
		etUserEmail.setText(originalEmail);
		btnChangeEmail.setOnClickListener(changeEmailClickListener);
	}
	
	private OnClickListener changeEmailClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String newEmail = etUserEmail.getText().toString();
			if (isProperEmail(newEmail)) {
				if (hasEmailChanged(newEmail)) {
					showProgressDialog();
					changeEmail(newEmail);
				}
			} else {
				makeToast("이메일을 입력해주세요.");
			}
		}
		
	};
	
	private boolean hasEmailChanged(String email) {
		return !originalEmail.equals(email);
	}
	
	private boolean isProperEmail(String email) {
		Matcher matcher = Patterns.EMAIL_ADDRESS.matcher(email);
		return matcher.matches() || email.isEmpty();
	}
	
	private void changeEmail(String email) {
		try {
			JSONObject message = new JSONObject();
			message.put("email", email);
			
			JSONObjectRequest request = new JSONObjectRequest(
					Method.PUT, "profile", message,
					new JSONObjectSuccessListener(this, "onChangedSuccess", Profile.class),
					new JSONErrorListener(this, "onChangedError"));
			addRequest(request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void onChangedSuccess(Profile profile) {
		User user = Authenticator.getUser();
		user.setProfile(profile);
		new Authenticator().update(user);
		if (getParentFragment() instanceof BaseFragment) {
			((BaseFragment) getParentFragment()).notifyDataChanged();
		}
		dismiss();
	}
	
	public void onChangedError() {
		makeToast("이메일 변경에 실패하였습니다.");
		dismissProgressDialog();
	}

}
