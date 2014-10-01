package com.myandb.singsong.fragment;

import java.util.ArrayList;

import com.myandb.singsong.R;
import com.myandb.singsong.pager.ViewPagerAdapter;
import com.myandb.singsong.util.Utility;

import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public abstract class ViewPagerFragment extends Fragment {

	private ViewPager viewPager;
	private ViewPagerAdapter pagerAdapter;
	private int pagerHeader;
	private boolean hasHeader;
	private int[] buttonRes;
	private ArrayList<Button> tabButtons;
	private int defaultFontColor;
	private int highlightFontColor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.pagerAdapter = getPagerAdapter();
		this.pagerHeader = getPagerHeader();
		this.buttonRes = getButtonRes();
		
		if (this.pagerHeader != 0) {
			this.hasHeader = true;
		}
		
		tabButtons = new ArrayList<Button>();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.common_viewpager, container, false);
		
		if (hasHeader) {
			ViewGroup pagerContainer = (ViewGroup)v.findViewById(R.id.ll_pager_container);
			View header = inflater.inflate(this.pagerHeader, pagerContainer, false);
			pagerContainer.addView(header, 0);
		}
		
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) { 
		super.onActivityCreated(savedInstanceState);
		
		View v = getView();
		
		try {
			defaultFontColor = getResources().getColor(R.color.font_default);
			highlightFontColor = getResources().getColor(R.color.font_highlight);
		} catch (NotFoundException e) {
			defaultFontColor = Color.parseColor("#444444");
			highlightFontColor = Color.parseColor("#6ab8d3");
		}
		
		viewPager = (ViewPager)v.findViewById(R.id.view_pager);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setPageMargin(8);
		viewPager.setPageMarginDrawable(R.color.app_bg);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() { 
			
			private int previousPageNum = 0;
			
			@Override
			public void onPageSelected(int pageNum) {
				if (pageNum < tabButtons.size()) {
					Button btnPrevious = tabButtons.get(previousPageNum);
					btnPrevious.setBackgroundResource(R.drawable.btn_tab_selector);
					btnPrevious.setTextColor(defaultFontColor);
					
					Button btnTarget = tabButtons.get(pageNum);
					btnTarget.setBackgroundResource(R.drawable.btn_tab_selected);
					btnTarget.setTextColor(highlightFontColor);
					
					previousPageNum = pageNum;
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) { }
			
			@Override
			public void onPageScrollStateChanged(int arg0) { }
			
		});

		for (int i = 0, l = buttonRes.length; i < l; i++) {
			Button tabButton = (Button)v.findViewById(buttonRes[i]);
			tabButton.setOnClickListener(new TabClickListener(i));
			tabButtons.add(tabButton);
		}
		
		int firstItem = 0;
		Button firstBtn = tabButtons.get(firstItem);
		viewPager.setCurrentItem(firstItem);
		firstBtn.setBackgroundResource(R.drawable.btn_tab_selected);
		firstBtn.setTextColor(highlightFontColor);
		
		handleHeaderView();
	}
	
	private class TabClickListener implements OnClickListener {
		
		private int mPosition;
		
		public TabClickListener(int position) {
			mPosition = position;
		}

		@Override
		public void onClick(View v) {
			if (mPosition < pagerAdapter.getCount()) {
				viewPager.setCurrentItem(mPosition);
			}
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		Utility.recursiveRecycle(viewPager);
		System.gc();
	}

	protected ViewPagerAdapter getPagerAdapter() {
		return null;
	}
	
	protected int getPagerHeader() {
		return 0;
	}
	
	protected int[] getButtonRes() {
		return null;
	}
	
	protected abstract void handleHeaderView();

}
