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

import android.content.Context;
import android.view.View;
import android.widget.Button;

public class WithdrawDialog extends BaseDialog {
	
	private SettingActivity parent;
	private Button btnYes;
	private Button btnNo;

	public WithdrawDialog(Context context) {
		super(context);
		parent = (SettingActivity) context;
	}

	@Override
	protected void initialize() {
		// Nothing to run
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_withdraw;
	}

	@Override
	protected void onViewInflated() {
		btnYes = (Button) findViewById(R.id.btn_yes);
		btnNo = (Button) findViewById(R.id.btn_no);
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
			
			RequestQueue queue = ((App) getContext().getApplicationContext()).getQueueInstance();
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
