package com.myandb.singsong.pager;

import com.myandb.singsong.R;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.LinearLayout.LayoutParams;

public class PagerWrappingAdapter extends PagerAdapter {
	
	private ListAdapter adapter;
	private int rowNum;
	
	public PagerWrappingAdapter(ListAdapter adapter) {
		this(adapter, 1);
	}
	
	public PagerWrappingAdapter(ListAdapter adapter, int rowNum) {
		this.adapter = adapter;
		this.rowNum = Math.max(1, rowNum);
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if (rowNum == 1) {
			View item = adapter.getView(position, null, container);
			container.addView(item, 0);
			return item;
		} else {
			LinearLayout wrapper = new LinearLayout(container.getContext());
			wrapper.setOrientation(LinearLayout.VERTICAL);
			int count = adapter.getCount();
			int offset = position * rowNum;
			int maximum = offset + rowNum;
			for (int i = offset; i < maximum && i < count; i++) {
				View child = adapter.getView(i, null, wrapper);
				wrapper.addView(child);
				if (i < maximum - 1) {
					wrapper.addView(getDivider(container.getContext()));
				}
			}
			container.addView(wrapper, 0);
			return wrapper;
		}
	}
	
	private View getDivider(Context context) {
		ImageView divider = new ImageView(context);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
		divider.setImageResource(R.color.divider);
		divider.setLayoutParams(params);
		return divider;
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
		return adapter.getCount() / rowNum;
	}
	
	public Object getItem(int position) {
		if (position >= 0 && position < adapter.getCount()) {
			return adapter.getItem(position);
		}
		return null;
	}

}
