package com.myandb.singsong.fragment;

import com.astuetz.PagerSlidingTabStrip;
import com.myandb.singsong.R;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class ViewPagerFragment extends BaseFragment {
	
	public static final String EXTRA_ITEM_NUM = "item_num";

	private ViewGroup vgPagerContainer;
	private ViewPager viewPager;
	private PagerSlidingTabStrip tabStrip;
	private int launchItemNum;

	@Override
	protected int getResourceId() {
		return R.layout.fragment_viewpager;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		launchItemNum = bundle.getInt(EXTRA_ITEM_NUM);
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		vgPagerContainer = (ViewGroup) view.findViewById(R.id.viewpager_container);
		viewPager = (ViewPager) view.findViewById(R.id.view_pager);
		tabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tab_strip);
	}
	
	public void setAdapter(PagerAdapter adapter) {
		viewPager.setAdapter(adapter);
		tabStrip.setViewPager(viewPager);
	}
	
	public ViewGroup getPagerContainer() {
		return vgPagerContainer;
	}
	
	public ViewPager getViewPager() {
		return viewPager;
	}
	
	public PagerSlidingTabStrip getTab() {
		return tabStrip;
	}

	@Override
	protected void onDataChanged() {
		PagerAdapter adapter = viewPager.getAdapter();
		if (adapter != null) {
			if (launchItemNum < adapter.getCount()) {
				viewPager.setCurrentItem(launchItemNum);
			}
		}
	}

}
