package com.myandb.singsong.widget;

import com.myandb.singsong.R;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

public class LinearGridView extends LinearLayout {
	
	private int columnCount = 1;
	private ListAdapter adapter;
	private DataSetObserver observer;

	public LinearGridView(Context context) {
		super(context);
	}
	
	public LinearGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setColumnCount(int count) {
		if (count > 0) {
			this.columnCount = count;
		}
	}
	
	public int getColumnCount() {
		return columnCount;
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
		
		LinearLayout row = null;
		for (int i = 0, l = adapter.getCount(); i < l; i++) {
			if (isNewRowStart(i)) {
				if (row != null) {
					addViewInLayout(row, -1, row.getLayoutParams(), true);
				}
				row = makeRow();
			}
			
			View child = getChild(i);
			row.addView(child);
		}
		addViewInLayout(row, -1, row.getLayoutParams(), true);
	}
	
	private boolean isNewRowStart(int position) {
		return position % columnCount == 0;
	}
	
	private LinearLayout makeRow() {
		LinearLayout row = new LinearLayout(getContext());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		row.setOrientation(LinearLayout.HORIZONTAL);
		row.setLayoutParams(params);
		return row;
	}
	
	private View getChild(int position) {
		View child = adapter.getView(position, null, this);
		LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
		child.setLayoutParams(params);
		setChildBackground(child, position);
		return child;
	}
	
	private void setChildBackground(View child, int position) {
		switch (position) {
		case 0:
		case 2:
			child.setBackgroundResource(R.drawable.drawer_right_bottom_selector);
			break;
			
		case 1:
		case 3:
			child.setBackgroundResource(R.drawable.drawer_bottom_selector);
			break;
			
		case 4:
			child.setBackgroundResource(R.drawable.drawer_right_selector);
			break;
			
		case 5:
			child.setBackgroundResource(R.drawable.drawer_selector);
			break;

		default:
			break;
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
