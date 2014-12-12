package com.myandb.singsong.widget;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public class FadingActionBarHelper implements OnScrollListener {
	
	private static final int DEFAULT_VISIBLE_POSITION = 4000;
	
	private Drawable backgroundDrawable;
	private ActionBar actionBar;
	private CharSequence title;
	private int backgroundResId;
	private int fullyVisiblePosition;
	private float maxAlpha;
	private float visibleThreshold;
	
	public FadingActionBarHelper setBackground(Drawable drawable) {
		return setBackground(drawable, 1f);
	}
	
	public FadingActionBarHelper setBackground(Drawable drawable, float maxAlpha) {
		this.backgroundDrawable = drawable;
		this.maxAlpha = getProperAlpha(maxAlpha);
		return this;
	}
	
	public FadingActionBarHelper setBackground(int resId) {
		return setBackground(resId, 1f);
	}
	
	public FadingActionBarHelper setBackground(int resId, float maxAlpha) {
		this.backgroundResId = resId;
		this.maxAlpha = getProperAlpha(maxAlpha);
		return this;
	}
	
	public FadingActionBarHelper setTitle(CharSequence title) {
		this.title = title;
		return this;
	}
	
	private float getProperAlpha(float alpha) {
		return Math.max(Math.min(alpha, 1f), 0f);
	}
	
	public FadingActionBarHelper setFullyVisiblePosition(int position) {
		this.fullyVisiblePosition = Math.max(position, 0);
		return this;
	}
	
	public void initialize(Activity activity) {
		if (activity instanceof ActionBarActivity) {
			if (backgroundDrawable == null) {
				backgroundDrawable = activity.getResources().getDrawable(backgroundResId);
			}
			actionBar = ((ActionBarActivity) activity).getSupportActionBar();
			actionBar.setBackgroundDrawable(backgroundDrawable);
			
			fullyVisiblePosition = fullyVisiblePosition > 0 ? fullyVisiblePosition : DEFAULT_VISIBLE_POSITION;
			visibleThreshold = (float) (fullyVisiblePosition * maxAlpha);
		}
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		View topChild = view.getChildAt(0);
		int position = 0;
		if (topChild != null) {
			position = -topChild.getTop() + view.getFirstVisiblePosition() * topChild.getHeight();
        }
		
		if (position <= visibleThreshold) {
			fade(position);
		}
	}
	
	private void fade(int position) {
		float alphaInFloat = (float) Math.max(position, 0) / fullyVisiblePosition;
		int alphaInInt = (int) (alphaInFloat * 255);
		
		fadeBackground(alphaInInt);
		fadeTitle(alphaInInt);
	}
	
	private void fadeBackground(int alpha) {
		if (backgroundDrawable != null) {
			backgroundDrawable.setAlpha(alpha);
		}
	}
	
	private void fadeTitle(int alpha) {
		if (title != null) {
			SpannableStringBuilder builder = new SpannableStringBuilder(title);
			builder.setSpan(new ForegroundColorSpan(Color.argb(alpha, 0, 0, 0)), 0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			actionBar.setTitle(builder);
		}
	}

}
