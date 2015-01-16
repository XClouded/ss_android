package com.myandb.singsong.widget;

import com.myandb.singsong.pager.InfinitePagerAdapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class InfiniteViewPager extends ViewPager {

    public InfiniteViewPager(Context context) {
        super(context);
    }

    public InfiniteViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        setCurrentItem(0);
    }

    @Override
	public int getCurrentItem() {
    	if (getAdapter() instanceof InfinitePagerAdapter) {
    		InfinitePagerAdapter infAdapter = (InfinitePagerAdapter) getAdapter();
    		return super.getCurrentItem() % infAdapter.getRealCount();
    	}
		return super.getCurrentItem();
	}

	@Override
    public void setCurrentItem(int item) {
        item = getOffsetAmount() + (item % getAdapter().getCount());
        super.setCurrentItem(item);
    }

    private int getOffsetAmount() {
        if (getAdapter() instanceof InfinitePagerAdapter) {
            InfinitePagerAdapter infAdapter = (InfinitePagerAdapter) getAdapter();
            
            return infAdapter.getRealCount() * 100;
        } else {
            return 0;
        }
    }

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		int height = 0;
		for (int i = 0, l = getChildCount(); i < l; i++) {
			View child = getChildAt(i);
			child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			int h = child.getMeasuredHeight();
			height = Math.max(h, height);
		}
		
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}