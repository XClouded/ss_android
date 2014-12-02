package com.myandb.singsong.fragment;

import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.service.PlayerService;

import android.app.Activity;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {
	
	public static final String EXTRA_FRAGMENT_TITLE = "fragment_title";
	
	private String title;
	
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
		
		setupViews();
		
		notifyDataChanged();
	}

	public void notifyDataChanged() {
		onDataChanged();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		setActionBarTitle(title);
	}

	public void onBackPressed() {
		getActivity().finish();
	}
	
	public PlayerService getPlayerService() throws Exception {
		Activity parent = getActivity();
		if (parent != null && parent instanceof BaseActivity) {
			return ((BaseActivity) parent).getPlayerService();
		} else {
			throw new Exception();
		}
	}

	protected void setActionBarTitle(String title) {
		if (title != null && title.length() > 0) {
			try {
				getSupportActionBar(getActivity()).setTitle(title);
			} catch (NotFoundException e) {
				// It's all right
			}
		}
	}
	
	protected void setActionBarTitle(int resId) {
		try {
			getSupportActionBar(getActivity()).setTitle(resId);
		} catch (NotFoundException e) {
			// It's all right
		}
	}
	
	public ActionBar getSupportActionBar(Activity activity) {
		if (activity != null && activity instanceof ActionBarActivity) {
			return ((ActionBarActivity) activity).getSupportActionBar();
		}
		return null;
	}

	protected abstract int getResourceId();
	
	protected abstract void onViewInflated(View view, LayoutInflater inflater);
	
	protected abstract void initialize(Activity activity);
	
	protected abstract void setupViews();
	
	protected abstract void onDataChanged();
	
}
