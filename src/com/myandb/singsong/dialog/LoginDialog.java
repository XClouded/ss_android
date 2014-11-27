package com.myandb.singsong.dialog;

import com.facebook.Session;
import com.myandb.singsong.R;

import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager.LayoutParams;

public class LoginDialog extends BaseDialog {

	public LoginDialog(Context context) {
		super(context);
	}

	@Override
	protected LayoutParams getWindowLayoutParams() {
		LayoutParams layoutParams = super.getWindowLayoutParams();
		layoutParams.gravity = Gravity.TOP;
		layoutParams.y = 150;
		return layoutParams;
	}

	@Override
	protected void initialize() {
		
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_login;
	}

	@Override
	protected void onViewInflated() {
		
	}

	@Override
	protected void setupViews() {
		
	}
	
	public void proceedLoginProcess(Session session) {
		
	}

}
