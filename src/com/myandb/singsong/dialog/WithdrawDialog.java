package com.myandb.singsong.dialog;

import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.myandb.singsong.R;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.OnFailListener;
import com.myandb.singsong.net.JSONObjectSuccessListener;
import com.myandb.singsong.secure.Authenticator;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class WithdrawDialog extends BaseDialog {
	
	private Button btnYes;
	private Button btnNo;

	@Override
	protected void initialize(Activity activity) {}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		btnYes = (Button) view.findViewById(R.id.btn_yes);
		btnNo = (Button) view.findViewById(R.id.btn_no);
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_withdraw;
	}

	@Override
	protected void setupViews() {
		btnYes.setOnClickListener(withdrawClickListener);
		btnNo.setOnClickListener(cancelClickListener);
	}
	
	private View.OnClickListener withdrawClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			JSONObjectRequest request = new JSONObjectRequest(
					Method.DELETE, "users", null,
					new JSONObjectSuccessListener(WithdrawDialog.this, "onWithdrawSuccess"),
					new OnFailListener(WithdrawDialog.this, "onWithdrawError")
			);
			addRequest(request);
		}
	};
	
	public void onWithdrawSuccess(JSONObject response) {
		onWithdrawFinish();
	}
	
	public void onWithdrawError() {
		onWithdrawFinish();
	}
	
	private void onWithdrawFinish() {
		Authenticator auth = new Authenticator();
		auth.logout();
		
//		parent.dismissProgressDialog();
	}
	
	private View.OnClickListener cancelClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			dismiss();
		}
	};

}
