package com.myandb.singsong.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

public abstract class BaseDiaglog extends Dialog {

	private float dimAmount = 0.5f;
	
	public BaseDiaglog(Context context, int style) {
		super(context, style);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
		layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		layoutParams.dimAmount = this.dimAmount;
		getWindow().setAttributes(layoutParams);
		this.setCancelable(false);
		
		initializeView();
		setupView();
	}
	
	@Override
	public void onBackPressed() {
		return;
	}
	
	public BaseDiaglog setDimAmount(float amount) {
		this.dimAmount = amount;
		
		return this;
	}
	
	protected abstract void initializeView();
	protected abstract void setupView();

}
