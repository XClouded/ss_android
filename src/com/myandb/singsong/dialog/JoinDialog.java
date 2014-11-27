package com.myandb.singsong.dialog;

import com.myandb.singsong.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager.LayoutParams;

public class JoinDialog extends BaseDialog {
	
	private ProgressDialog progressDialog;

	public JoinDialog(Context context) {
		super(context);
	}
	
	@Override
	protected LayoutParams getWindowLayoutParams() {
		LayoutParams layoutParams = super.getWindowLayoutParams();
		layoutParams.gravity = Gravity.TOP;
		layoutParams.y = 250;
		return layoutParams;
	}

	@Override
	protected void initialize() {
		progressDialog = new ProgressDialog(getContext());
		progressDialog.setIndeterminate(true);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("회원가입 중입니다.");
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_join;
	}

	@Override
	protected void onViewInflated() {
		
	}

	@Override
	protected void setupViews() {
		
	}

}
