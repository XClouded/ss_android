package com.myandb.singsong.dialog;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.myandb.singsong.R;
import com.myandb.singsong.fragment.BaseFragment;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JSONObjectSuccessListener;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.secure.Authenticator;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ChangeNicknameDialog extends BaseDialog {
	
	public static final String EXTRA_USER_NICKNAME = "user_nickname";
	
	private String originalNickname;
	private EditText etUserNickname;
	private Button btnChangeNickname;
	private String lastInputNickname;

	@Override
	protected int getResourceId() {
		return R.layout.dialog_change_nickname;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		originalNickname = bundle.getString(EXTRA_USER_NICKNAME);
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		etUserNickname = (EditText) view.findViewById(R.id.et_user_nickname);
		btnChangeNickname = (Button) view.findViewById(R.id.btn_change_nickname);
	}
	
	@Override
	protected void initialize(Activity activity) {
		setProgressDialogMessage(getString(R.string.progress_changing));
		originalNickname = originalNickname != null ? originalNickname : "";
	}

	@Override
	protected void setupViews() {
		etUserNickname.setText(originalNickname);
		btnChangeNickname.setOnClickListener(changeNicknameClickListener);
	}
	
	private OnClickListener changeNicknameClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String newNickname = etUserNickname.getText().toString();
			if (isProperNickname(newNickname)) {
				if (hasNicknameChanged(newNickname)) {
					showProgressDialog();
					checkNicknameDuplication(newNickname);
				}
			} else {
				makeToast(getString(R.string.t_alert_nickname_validation_failed));
			}
		}
		
	};
	
	private boolean hasNicknameChanged(String nickname) {
		return !originalNickname.equals(nickname);
	}
	
	private boolean isProperNickname(String nickname) {
		return nickname.length() > 1;
	}
	
	private void checkNicknameDuplication(String nickname) {
		lastInputNickname = nickname;
		Bundle params = new Bundle();
		params.putString("nickname", nickname);
		JSONObjectRequest request = new JSONObjectRequest(
				"users", params, null,
				new JSONObjectSuccessListener(this, "onNicknameFound"),
				new JSONErrorListener(this, "onNicknameNotFound")
		);
		addRequest(request);
	}
	
	public void onNicknameFound(JSONObject response) {
		dismissProgressDialog();
		makeToast(getString(R.string.t_alert_nickname_duplicated));
	}
	
	public void onNicknameNotFound() {
		changeNickname(lastInputNickname);
	}
	
	private void changeNickname(String nickname) {
		try {
			JSONObject message = new JSONObject();
			message.put("nickname", nickname);
			
			JSONObjectRequest request = new JSONObjectRequest(
					Method.PUT, "users", null, message,
					new JSONObjectSuccessListener(this, "onChangedSuccess", User.class),
					new JSONErrorListener(this, "onChangedError"));
			addRequest(request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void onChangedSuccess(User user) {
		new Authenticator().update(user);
		if (getParentFragment() instanceof BaseFragment) {
			((BaseFragment) getParentFragment()).notifyDataChanged();
		}
		dismiss();
	}
	
	public void onChangedError() {
		makeToast(getString(R.string.t_alert_change_failed));
		dismissProgressDialog();
	}

}
