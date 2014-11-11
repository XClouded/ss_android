package com.myandb.singsong.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(getResourceId(), container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		onArgumentsReceived(getArguments());
		
		onViewInflated(view, getLayoutInflater(savedInstanceState));
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
	
	public void onBackPressed() {
		getActivity().finish();
	}

	protected abstract int getResourceId();
	
	protected abstract void onArgumentsReceived(Bundle bundle);
	
	protected abstract void onViewInflated(View view, LayoutInflater inflater);
	
	protected abstract void initialize(Activity activity);
	
	protected abstract void setupViews();
	
	protected abstract void onDataChanged();
	
}
