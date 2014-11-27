package com.myandb.singsong.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
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
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		getWindow().setAttributes(getWindowLayoutParams());
		
		setContentView(getResourceId());
		
		stylingDialog();
		
		onViewInflated();
		
		setupViews();
	}
	
	protected void stylingDialog() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		getWindow().getAttributes().width = (int) (metrics.widthPixels * 0.85);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
	}
	
	protected WindowManager.LayoutParams getWindowLayoutParams() {
		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
		layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		layoutParams.dimAmount = DIM_AMOUNT;
		return layoutParams;
	}
	
	protected abstract void initialize();
	
	protected abstract int getResourceId();
	
	protected abstract void onViewInflated();
	
	protected abstract void setupViews();

}
