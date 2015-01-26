package com.myandb.singsong.dialog;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.myandb.singsong.R;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JSONObjectSuccessListener;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.secure.Encryption;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ChangePasswordDialog extends BaseDialog {
	
	private EditText etUserOldPassword;
	private EditText etUserNewPassword;
	private EditText etUserNewPasswordRe;
	private Button btnChangePassword;

	@Override
	protected int getResourceId() {
		return R.layout.dialog_change_password;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		etUserOldPassword = (EditText) view.findViewById(R.id.et_user_old_password);
		etUserNewPassword = (EditText) view.findViewById(R.id.et_user_new_password);
		etUserNewPasswordRe = (EditText) view.findViewById(R.id.et_user_new_password_re);
		btnChangePassword = (Button) view.findViewById(R.id.btn_change_password);
	}

	@Override
	protected void initialize(Activity activity) {
		setProgressDialogMessage(getString(R.string.progress_changing));
	}

	@Override
	protected void setupViews() {
		btnChangePassword.setOnClickListener(changePasswordClickListener);
	}
	
	private OnClickListener changePasswordClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String oldPassword = etUserOldPassword.getText().toString();
			String newPassword = etUserNewPassword.getText().toString();
			String newPasswordRe = etUserNewPasswordRe.getText().toString();
			
			if (!isProperPassword(oldPassword)) {
				makeToast(getString(R.string.t_alert_old_password_validation_failed));
				return;
			}
			
			if (!isProperPassword(newPassword)) {
				makeToast(getString(R.string.t_alert_new_password_validation_failed));
				return;
			}
			
			if (!newPassword.equals(newPasswordRe)) {
				makeToast(getString(R.string.t_alert_new_re_password_validation_failed));
				return;
			}
			
			if (oldPassword.equals(newPassword)) {
				makeToast(getString(R.string.t_alert_new_password_not_changed));
				return;
			}
			
			showProgressDialog();
			changePassword(oldPassword, newPassword);
		}
	};
	
	private boolean isProperPassword(String password) {
		return !password.contains(" ") && !password.contains("\n") && password.length() >= 4;
	}
	
	private void changePassword(String oldPassword, String newPassword) {
		try {
			Encryption encryption = new Encryption();
			JSONObject message = new JSONObject();
			message.put("old_password", encryption.getSha512Convert(oldPassword));
			message.put("new_password", newPassword);
			
			JSONObjectRequest request = new JSONObjectRequest(
					Method.PUT, "users", null, message,
					new JSONObjectSuccessListener(this, "onChangeSuccess"),
					new JSONErrorListener(this, "onChangeError"));
			addRequest(request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void onChangeSuccess(JSONObject response) {
		try {
			String token = response.getString("oauth-token");
			new Authenticator().update(token);
			dismiss();
		} catch (JSONException e) {
			e.printStackTrace();
			onChangeError();
		}
	}
	
	public void onChangeError() {
		dismissProgressDialog();
		makeToast(getString(R.string.t_alert_change_failed));
	}

}
