package com.myandb.singsong.widget;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

public class SlideAnimation extends Animation {
	
	private RelativeLayout.LayoutParams params;
	private View view;
	private int topMargin;
	private boolean stretched;
	
	public SlideAnimation(View view, int topMargin, View anchor) {
		this.view = view;
		this.topMargin = topMargin;
		
		params = (RelativeLayout.LayoutParams) view.getLayoutParams();
		params.topMargin = topMargin;
		params.addRule(RelativeLayout.BELOW, anchor.getId());
		view.setLayoutParams(params);
		setInterpolator(new DecelerateInterpolator());
		setDuration(300);
		setRepeatCount(0);
		
		stretched = true;
	}
	
	public void changeDirection() {
		stretched = !stretched;
	}
	
	public boolean isStretched() {
		return stretched;
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		if (stretched) {
			params.topMargin = (int) (interpolatedTime * topMargin);
		} else {
			params.topMargin = topMargin - (int) (interpolatedTime * topMargin);
		}
		
		view.setLayoutParams(params);
	}
	
}