package com.myandb.singsong.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;

public class PausableTranslateAnimation extends TranslateAnimation {
	
	private boolean paused;
	private long elapsedAtPause = 0;

	public PausableTranslateAnimation(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PausableTranslateAnimation(float fromXDelta, float toXDelta,
			float fromYDelta, float toYDelta) {
		super(fromXDelta, toXDelta, fromYDelta, toYDelta);
	}

	public PausableTranslateAnimation(int fromXType, float fromXValue,
			int toXType, float toXValue, int fromYType, float fromYValue,
			int toYType, float toYValue) {
		super(fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue, toYType,
				toYValue);
	}
	
	@Override
	public boolean getTransformation(long currentTime, Transformation outTransformation) {
		if (paused && elapsedAtPause == 0) {
			elapsedAtPause = currentTime - getStartTime();
		}
		if (paused) {
			setStartTime(currentTime - elapsedAtPause);
		}
		return super.getTransformation(currentTime, outTransformation);
	}

	public void pause() {
		elapsedAtPause = 0;
		paused = true;
	}
	
	public void resume() {
		paused = false;
	}

}
