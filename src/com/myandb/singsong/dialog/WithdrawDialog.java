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
import android.widget.ImageView;

public class WithdrawDialog extends BaseDialog {
	
	private SettingActivity parent;
	private ImageView ivCancel;
	private Button btnYes;
	private Button btnNo;

	public WithdrawDialog(Context context) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
		
		parent = (SettingActivity) context;
	}

	@Override
	protected void initializeView() {
		setContentView(R.layout.dialog_withdraw);
		
		ivCancel = (ImageView) findViewById(R.id.iv_cancel);
		btnYes = (Button) findViewById(R.id.btn_yes);
		btnNo = (Button) findViewById(R.id.btn_no);
	}

	@Override
	protected void setupView() {
		ivCancel.setOnClickListener(cancelClickListener);
		btnNo.setOnClickListener(cancelClickListener);
		btnYes.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				parent.showProgressDialog();
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
		});
	}
	
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
			WithdrawDialog.this.dismiss();
		}
	};

}
