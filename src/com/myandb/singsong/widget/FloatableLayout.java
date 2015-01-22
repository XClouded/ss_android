package com.myandb.singsong.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;

public class FloatableLayout extends RelativeLayout {
	
	private ListAdapter adapter;
	private int horizontalSpacing;
	private int verticalSpacing;
	private DataSetObserver observer;

	public FloatableLayout(Context context) {
		super(context);
	}

	public FloatableLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setHorizontalSpacing(int resId) {
		horizontalSpacing = getResources().getDimensionPixelSize(resId);
	}
	
	public void setVerticalSpacing(int resId) {
		verticalSpacing = getResources().getDimensionPixelSize(resId);
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
		
		getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi")
			@Override
			public void onGlobalLayout() {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					getViewTreeObserver().removeOnGlobalLayoutListener(this);
				} else {
					getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
				setupChildren();
			}
		});
	}

	public void setupChildren() {
		removeAllViews();
		
		if (adapter == null) {
			return;
		}
		
		int layoutWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
		int rowWidthSum = 0;
		int countAtLine = 0;
		View firstChildAtBeforeLine = null;
		View beforeChildAtLine = null;
		for (int i = 0, l = adapter.getCount(); i < l; i++) {
			View child = adapter.getView(i, null, this);
			child.setId(i + 1);
			child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			LayoutParams lp = (LayoutParams) child.getLayoutParams();
			
			int totalHorizontalSpacing = countAtLine * horizontalSpacing;
			int expectedWith = rowWidthSum + totalHorizontalSpacing + child.getMeasuredWidth();
			
			if (expectedWith > layoutWidth || firstChildAtBeforeLine == null) {
				if (firstChildAtBeforeLine != null) {
					lp.topMargin = verticalSpacing;
					lp.addRule(RelativeLayout.BELOW, firstChildAtBeforeLine.getId());
				}
				firstChildAtBeforeLine = child;
				rowWidthSum = 0;
				countAtLine = 0;
			} else {
				lp.leftMargin = horizontalSpacing;
				if (beforeChildAtLine != null) {
					lp.addRule(RelativeLayout.ALIGN_TOP, beforeChildAtLine.getId());
					lp.addRule(RelativeLayout.RIGHT_OF, beforeChildAtLine.getId());
				}
			}
			
			beforeChildAtLine = child;
			countAtLine++;
			rowWidthSum += child.getMeasuredWidth();
			addView(child, lp);
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
