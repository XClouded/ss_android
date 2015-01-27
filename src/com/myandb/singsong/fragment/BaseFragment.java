package com.myandb.singsong.fragment;

import com.android.volley.Request;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.service.PlayerService;
import com.sromku.simple.fb.SimpleFacebook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public abstract class BaseFragment extends Fragment {
	
	public static final String EXTRA_FRAGMENT_TITLE = "fragment_title";
	
	private String title;
	private ProgressDialog progressDialog;
	private CharSequence progressMessage; 
	private SimpleFacebook simpleFacebook;

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
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		initialize(getActivity());
		
		setupViews(savedInstanceState);
		
		notifyDataChanged();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		try {
			configureActionBar();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		simpleFacebook = SimpleFacebook.getInstance(getActivity());
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (isAdded() && getActivity() != null) {
			getSimpleFacebook().onActivityResult(getActivity(), requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public SimpleFacebook getSimpleFacebook() {
		if (simpleFacebook == null) {
			simpleFacebook = SimpleFacebook.getInstance(getActivity());
		}
		return simpleFacebook;
	}

	private void configureActionBar() {
		if (!isActionBarEnabled()) {
			return;
		}
		
		setActionBarBackground(R.drawable.actionbar_background);
		setActionBarOverlay(false);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		if (isActionBarLogoEnabled()) {
			setActionBarLogo(R.drawable.logo_actionbar);
		} else {
			setActionBarTitle(title);
		}
	}

	public void notifyDataChanged() {
		onDataChanged();
	}

	public void onBackPressed() {
		getActivity().finish();
	}
	
	public void setActionBarOverlay(boolean overlay) {
		View container = getActivity().findViewById(R.id.fl_content_fragment_container);
		
		if (container != null) {
			if (overlay) {
				container.setPadding(0, 0, 0, 0);
			} else {
				int actionBarHeight = getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height);
				container.setPadding(0, actionBarHeight, 0, 0);
			}
		}
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
	
	public void setActionBarBackground(int resId) throws IllegalStateException {
		try {
			Drawable background = getResources().getDrawable(resId);
			getSupportActionBar().setBackgroundDrawable(background);
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
	}

	public void setActionBarTitle(String title) throws IllegalStateException {
		if (title != null) {
			getSupportActionBar().setDisplayShowTitleEnabled(true);
			getSupportActionBar().setDisplayUseLogoEnabled(false);
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
	
	public void setActionBarLogo(int resId) {
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setLogo(resId);
	}
	
	public <T> void addRequest(Request<T> request) {
		try {
			getApplicationContext().addShortLivedRequest(this, request);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void makeToast(String message) {
		if (!isAdded()) {
			return;
		}
		
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
	
	public boolean isActionBarEnabled() {
		return true;
	}
	
	public boolean isActionBarLogoEnabled() {
		return false;
	}
	
	protected abstract int getResourceId();
	
	protected abstract void onViewInflated(View view, LayoutInflater inflater);
	
	protected abstract void initialize(Activity activity);
	
	protected abstract void setupViews(Bundle savedInstanceState);
	
	protected abstract void onDataChanged();
	
}
