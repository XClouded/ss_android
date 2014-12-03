package com.myandb.singsong.fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.myandb.singsong.App;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.service.PlayerService;
import com.myandb.singsong.util.Logger;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public abstract class BaseFragment extends Fragment {
	
	public static final String EXTRA_FRAGMENT_TITLE = "fragment_title";
	public static final String EXTRA_FRAGMENT_SUBTITLE = "fragment_subtitle";
	
	private String title;
	private String subtitle;

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
	
	public PlayerService getPlayerService() throws Exception {
		Activity parent = getActivity();
		if (parent != null && parent instanceof BaseActivity) {
			return ((BaseActivity) parent).getPlayerService();
		} else {
			throw new Exception();
		}
	}

	public void setActionBarTitle(String title) {
		if (title != null && title.length() > 0) {
			if (getSupportActionBar() != null) {
				getSupportActionBar().setTitle(title);
			}
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
	
	public void setActionBarSubtitle(String subtitle) {
		if (subtitle != null && subtitle.length() > 0) {
			if (getSupportActionBar() != null) {
				getSupportActionBar().setSubtitle(subtitle);
			}
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
	
	public ActionBar getSupportActionBar() {
		Activity activity = getActivity();
		if (activity != null && activity instanceof ActionBarActivity) {
			return ((ActionBarActivity) activity).getSupportActionBar();
		}
		return null;
	}
	
	public Context getApplicationContext() {
		Activity activity = getActivity();
		if (activity != null) {
			return activity.getApplicationContext();
		}
		return null;
	}
	
	public RequestQueue getRequestQueue() {
		Context context = getApplicationContext();
		if (context != null && context instanceof App) {
			return ((App) context).getQueueInstance();
		} else {
			Logger.log("Request queue is null. Please check out the reason");
			return null;
		}
	}
	
	public <T> void addRequest(Request<T> request) {
		RequestQueue queue = getRequestQueue();
		if (queue != null) {
			queue.add(request);
		}
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

	protected abstract int getResourceId();
	
	protected abstract void onViewInflated(View view, LayoutInflater inflater);
	
	protected abstract void initialize(Activity activity);
	
	protected abstract void setupViews();
	
	protected abstract void onDataChanged();
	
}
