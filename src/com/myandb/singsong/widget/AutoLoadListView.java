package com.myandb.singsong.widget;

import com.myandb.singsong.adapter.AutoLoadAdapter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class AutoLoadListView extends ListView {
	
	private AutoLoadAdapter<?> mAdapter;
	
	public AutoLoadListView(Context context) {
		super(context);
	}

	public AutoLoadListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AutoLoadListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		
		mAdapter = (AutoLoadAdapter<?>) adapter;
		
		initializeScroll();
	}
	
	public void initializeScroll() {
		setOnScrollListener(new OnScrollListener() {
			
			private int visibleThreshold = 10; 
	        private int previousTotal = 0;
	        private boolean loading = true;
	        
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) { }
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (loading) {
	                if (totalItemCount > previousTotal) {
	                    loading = false;
	                    previousTotal = totalItemCount;
	                }
	            }
	            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
	            	mAdapter.executeQuery();
	            	loading = true;
	            }
			}
		});
	}

}
