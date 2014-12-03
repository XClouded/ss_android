package com.myandb.singsong.dialog;

import com.myandb.singsong.R;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public abstract class BaseDialog extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		dialog.getWindow().setAttributes(getWindowLayoutParams());
		dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
		dialog.setCanceledOnTouchOutside(true);
		
		return dialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(getResourceId(), container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		onViewInflated(view, getLayoutInflater(savedInstanceState));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		styleDialog(getDialog());
		
		initialize(getActivity());
		
		setupViews();
	}
	
	private void styleDialog(Dialog dialog) {
		DisplayMetrics metrics = new DisplayMetrics();
		dialog.getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		dialog.getWindow().getAttributes().width = (int) (metrics.widthPixels * getWidthPercentage());
	}

	protected WindowManager.LayoutParams getWindowLayoutParams() {
		return new WindowManager.LayoutParams();
	}
	
	protected float getWidthPercentage() {
		return 0.85f;
	}

	protected abstract void initialize(Activity activity);
	
	protected abstract int getResourceId();
	
	protected abstract void onViewInflated(View view, LayoutInflater inflater);
	
	protected abstract void setupViews();

}
