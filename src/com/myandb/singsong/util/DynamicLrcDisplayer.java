package com.myandb.singsong.util;

import java.util.ArrayList;
import java.util.List;

import com.myandb.singsong.util.Lrc.Line;
import com.myandb.singsong.util.Lrc.Line.Type;
import com.myandb.singsong.util.Lrc.Word;

import android.content.Context;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

public class DynamicLrcDisplayer extends LrcDisplayer {
	
	private Handler lineHandler;
	private LayoutParams lp;
	
	public DynamicLrcDisplayer(Context context) {
		super(context);
		lineHandler = new Handler();
	}

	@Override
	protected View getLine(Line line) throws Exception {
		if (lp == null) {
			lp = new LayoutParams(getWrapperWidth(), LayoutParams.WRAP_CONTENT);
		}
		
		FrameLayout activeLineWrapper = new FrameLayout(getContext());
		TextView forward = getForwardTextView(line);
		activeLineWrapper.addView(forward);
		if (line.isDynamicWords()) {
			List<WidthChunk> widths = setDynamicWordsAndGetWidthChunkList(forward, line);
			int lineInitialWidth = getLineLeftMargin(forward);
			updateViewWidth(activeLineWrapper, lineInitialWidth);
			List<WidthChunk> converted = convertWidthChunkList(widths, lineInitialWidth);
			Animation animation = new ContinuousExpandAnimation(activeLineWrapper, converted);
			animation.setDuration(getTotalDelay(widths));
			activeLineWrapper.setTag(animation);
		} else {
			forward.setText(line.toString());
			activeLineWrapper.setLayoutParams(lp);
		}
		forward.setLayoutParams(lp);
		activeLineWrapper.setVisibility(View.GONE);
		
		FrameLayout lineWrapper = new FrameLayout(getContext());
		lineWrapper.addView(getBehindTextView(line));
		lineWrapper.addView(activeLineWrapper);
		lineWrapper.setTag(line);
		
		return lineWrapper;
	}
	
	private TextView getBehindTextView(Line line) {
		Type type = line.getType();
		TextView behind = new TextView(getContext());
		behind.setLayoutParams(lp);
		behind.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize());
		behind.setGravity(Gravity.CENTER_HORIZONTAL);
		behind.setTextColor(getInActiveColor(type));
		behind.setText(line.toString());
		return behind;
	}
	
	private TextView getForwardTextView(Line line) {
		Type type = line.getType();
		TextView forward = new TextView(getContext());
		forward.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize());
		forward.setGravity(Gravity.CENTER_HORIZONTAL);
		forward.setTextColor(getActiveColor(type));
		return forward;
	}
	
	private List<WidthChunk> setDynamicWordsAndGetWidthChunkList(TextView forward, Line line) {
		List<WidthChunk> widths = new ArrayList<WidthChunk>();
		List<Word> timeWords = line.getTimeWords();
		int totalDelay = 0;
		String totalString = "";
		for (int i = 0, l = timeWords.size(); i < l; i++) {
			Word word = timeWords.get(i);
			measureText(forward, word.toString());
			totalString += word.toString();
			totalDelay += (int) word.getDelay();
			widths.add(new WidthChunk(totalDelay, forward.getMeasuredWidth()));
			
			Word nextWord = getNext(timeWords, i);
			if (nextWord != null) {
				int pauseDelay = getPauseDelay(word, nextWord);
				if (pauseDelay > 0) {
					totalDelay += pauseDelay;
					widths.add(new WidthChunk(totalDelay, 0));
				}
			}
		}
		measureText(forward, totalString);
		return widths;
	}
	
	private void updateViewWidth(View view, int width) {
		LayoutParams layoutParams = new LayoutParams(width, LayoutParams.WRAP_CONTENT);
		view.setLayoutParams(layoutParams);
	}
	
	private void measureText(TextView textView, String text) {
		textView.setText(text);
		textView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
	}
	
	private <T> T getNext(List<T> list, int index) {
		if ((index + 1) < list.size() && (index + 1) >= 0) {
			return list.get(index + 1);
		}
		return null;
	}
	
	private int getPauseDelay(Word current, Word next) {
		return (int) (next.getStartTime() - current.getEndTime());
	}
	
	private int getLineLeftMargin(View line) {
		return (getWrapperWidth() - line.getMeasuredWidth()) / 2;
	}
	
	private List<WidthChunk> convertWidthChunkList(List<WidthChunk> rawList, int baseWidth) {
		List<WidthChunk> converted = new ArrayList<WidthChunk>();
		long totalDelay = getTotalDelay(rawList);
		for (int i = 0, l = rawList.size(); i < l; i++) {
			float interpolatedTime = rawList.get(i).delayLong / ((float) totalDelay);
			WidthChunk chunk = new WidthChunk(interpolatedTime, rawList.get(i).expandWidth, baseWidth);
			converted.add(chunk);
			
			baseWidth += rawList.get(i).expandWidth;
		}
		return converted;
	}
	
	private long getTotalDelay(List<WidthChunk> rawList) {
		return rawList.get(rawList.size() - 1).delayLong;
	}

	@Override
	public void start() {
		super.start();
		runNextLine(lineHandler, 0);
	}
	
	private void runNextLine(final Handler handler, final int index) {
		final ViewGroup lineWrapper = (ViewGroup) getWrapper().getChildAt(index);
		if (lineWrapper == null) {
			return;
		}
		
		final Line line = (Line) lineWrapper.getTag();
		long delay = line.getStartTime();
		
		final ViewGroup previousLineWrapper = (ViewGroup) getWrapper().getChildAt(index - 1);
		if (previousLineWrapper != null) {
			Line previousLine = (Line) previousLineWrapper.getTag();
			delay -= previousLine.getStartTime();
		}
		
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				setActiveLineShown(previousLineWrapper, false);
				scrollToActiveLine(index);
				animateActiveLine(lineWrapper, line);
				runNextLine(handler, index + 1);
			}
		}, delay);
	}
	
	private void setActiveLineShown(ViewGroup lineWrapper, boolean shown) {
		if (lineWrapper != null) {
			View activeLineWrapper = lineWrapper.getChildAt(1);
			if (activeLineWrapper.isShown() == shown) {
				return;
			}
			if (shown) {
				activeLineWrapper.setVisibility(View.VISIBLE);
			} else {
				activeLineWrapper.setVisibility(View.GONE);
			}
		}
	}
	
	private void scrollToActiveLine(int index) {
		if (getWrapper().getParent() instanceof ScrollView) {
			int scrollLength = 0;
			int bottomMargin = getLineBottomMargin();
			for (int i = index - 1; i >= 0; i--) {
				View child = getWrapper().getChildAt(i);
				scrollLength += child.getHeight();
				scrollLength += bottomMargin;
			}
			
			((ScrollView) getWrapper().getParent()).scrollTo(0, scrollLength);
		}
	}
	
	private void animateActiveLine(ViewGroup lineWrapper, Line line) {
		setActiveLineShown(lineWrapper, true);
		if (line.isDynamicWords()) {
			View activeLineWrapper = lineWrapper.getChildAt(1);
			Animation animation = (Animation) activeLineWrapper.getTag();
			activeLineWrapper.startAnimation(animation);
		}
	}

	@Override
	public void stop() {
		super.stop();
		
		if (lineHandler != null) {
			lineHandler.removeCallbacksAndMessages(null);
		}
	}

	@Override
	protected void initializeDisplay() {
		super.initializeDisplay();
		for (int i = 0, l = getWrapper().getChildCount(); i < l; i++) {
			ViewGroup child = (ViewGroup) getWrapper().getChildAt(i);
			setActiveLineShown(child, false);
		}
	}
	
	public static class ContinuousExpandAnimation extends Animation {
		
		private View view;
		private LayoutParams lp;
		private List<WidthChunk> converted;
		private int cachedIndex;
		
		public ContinuousExpandAnimation(View view, List<WidthChunk> converted) {
			this.view = view;
			this.lp = (LayoutParams) view.getLayoutParams();
			this.converted = converted;
			setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					cachedIndex = 0;
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {}
				
				@Override
				public void onAnimationEnd(Animation animation) {}
			});
		}

		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			for (int i = cachedIndex, l = converted.size(); i < l; i++) {
				WidthChunk chunk = converted.get(i);
				if (interpolatedTime < chunk.delayFloat) {
					WidthChunk previousChunk = getPreviousItem(converted, i);
					updateWidth(chunk, previousChunk, interpolatedTime);
					cachedIndex = i;
					break;
				}
			}
		}
		
		private <T> T getPreviousItem(List<T> list, int index) {
			if ((index - 1) < list.size() && (index - 1) >= 0) {
				return list.get(index - 1);
			}
			return null;
		}
		
		private void updateWidth(WidthChunk chunk, WidthChunk previous, float interpolatedTime) {
			float convertedInterpolatedTime = interpolatedTime;
			if (previous != null) {
				convertedInterpolatedTime = (interpolatedTime - previous.delayFloat) / (chunk.delayFloat - previous.delayFloat);
			} else {
				convertedInterpolatedTime = interpolatedTime / chunk.delayFloat;
			}
			lp.width = (int) (chunk.baseWidth + chunk.expandWidth * convertedInterpolatedTime);
			view.requestLayout();
		}
		
	}
	
	private static class WidthChunk {
		
		public long delayLong;
		public float delayFloat;
		public int expandWidth;
		public int baseWidth;
		
		public WidthChunk(long delayLong, int expandWidth) {
			this.delayLong = delayLong;
			this.expandWidth = expandWidth;
		}
		
		public WidthChunk(float delay, int expandWidth, int baseWidth) {
			this.delayFloat = delay;
			this.expandWidth = expandWidth;
			this.baseWidth = baseWidth;
		}
		
	}

}
