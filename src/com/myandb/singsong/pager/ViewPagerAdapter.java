package com.myandb.singsong.pager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class ViewPagerAdapter extends PagerAdapter {

	private final LayoutInflater inflater;
	private final Context context;
	
	public ViewPagerAdapter(Context context) {
		super();
		
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == (View) object;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}
	
	protected LayoutInflater getInflater() {
		return inflater;
	}
	
	protected Context getContext() {
		return context;
	}

}
