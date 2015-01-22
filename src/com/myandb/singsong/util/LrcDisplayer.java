package com.myandb.singsong.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.myandb.singsong.R;
import com.myandb.singsong.util.Lrc.Line;
import com.myandb.singsong.util.Lrc.Line.Type;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.LinearLayout.LayoutParams;

public abstract class LrcDisplayer {
	
	private Lrc lrc;
	private Context context;
	private LinearLayout.LayoutParams lineLayout;
	private ViewGroup wrapper;
	private Type previousType;
	private OnTypeChangeListener typeChangeListener;
	private List<Long> countTimeTable;
	private Handler countHandler;
	
	private boolean initialized;
	private float textSize;
	private int colorMaleActive;
	private int colorMaleInActive;
	private int colorFemaleActive;
	private int colorFemaleInActive;
	private int colorDualActive;
	private int colorDualInActive;
	private int colorHint;
	private Iterator<Long> countIterator;
	private long previousCountTime;
	
	public LrcDisplayer(Context context) {
		this.context = context;
		this.initialized = false;
		this.countTimeTable = new ArrayList<Long>();
		this.countHandler = new Handler();
		
		this.textSize = context.getResources().getDimension(R.dimen.text_main_content);
		this.lineLayout = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		this.lineLayout.bottomMargin = getDimensionPixelSize(R.dimen.margin);
		
		this.colorMaleActive = getColor(R.color.font_male_active);
		this.colorMaleInActive = getColor(R.color.font_male_inactive);
		this.colorFemaleActive = getColor(R.color.font_female_active);
		this.colorFemaleInActive = getColor(R.color.font_female_inactive);
		this.colorDualActive = getColor(R.color.font_dual_active);
		this.colorDualInActive = getColor(R.color.font_dual_inactive);
		this.colorHint = getColor(R.color.font_white);
	}
	
	private int getDimensionPixelSize(int id) {
		return context.getResources().getDimensionPixelSize(id);
	}
	
	private int getColor(int id) {
		return context.getResources().getColor(id);
	}
	
	public float getTextSize() {
		return textSize;
	}
	
	public int getActiveColor(Type type) {
		switch (type) {
		case MALE:
			return colorMaleActive;
		case FEMALE:
			return colorFemaleActive;
		case DUAL:
			return colorDualActive;
		case HINT:
		default:
			return colorHint;
		}
	}
	
	public int getInActiveColor(Type type) {
		switch (type) {
		case MALE:
			return colorMaleInActive;
		case FEMALE:
			return colorFemaleInActive;
		case DUAL:
			return colorDualInActive;
		case HINT:
		default:
			return colorHint;
		}
	}
	
	public Context getContext() {
		return context;
	}
	
	public ViewGroup getWrapper() {
		return wrapper;
	}
	
	public int getLineBottomMargin() {
		return lineLayout.bottomMargin;
	}
	
	public int getWrapperWidth() {
		int padding = wrapper.getPaddingLeft() + wrapper.getPaddingRight();
		return wrapper.getMeasuredWidth() - padding;
	}
	
	public LrcDisplayer setOnTypeChangeListener(OnTypeChangeListener listener) {
		this.typeChangeListener = listener;
		return this;
	}
	
	public LrcDisplayer setWrapper(ViewGroup wrapper) {
		this.wrapper = wrapper;
		return this;
	}
	
	public LrcDisplayer setLrc(Lrc lrc) {
		this.lrc = lrc;
		return this;
	}
	
	public float getSampleSkipSecond() {
		Line line = lrc.getTimeLines().get(0);
		if (line.getType().equals(Type.GO)) {
			line = lrc.getTimeLines().get(1);
		}
		return line.getStartTime() / 1000f;
	}
	
	public void initialize() {
		if (initialized) {
			return;
		}
		
		try {
			for (Line line : lrc.getTimeLines()) {
				Type type = line.getType();
				if (type.equals(Type.GO)) {
					addCountTime(line.getStartTime());
				} else {
					addLine(line);
				}
			}
			initialized = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			requestInitialDisplay();
		}
	}
	
	private void addCountTime(long countTime) {
		countTimeTable.add(countTime);
	}
	
	private void addLine(Line line) throws Exception {
		View lineView = getLine(line);
		lineView.setLayoutParams(lineLayout);
		wrapper.addView(lineView);
	}
	
	public void requestInitialDisplay() {
		if (initialized) {
			initializeDisplay();
		}
	}
	
	public void start() {
		stop();
		requestInitialDisplay();
		startCount();
		previousType = Type.NULL;
	}
	
	private void startCount() {
		countIterator = countTimeTable.iterator();
		previousCountTime = 0;
		updateCount();
	}
	
	private void updateCount() {
		if (countIterator.hasNext()) {
			long countTime = countIterator.next();
			long delay = countTime - previousCountTime;
			previousCountTime = countTime;
			countHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					dispatchType(Type.GO, true);
					updateCount();
				}
			}, delay);
		}
	}
	
	public void stop() {
		countHandler.removeCallbacksAndMessages(null);
	}
	
	protected void dispatchType(Type type) {
		dispatchType(type, false);
	}
	
	private void dispatchType(Type type, boolean force) {
		if (typeChangeListener == null) {
			return;
		}
		
		if (force) {
			typeChangeListener.onChange(type);
		} else {
			if (!previousType.equals(type)) {
				typeChangeListener.onChange(type);
			}
		}
		
		previousType = type;
	}
	
	protected abstract View getLine(Line line) throws Exception;
	
	protected void initializeDisplay() {
		scrollToTop();
	}
	
	private void scrollToTop() {
		if (wrapper.getParent() instanceof ScrollView) {
			((ScrollView) wrapper.getParent()).scrollTo(0, 0);
		}
	}
	
	public interface OnTypeChangeListener {
		
		public void onChange(Type type);
		
	}

}
