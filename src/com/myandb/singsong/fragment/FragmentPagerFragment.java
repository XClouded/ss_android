package com.myandb.singsong.fragment;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.astuetz.PagerSlidingTabStrip;
import com.myandb.singsong.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentPagerFragment extends BaseFragment {
	
	public static final String EXTRA_PAGER_ADAPTER = "pager_adapter";
	public static final String EXTRA_ITEM_NUM = "item_num";

	private ViewGroup vgPagerContainer;
	private ViewPager viewPager;
	private PagerSlidingTabStrip tabStrip;
	private FragmentPagerAdapter pagerAdapter;
	private int launchItemNum;

	@Override
	protected int getResourceId() {
		return R.layout.fragment_fragment_pager;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		launchItemNum = bundle.getInt(EXTRA_ITEM_NUM);
		String className = bundle.getString(EXTRA_PAGER_ADAPTER);
		pagerAdapter = instantiatePagerAdapter(className);
	}
	
	private FragmentPagerAdapter instantiatePagerAdapter(String className) {
		if (className != null) {
			try {
				Class<?> classForAdapter = Class.forName(className);
				Constructor<?> constructor = classForAdapter.getConstructor(FragmentManager.class); 
				return (FragmentPagerAdapter) constructor.newInstance(getChildFragmentManager());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (java.lang.InstantiationException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected void initialize(Activity activity) {}
	
	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		vgPagerContainer = (ViewGroup) view.findViewById(R.id.viewpager_container);
		viewPager = (ViewPager) view.findViewById(R.id.viewpager);
		tabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tab_strip);
	}
	
	@Override
	protected void setupViews() {
		setAdapter(pagerAdapter);
		viewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.margin_small));
	}

	@Override
	protected void onDataChanged() {
		if (pagerAdapter != null) {
			if (launchItemNum < pagerAdapter.getCount()) {
				viewPager.setCurrentItem(launchItemNum);
			}
		}
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

}
