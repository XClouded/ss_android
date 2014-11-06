package com.myandb.singsong.fragment;

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
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		initiateChildViews(getView());
		
		setupViews();
		
		notifyDataChanged();
	}
	
	public void notifyDataChanged() {
		onDataChanged();
	}
	
	public void onBackPressed() {
		getActivity().finish();
	}

	public abstract boolean addToBackStack();
	
	protected abstract int getResourceId();
	
	protected abstract void initiateChildViews(View parent);
	
	protected abstract void setupViews();
	
	protected abstract void onDataChanged();
	
}
