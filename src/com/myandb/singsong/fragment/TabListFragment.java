package com.myandb.singsong.fragment;

import android.app.Activity;
import android.util.SparseArray;
import android.view.View;
import android.widget.ListAdapter;

import com.myandb.singsong.App;
import com.myandb.singsong.net.UrlBuilder;

public abstract class TabListFragment extends ListFragment {
	
	private int selectedViewId = App.INVALID_RESOURCE_ID;
	private TabHost tabHost;
	private OnTabChangedListener listener;
	
	@Override
	protected void initialize(Activity activity) {
		super.initialize(activity);
		initializeTabs();
	}

	private void initializeTabs() {
		if (hasNoTabs()) {
			tabHost = new TabHost();
			defineTabs(tabHost);
		}
	}

	public void selectTab(View view) {
		if (view == null) {
			return;
		}
		
		if (hasNoTabs()) {
			return;
		}
		
		Tab tab = tabHost.getTab(view);
		if (tab != null) {
			if (selectedViewId == view.getId()) {
				return;
			}
			
			changeList(view, tab);
			dispatchTabChangedListener(view, getView().findViewById(selectedViewId));
			
			selectedViewId = view.getId();
		}
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
	
	private boolean hasNoTabs() {
		return tabHost == null;
	}
	
	public void setOnTabChangedListener(OnTabChangedListener listener) {
		this.listener = listener;
	}
	
	protected abstract void defineTabs(TabHost tabHost);
	
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
	
	public static final class TabHost {
		
		private SparseArray<Tab> tabs = new SparseArray<Tab>();
		
		public void putTab(View view, Tab tab) {
			if (getTab(view) == null) {
				tabs.put(view.getId(), tab);
			}
		}
		
		private Tab getTab(View view) {
			return tabs.get(view.getId());
		}
		
	}
	
	public interface OnTabChangedListener {
		
		public void onSelected(View view);
		
		public void onUnselected(View view);
		
	}
	
}
