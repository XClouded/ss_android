package com.myandb.singsong.widget;

import com.myandb.singsong.R;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

public class HorizontalListView extends HorizontalScrollView {
	
	private ListAdapter adapter;
	private DataSetObserver observer;
	private int horizontalSpacing;

	public HorizontalListView(Context context) {
		super(context);
	}
	
	public HorizontalListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setHorizontalSpacing(int resId) {
		horizontalSpacing = getResources().getDimensionPixelOffset(resId);
	}
	
	public ListAdapter getAdapter() {
		return adapter;
	}
	
	public void setAdapter(ListAdapter adapter) {
		if (this.adapter != null && observer != null) {
			this.adapter.unregisterDataSetObserver(observer);
		}
		
		this.adapter = adapter;
		
		if (this.adapter != null) {
			this.observer = new AdapterDataSetObserver();
			this.adapter.registerDataSetObserver(observer);
		}
		
		setupChildren();
	}
	
	private void setupChildren() {
		removeAllViews();
		
		if (adapter == null) {
			return;
		}
		
		LinearLayout row = makeRow();
		addViewInLayout(row, -1, row.getLayoutParams(), true);
		for (int i = 0, l = adapter.getCount(); i < l; i++) {
			View child = adapter.getView(i, null, row);
			if (i > 0) {
				setChildSpacing(child);
			}
			row.addView(child);
		}
	}
	
	private LinearLayout makeRow() {
		LinearLayout row = new LinearLayout(getContext());
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		row.setLayoutParams(params);
		row.setOrientation(LinearLayout.HORIZONTAL);
		int margin = getResources().getDimensionPixelSize(R.dimen.margin);
		row.setPadding(margin, 0, margin, 0);
		return row;
	}
	
	private void setChildSpacing(View child) {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) child.getLayoutParams();
		if (lp != null) {
			lp.leftMargin = horizontalSpacing;
			child.setLayoutParams(lp);
		}
	}
	
	private class AdapterDataSetObserver extends DataSetObserver {

		@Override
		public void onChanged() {
			super.onChanged();
			setupChildren();
		}

		@Override
		public void onInvalidated() {
			super.onInvalidated();
			setupChildren();
		}
		
	}
	
}
