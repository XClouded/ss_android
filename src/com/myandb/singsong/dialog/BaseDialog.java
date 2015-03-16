package com.myandb.singsong.dialog;

import com.android.volley.Request;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.sromku.simple.fb.SimpleFacebook;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public abstract class BaseDialog extends DialogFragment {
	
	private ProgressDialog progressDialog;
	private CharSequence progressMessage;
	private SimpleFacebook simpleFacebook;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		dialog.getWindow().setAttributes(getWindowLayoutParams());
		dialog.getWindow().setWindowAnimations(R.style.S2_Animation_Dialog);
		dialog.setCanceledOnTouchOutside(true);
		
		return dialog;
	}

	protected WindowManager.LayoutParams getWindowLayoutParams() {
		return new WindowManager.LayoutParams();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(getResourceId(), container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		if (getArguments() != null) {
			onArgumentsReceived(getArguments());
		}
		
		onViewInflated(view, getLayoutInflater(savedInstanceState));
	}
	
	protected void onArgumentsReceived(Bundle bundle) {}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		styleDialog(getDialog());
		
		initialize(getActivity());
		
		setupViews();
	}
	
	protected void styleDialog(Dialog dialog) {
		DisplayMetrics metrics = new DisplayMetrics();
		dialog.getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		dialog.getWindow().getAttributes().width = (int) (metrics.widthPixels * getWidthPercentage());
	}
	
	protected float getWidthPercentage() {
		return 0.85f;
	}
	
	public App getApplicationContext() {
		Activity activity = getActivity();
		if (activity != null) {
			return (App) activity.getApplicationContext();
		}
		return null;
	}
	
	public <T> void addRequest(Request<T> request) {
		getApplicationContext().addShortLivedRequest(this, request);
	}
	
	public void cancelRequests() {
		try {
			getApplicationContext().cancelRequests(this);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDestroy() {
		cancelRequests();
		super.onDestroy();
	}
	
	public void makeToast(String message) {
		if (message != null && message.length() > 0) {
			if (getApplicationContext() != null) {
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public void makeToast(int resId) {
		try {
			makeToast(getString(resId));
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void showProgressDialog() {
		ProgressDialog dialog = getProgressDialog();
		if (!dialog.isShowing()) {
			dialog.show();
		}
	}
	
	public ProgressDialog getProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(getActivity());
			progressDialog.setIndeterminate(true);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(false);
		}
		
		if (!"".equals(progressMessage)) {
			progressDialog.setMessage(progressMessage);
		}
		
		return progressDialog;
	}
	
	public void setProgressDialogMessage(CharSequence message) {
		progressMessage = message;
	}
	
	public void dismissProgressDialog() {
		try {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();
			// ignore
		}
	}
	
	@Override
	public void dismiss() {
		super.dismiss();
		dismissProgressDialog();
	}

	@Override
	public void onResume() {
		super.onResume();
		simpleFacebook = SimpleFacebook.getInstance(getActivity());
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		getSimpleFacebook().onActivityResult(getActivity(), requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public SimpleFacebook getSimpleFacebook() {
		if (simpleFacebook == null) {
			simpleFacebook = SimpleFacebook.getInstance(getActivity());
		}
		return simpleFacebook;
	}

	protected abstract int getResourceId();
	
	protected abstract void onViewInflated(View view, LayoutInflater inflater);
	
	protected abstract void initialize(Activity activity);
	
	protected abstract void setupViews();

}
