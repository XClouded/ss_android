package com.myandb.singsong.widget;

import com.myandb.singsong.pager.InfinitePagerAdapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

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

}