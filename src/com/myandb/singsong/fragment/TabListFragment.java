package com.myandb.singsong.fragment;

import android.app.Activity;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListAdapter;

import com.myandb.singsong.App;
import com.myandb.singsong.net.UrlBuilder;

public abstract class TabListFragment extends ListFragment {
	
	private SparseArray<Tab> tabs;
	private OnTabChangedListener listener;
	private int selectedTabViewId = App.INVALID_RESOURCE_ID;
	private int initialDisplayTabViewId = App.INVALID_RESOURCE_ID;
	
	@Override
	protected void initialize(Activity activity) {
		initializeTabs();
		super.initialize(activity);
	}

	private void initializeTabs() {
		if (tabs == null) {
			tabs = new SparseArray<Tab>();
			defineTabs();
		}
	}
	
	protected void addTab(View view, Tab tab) {
		if (tabs.get(view.getId()) != null) {
			return;
		}
		
		if (initialDisplayTabViewId == App.INVALID_RESOURCE_ID) {
			initialDisplayTabViewId = view.getId();
		}
		
		tabs.put(view.getId(), tab);
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				selectTab(v);
			}
		});
	}

	public void selectTab(View view) {
		if (view == null) {
			return;
		}
		
		if (selectedTabViewId == view.getId()) {
			return;
		}
		
		Tab tab = tabs.get(view.getId());
		
		if (tab == null) {
			return;
		}
		
		changeList(view, tab);
		dispatchTabChangedListener(view, getView().findViewById(selectedTabViewId));
		
		selectedTabViewId = view.getId();
	}
	
	private void changeList(View tabView, Tab tab) {
		setUrlBuilder(tab.getUrlBuilder(), false);
		setAdapter(tab.getAdapter());
	}
	
	private void dispatchTabChangedListener(View selectedView, View unselectedView) {
		if (listener != null) {
			if (selectedView != null) {
				listener.onSelected(selectedView);
			}
			if (unselectedView != null) {
				listener.onUnselected(unselectedView);
			}
		}
	}
	
	public void setOnTabChangedListener(OnTabChangedListener listener) {
		this.listener = listener;
	}
	
	@Override
	protected ListAdapter instantiateAdapter(Activity activity) {
		if (tabs != null) {
			Tab tab = tabs.get(initialDisplayTabViewId);
			return tab.getAdapter();
		}
		return null;
	}

	@Override
	protected UrlBuilder instantiateUrlBuilder(Activity activity) {
		if (tabs != null) {
			Tab tab = tabs.get(initialDisplayTabViewId);
			return tab.getUrlBuilder();
		}
		return null;
	}

	protected abstract void defineTabs();
	
	public static final class Tab {
		
		private UrlBuilder urlBuilder;
		private ListAdapter adapter;
		
		public Tab(UrlBuilder urlBuilder, ListAdapter adapter) {
			this.urlBuilder = urlBuilder;
			this.adapter = adapter;
		}
		
		public UrlBuilder getUrlBuilder() {
			return urlBuilder;
		}
		
		public ListAdapter getAdapter() {
			return adapter;
		}
		
	}
	
	public interface OnTabChangedListener {
		
		public void onSelected(View view);
		
		public void onUnselected(View view);
		
	}
	
}
