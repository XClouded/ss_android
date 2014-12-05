package com.myandb.singsong.dialog;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ChangeStatusDialog extends BaseDialog {
	
	public static final String EXTRA_USER_STATUS = "user_status";
	
	private String originalStatus;
	private EditText etUserStatus;
	private Button btnChangeStatus;

	@Override
	protected int getResourceId() {
		return R.layout.dialog_change_status;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		originalStatus = bundle.getString(EXTRA_USER_STATUS);
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		etUserStatus = (EditText) view.findViewById(R.id.et_user_status);
		btnChangeStatus = (Button) view.findViewById(R.id.btn_change_status);
	}

	@Override
	protected void initialize(Activity activity) {
		setProgressDialogMessage("변경 중입니다.");
		originalStatus = originalStatus != null ? originalStatus : "";
	}

	@Override
	protected void setupViews() {
		etUserStatus.setText(originalStatus);
		btnChangeStatus.setOnClickListener(changeStatusClickListener);
	}
	
	private OnClickListener changeStatusClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String newStatus = etUserStatus.getText().toString();
			if (hasStatusChanged(newStatus)) {
				showProgressDialog();
				changeStatus(newStatus);
			}
		}
		
	};
	
	private boolean hasStatusChanged(String status) {
		return !originalStatus.equals(status);
	}
	
	private void changeStatus(String status) {
		try {
			JSONObject message = new JSONObject();
			message.put("status_message", status);
			
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
		makeToast("상태 메세지 변경에 실패하였습니다.");
		dismissProgressDialog();
	}

}
