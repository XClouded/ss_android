package com.myandb.singsong.fragment;

import com.android.volley.Request;
import com.myandb.singsong.App;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.service.PlayerService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public abstract class BaseFragment extends Fragment {
	
	public static final String EXTRA_FRAGMENT_TITLE = "fragment_title";
	public static final String EXTRA_FRAGMENT_SUBTITLE = "fragment_subtitle";
	
	private String title;
	private String subtitle;
	private ProgressDialog progressDialog;
	private CharSequence progressMessage; 

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
	
	protected void onArgumentsReceived(Bundle bundle) {
		title = bundle.getString(EXTRA_FRAGMENT_TITLE);
		subtitle = bundle.getString(EXTRA_FRAGMENT_SUBTITLE);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		initialize(getActivity());
		
		setupViews();
		
		notifyDataChanged();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		setActionBarTitle(title);
		setActionBarSubtitle(subtitle);
	}

	public void notifyDataChanged() {
		onDataChanged();
	}

	public void onBackPressed() {
		getActivity().finish();
	}
	
	public BaseActivity getBaseActivity() throws IllegalStateException {
		Activity activity = getActivity();
		if (activity != null && activity instanceof BaseActivity) {
			return ((BaseActivity) activity);
		} else {
			throw new IllegalStateException("Parent Activity is null or not instance of BaseActivity");
		}
	}
	
	public App getApplicationContext() throws IllegalStateException {
		Context context = getBaseActivity().getApplicationContext();
		if (context != null && context instanceof App) {
			return ((App) context);
		} else {
			throw new IllegalStateException("Application is null or not instance of App");
		}
	}
	
	public PlayerService getPlayerService() throws IllegalStateException {
		return getBaseActivity().getPlayerService();
	}
	
	public ActionBar getSupportActionBar() throws IllegalStateException {
		return getBaseActivity().getSupportActionBar();
	}

	public void setActionBarTitle(String title) throws IllegalStateException {
		if (title != null && title.length() > 0) {
			getSupportActionBar().setTitle(title);
		}
	}
	
	public void setActionBarTitle(int resId) {
		try {
			setActionBarTitle(getString(resId));
		} catch (NotFoundException e) {
			e.printStackTrace();
			// It's all right
		}
	}
	
	public void setActionBarSubtitle(String subtitle) throws IllegalStateException {
		if (subtitle != null && subtitle.length() > 0) {
			getSupportActionBar().setSubtitle(subtitle);
		}
	}
	
	public void setActionBarSubtitle(int resId) {
		try {
			setActionBarSubtitle(getString(resId));
		} catch (NotFoundException e) {
			e.printStackTrace();
			// It's all right
		}
	}
	
	public <T> void addRequest(Request<T> request) {
		getApplicationContext().addShortLivedRequest(this, request);
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
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
	
	public void startFragment(Intent intent) {
		try {
			getBaseActivity().changePage(intent);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDestroy() {
		cancelRequests();
		dismissProgressDialog();
		super.onDestroy();
	}
	
	public void cancelRequests() {
		try {
			getApplicationContext().cancelRequests(this);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	protected abstract int getResourceId();
	
	protected abstract void onViewInflated(View view, LayoutInflater inflater);
	
	protected abstract void initialize(Activity activity);
	
	protected abstract void setupViews();
	
	protected abstract void onDataChanged();
	
}
