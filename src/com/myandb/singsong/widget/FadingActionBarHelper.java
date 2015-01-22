package com.myandb.singsong.widget;

import com.myandb.singsong.R;

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
import android.widget.ImageView;
import android.widget.AbsListView.OnScrollListener;

public class FadingActionBarHelper implements OnScrollListener {
	
	private static final int DEFAULT_VISIBLE_POSITION = 4000;
	private static final int MAX_ALPHA = 255;
	private static final int ICON_TRANSITION_ALPHA = 130;
	
	private Drawable backgroundDrawable;
	private Drawable homeDrawable;
	private ImageView overflow;
	private ActionBar actionBar;
	private CharSequence title;
	private int headerHeight;
	private int backgroundResId;
	private int fullyVisiblePosition;
	private int maxAlpha;
	private int previousAlpha = -1;
	private int currentOverflowImageRes;
	private float visibleThreshold;
	
	public FadingActionBarHelper setBackground(Drawable drawable) {
		return setBackground(drawable, MAX_ALPHA);
	}
	
	public FadingActionBarHelper setBackground(Drawable drawable, int maxAlpha) {
		this.backgroundDrawable = drawable;
		this.maxAlpha = getProperAlpha(maxAlpha);
		return this;
	}
	
	public FadingActionBarHelper setBackground(int resId) {
		return setBackground(resId, MAX_ALPHA);
	}
	
	public FadingActionBarHelper setBackground(int resId, int maxAlpha) {
		this.backgroundResId = resId;
		this.maxAlpha = getProperAlpha(maxAlpha);
		return this;
	}
	
	public FadingActionBarHelper setOverflow(ImageView overflow) {
		this.overflow = overflow;
		return this;
	}
	
	public FadingActionBarHelper setTitle(CharSequence title) {
		this.title = title;
		return this;
	}
	
	private int getProperAlpha(int alpha) {
		return Math.max(Math.min(alpha, MAX_ALPHA), 0);
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
			if (homeDrawable == null) {
				homeDrawable = activity.getResources().getDrawable(R.drawable.ic_action_back);
			}
			actionBar = ((ActionBarActivity) activity).getSupportActionBar();
			actionBar.setBackgroundDrawable(backgroundDrawable);
			actionBar.setHomeAsUpIndicator(homeDrawable);
			
			fullyVisiblePosition = fullyVisiblePosition > 0 ? fullyVisiblePosition : DEFAULT_VISIBLE_POSITION;
			visibleThreshold = (float) (fullyVisiblePosition * maxAlpha / MAX_ALPHA);
		}
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		int position = 0;
		
		View topChild = view.getChildAt(0);
		if (topChild != null) {
			if (headerHeight == 0) {
				headerHeight = topChild.getHeight();
			}
			
			if (isHeaderVisible(firstVisibleItem)) {
				position = -topChild.getTop();
			} else {
				position = -topChild.getTop() + Math.max(firstVisibleItem - 1, 0) * topChild.getHeight() + headerHeight;
			}
		}
		
		fade(position);
	}
	
	private boolean isHeaderVisible(int firstVisibleItem) {
		return firstVisibleItem == 0;
	}
	
	private void fade(int position) {
		float ratio = 0f;
		int newAlpha = maxAlpha;
		if (position <= visibleThreshold) {
			ratio = (float) Math.max(position, 0) / fullyVisiblePosition;
			newAlpha = (int) (ratio * MAX_ALPHA);
		}
		
		if (newAlpha != previousAlpha) {
			fadeBackground(newAlpha);
			fadeHome(newAlpha);
			fadeTitle(newAlpha);
		}
		fadeOverflow(newAlpha);
		
		previousAlpha = newAlpha;
	}
	
	private void fadeBackground(int alpha) {
		if (backgroundDrawable == null) {
			return;
		}
		
		backgroundDrawable.setAlpha(alpha);
	}
	
	private void fadeHome(int alpha) {
		if (homeDrawable == null) {
			return;
		}
	
		homeDrawable.setAlpha(alpha);
		actionBar.setHomeAsUpIndicator(homeDrawable);
	}
	
	private void fadeTitle(int alpha) {
		if (title == null) {
			return;
		}
		
		if (alpha == MAX_ALPHA) {
			actionBar.setTitle(title);
		} else {
			SpannableStringBuilder builder = new SpannableStringBuilder(title);
			builder.setSpan(new ForegroundColorSpan(Color.argb(alpha, 0, 0, 0)), 0, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			actionBar.setTitle(builder);
		}
	}
	
	private void fadeOverflow(int alpha) {
		if (overflow == null) {
			return;
		}
		
		if (alpha > ICON_TRANSITION_ALPHA) {
			setOverflowImageResource(R.drawable.ic_overflow_sky);
		} else {
			setOverflowImageResource(R.drawable.ic_overflow_white);
		}
	}
	
	private void setOverflowImageResource(int imageRes) {
		if (imageRes == currentOverflowImageRes) {
			return;
		}
		
		overflow.setImageResource(imageRes);
		currentOverflowImageRes = imageRes;
	}

}
