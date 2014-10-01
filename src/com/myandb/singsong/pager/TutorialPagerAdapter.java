package com.myandb.singsong.pager;

import com.myandb.singsong.R;
import com.myandb.singsong.util.ImageHelper.BitmapBuilder;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class TutorialPagerAdapter extends ViewPagerAdapter {
	
	private static final int ITEM_NUM = 5;
	
	private BitmapBuilder bitmapBuilder;

	public TutorialPagerAdapter(Context context) {
		super(context);
		
		bitmapBuilder = new BitmapBuilder();
	}

	@Override
	public int getCount() {
		return TutorialPagerAdapter.ITEM_NUM;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View view = getInflater().inflate(R.layout.page_tutorial, null);
		ImageView ivSnapShot = (ImageView) view.findViewById(R.id.iv_tutorial_snapshot);
		int resourceId = -1;
		
		switch (position) {
		case 0:
			resourceId = R.drawable.tutorial1;
			
			break;
			
		case 1:
			resourceId = R.drawable.tutorial2;
			
			break;
			
		case 2:
			resourceId = R.drawable.tutorial3;
			
			break;
			
		case 3:
			resourceId = R.drawable.tutorial4;
			
			break;
			
		case 4:
			resourceId = R.drawable.tutorial5;
			
			break;
			
		}
		
		if (resourceId > 0) {
			Bitmap bitmap = bitmapBuilder.setSource(getContext().getResources(), resourceId)
										 .enableCrop(false)
										 .setOutputSize(400)
										 .build();
			
			if (bitmap != null) {
				ivSnapShot.setImageBitmap(bitmap);
			}
		}
		
		container.addView(view, 0);
		
		return view;
	}

}
