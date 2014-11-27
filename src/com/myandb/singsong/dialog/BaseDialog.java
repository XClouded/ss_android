package com.myandb.singsong.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

public abstract class BaseDialog extends Dialog {

	private static final float DIM_AMOUNT = 0.5f;
	
	public BaseDialog(Context context) {
		super(context);
		initialize();
	}
	
	public BaseDialog(Context context, int style) {
		super(context, style);
		initialize();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setDialogDimAmount(DIM_AMOUNT);
		
		setContentView(getResourceId());
		
		onViewInflated();
		
		setupViews();
	}
	
	private void setDialogDimAmount(float dimAmount) {
		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
		layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		layoutParams.dimAmount = dimAmount;
		getWindow().setAttributes(layoutParams);
	}
	
	protected abstract void initialize();
	
	protected abstract int getResourceId();
	
	protected abstract void onViewInflated();
	
	protected abstract void setupViews();

}
