package com.myandb.singsong.dialog;

import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Request.Method;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.SettingActivity;
import com.myandb.singsong.event.OnVolleyWeakError;
import com.myandb.singsong.event.OnVolleyWeakResponse;
import com.myandb.singsong.net.OAuthJsonObjectRequest;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Authenticator;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class WithdrawDialog extends BaseDialog {
	
	private SettingActivity parent;
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
			UrlBuilder urlBuilder = new UrlBuilder();
			String url = urlBuilder.s("users").toString();
			
			OAuthJsonObjectRequest request = new OAuthJsonObjectRequest(
					Method.DELETE, url, null,
					new OnVolleyWeakResponse<WithdrawDialog, JSONObject>(WithdrawDialog.this, "onWithdrawSuccess"),
					new OnVolleyWeakError<WithdrawDialog>(WithdrawDialog.this, "onWithdrawError")
			);
			
			RequestQueue queue = ((App) getActivity().getApplicationContext()).getQueueInstance();
			queue.add(request);
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
		parent.finish();
	}
	
	private View.OnClickListener cancelClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			dismiss();
		}
	};

}
