package com.myandb.singsong.pager;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public class PagerWrappingAdapter extends PagerAdapter {
	
	private ListAdapter adapter;
	
	public PagerWrappingAdapter(ListAdapter adapter) {
		this.adapter = adapter;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View item = adapter.getView(position, null, container);
		container.addView(item, 0);
		return item;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == (View) object;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getCount() {
		return adapter.getCount();
	}

}
