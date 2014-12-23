package com.myandb.singsong.widget;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

public class HorizontalAdapterView extends HorizontalScrollView {
	
	private ListAdapter adapter;
	private DataSetObserver observer;

	public HorizontalAdapterView(Context context) {
		super(context);
	}

	public HorizontalAdapterView(Context context, AttributeSet attrs) {
		super(context, attrs);
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
		addView(row);
		for (int i = 0, l = adapter.getCount(); i < l; i++) {
			View child = adapter.getView(i, null, row);
			row.addView(child);
		}
	}
	
	private LinearLayout makeRow() {
		LinearLayout row = new LinearLayout(getContext());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		row.setOrientation(LinearLayout.HORIZONTAL);
		row.setLayoutParams(params);
		return row;
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
