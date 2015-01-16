package com.myandb.singsong.dialog;

import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.google.android.gcm.GCMRegistrar;
import com.myandb.singsong.R;
import com.myandb.singsong.fragment.SettingFragment;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.net.JSONObjectSuccessListener;
import com.myandb.singsong.secure.Authenticator;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class WithdrawDialog extends BaseDialog {
	
	private EditText etUserUsername;
	private Button btnWithdraw;
	private User currentUser;
	
	@Override
	protected int getResourceId() {
		return R.layout.dialog_withdraw;
	}
	
	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		etUserUsername = (EditText) view.findViewById(R.id.et_user_username);
		btnWithdraw = (Button) view.findViewById(R.id.btn_withdraw);
	}

	@Override
	protected void initialize(Activity activity) {
		currentUser = Authenticator.getUser();
	}

	@Override
	protected void setupViews() {
		btnWithdraw.setOnClickListener(withdrawClickListener);
	}
	
	private View.OnClickListener withdrawClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String username = etUserUsername.getText().toString();
			if (!username.equals(currentUser.getUsername())) {
				makeToast(getString(R.string.t_alert_withdraw_validation_failed));
				return;
			}
			
			showProgressDialog();
			withdraw();
		}
	};
	
	private void withdraw() {
		JSONObjectRequest request = new JSONObjectRequest(
				Method.DELETE, "users", null,
				new JSONObjectSuccessListener(WithdrawDialog.this, "onWithdrawSuccess"),
				new JSONErrorListener(WithdrawDialog.this, "onWithdrawError"));
		addRequest(request);
	}
	
	public void onWithdrawSuccess(JSONObject response) {
		onWithdrawFinish();
	}
	
	public void onWithdrawError() {
		onWithdrawFinish();
	}
	
	private void onWithdrawFinish() {
		new Authenticator().logout();
		unregisterGcm();
		dismissProgressDialog();
		if (getParentFragment() instanceof SettingFragment) {
			((SettingFragment) getParentFragment()).clearSharedPreferences();
		}
	}
	
	private void unregisterGcm() {
		if (GCMRegistrar.isRegistered(getActivity())) {
			GCMRegistrar.unregister(getActivity());
		}
	}

}
