package com.myandb.singsong.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.myandb.singsong.R;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class LrcDisplayer {
	
	private int helpCount = 0;
	private long preSysTime = 0;
	private int timeIndex = 0;
	private long delayed = 0;
	private boolean isHelperShowing = false;
	
	private List<Long> lrcTime;
	private HashMap<Long, String> types;
	private HashMap<Long, String> infos;
	private HashMap<Long, Integer> textViewIndexes;
	public Handler handler = new Handler();
	private Runnable lyricRunnable;
	private CountRunnable countRunnable;
	private ScrollView scrollView;
	private TextSwitcher switcher;
	private ViewGroup lyricWrapper;
	private boolean dynamic;
	private Context context;
	private LinearLayout.LayoutParams layoutParams;
	private float textSize;
	private int colorMaleActive;
	private int colorMaleInActive;
	private int colorFemaleActive;
	private int colorFemaleInActive;
	private int colorDualActive;
	private int colorDualInActive;
	
	public LrcDisplayer(File source, Context context) {
		try {
			LrcParser.read(source);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		this.lrcTime = LrcParser.getTimeset();
		this.infos = LrcParser.getInfos();
		this.types = LrcParser.getTypes();
		this.textViewIndexes = new HashMap<Long, Integer>();
		
		this.context = context;
		this.textSize = context.getResources().getDimension(R.dimen.text_bigger);
		this.layoutParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		this.layoutParams.bottomMargin = (int) context.getResources().getDimension(R.dimen.margin);
		
		this.colorMaleActive = context.getResources().getColor(R.color.font_male_active);
		this.colorMaleInActive = context.getResources().getColor(R.color.font_male_inactive);
		this.colorFemaleActive = context.getResources().getColor(R.color.font_female_active);
		this.colorFemaleInActive = context.getResources().getColor(R.color.font_female_inactive);
		this.colorDualActive = context.getResources().getColor(R.color.font_dual_active);
		this.colorDualInActive = context.getResources().getColor(R.color.font_dual_inactive);
	}
	
	public LrcDisplayer setScrollView(ScrollView scrollView) {
		this.scrollView = scrollView;
		
		return this;
	}
	
	public LrcDisplayer setLyricWrapper(ViewGroup wrapper) {
		this.lyricWrapper = wrapper;
		
		return this;
	}
	
	public LrcDisplayer setTextSwitcher(TextSwitcher switcher) {
		this.switcher = switcher;
		
		return this;
	}
	
	public LrcDisplayer setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
		
		return this;
	}
	
	public boolean isDynamic() {
		return dynamic;
	}
	
	public Context getContext() {
		return context;
	}
	
	public void ready() {
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				String type;
				String content;
				Long time;
				
				for (int i = 0, l = lrcTime.size(); i < l; i++) {
					time = lrcTime.get(i);
					type = types.get(time);
					content = infos.get(time);
					
					if (type != null && isTypeLyric(type)) {
						textViewIndexes.put(time, textViewIndexes.size());
						lyricWrapper.addView(createLyricLine(content, type));
					}
				}
			}
		});
	}
	
	private TextView createLyricLine(String content, String type) {
		TextView lyric = new TextView(getContext());
		lyric.setLayoutParams(layoutParams);
		lyric.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		lyric.setGravity(Gravity.CENTER_HORIZONTAL);
		
		if (isDynamic()) {
			if (isTypeMale(type)) {
				lyric.setTextColor(colorMaleInActive);
			} else if (isTypeFemale(type)) {
				lyric.setTextColor(colorFemaleInActive);
			} else if (isTypeDual(type)) {
				lyric.setTextColor(colorDualInActive);
			}
		} else {
			if (isTypeMale(type)) {
				lyric.setTextColor(colorMaleActive);
			} else if (isTypeFemale(type)) {
				lyric.setTextColor(colorFemaleActive);
			} else if (isTypeDual(type)) {
				lyric.setTextColor(colorDualActive);
			}
		}
		
		lyric.setText(content);
		lyric.setTag(type);
		
		return lyric;
	}
	
	private boolean isTypeLyric(String type) {
		return isTypeMale(type) || isTypeFemale(type) || isTypeDual(type);
	}
	
	private boolean isTypeMale(String type) {
		return type.equals("M") || type.equals("m");
	}
	
	private boolean isTypeFemale(String type) {
		return type.equals("F") || type.equals("f");
	}
	
	private boolean isTypeDual(String type) {
		return type.equals("D") || type.equals("d");
	}
	
	private boolean isTypeCounter(String type) {
		return type.equals("G") || type.equals("g");
	}
	
	public void start() {
		handler.removeCallbacksAndMessages(null);
		
		timeIndex = 0;
		
		update();
	}
	
	private void update() {
		if (timeIndex < lrcTime.size()) {
			lyricRunnable = new Runnable() {
				
				private Handler h;
				
				@Override
				public void run() {
					final Long currentTime = lrcTime.get(timeIndex);
					final String type = types.get(currentTime);
					final Integer textViewIndex = textViewIndexes.get(currentTime);
					
					if (isDynamic()) {
						if (textViewIndex != null) {
							TextView currentTextView = (TextView) lyricWrapper.getChildAt(textViewIndex);
							activateTextView(currentTextView);
							
							if (textViewIndex > 0) {
								TextView previousTextView = (TextView) lyricWrapper.getChildAt(textViewIndex - 1);
								deactivateTextView(previousTextView);
							}
							
							int scrollLength = 0;
							TextView textView;
							for (int i = textViewIndex - 1; i >= 0; i--) {
								textView = (TextView) lyricWrapper.getChildAt(i);
								scrollLength += textView.getHeight();
								scrollLength += layoutParams.bottomMargin;
							}
							
							scrollView.scrollTo(0, scrollLength);
						}
					}
					
					if (isTypeCounter(type) && !isHelperShowing) {
						helpCount = 0;
						isHelperShowing = true;
						
						if (h == null) {
							h = new Handler();
						}
						
						updateSwitcher(h);
					}
					
					if (timeIndex < lrcTime.size() - 1) {
						adjustDelay();
						
						timeIndex++;
						
						update();
					}
				}
				
				private void adjustDelay() {
					final long currentSysTime = System.currentTimeMillis();
					final long realDelayed = currentSysTime - preSysTime; 
					final long differ = delayed - realDelayed;
					
					delayed = lrcTime.get(timeIndex + 1) - lrcTime.get(timeIndex);
					delayed += differ;
				}
				
				private void updateSwitcher(final Handler h) {
					countRunnable = new CountRunnable() {
						
						@Override
						public void run() {
							if (switcher == null) {
								return;
							}
							
							switch (helpCount) {
							case 0:
								switcher.setVisibility(View.VISIBLE);
								switcher.setText("3");
								updateSwitcher(h);
								break;

							case 1:
								switcher.setText("2");
								updateSwitcher(h);
								break;
								
							case 2:
								switcher.setText("1");
								updateSwitcher(h);
								break;
								
							case 3:
								switcher.setText("GO!");
								updateSwitcher(h);
								break;
								
							case 4:
								switcher.setText("");
								switcher.setVisibility(View.GONE);
								isHelperShowing = false;
								break;
							}
							
							helpCount++;
						}

						@Override
						public void stop() {
							if (h != null) {
								h.removeCallbacksAndMessages(null);
							}
						}
					};
					
					h.postDelayed(countRunnable, 1000);
				}
			};
			
			if (timeIndex == 0) {
				delayed = lrcTime.get(timeIndex);
			}
			
			preSysTime = System.currentTimeMillis();
			handler.postDelayed(lyricRunnable, delayed);
		}
	}
	
	private void activateTextView(TextView textView) {
		String type = (String) textView.getTag();
		String content = textView.getText().toString();
		Spannable boldContent = new SpannableString(content);
		Utility.getStyleSpan(boldContent, Typeface.BOLD);
		
		if (isTypeMale(type)) {
			textView.setTextColor(colorMaleActive);
		} else if (isTypeFemale(type)) {
			textView.setTextColor(colorFemaleActive);
		} else if (isTypeDual(type)) {
			textView.setTextColor(colorDualActive);
		}
		
		textView.setText(boldContent);
	}
	
	private void deactivateTextView(TextView textView) {
		String type = (String) textView.getTag();
		String content = textView.getText().toString();
		Spannable normalContent = new SpannableString(content);
		Utility.getStyleSpan(normalContent, Typeface.NORMAL);
		
		if (isTypeMale(type)) {
			textView.setTextColor(colorMaleInActive);
		} else if (isTypeFemale(type)) {
			textView.setTextColor(colorFemaleInActive);
		} else if (isTypeDual(type)) {
			textView.setTextColor(colorDualInActive);
		}
		
		textView.setText(normalContent);
	}
	
	public void stop() {
		handler.removeCallbacksAndMessages(null);
		
		if (countRunnable != null) {
			countRunnable.stop();
		}
		
		if (switcher != null) {
			switcher.setVisibility(View.GONE);
		}
		
		if (isDynamic()) {
			final int currentIndex = timeIndex - 1;
			
			if (currentIndex >= 0 && currentIndex < lrcTime.size()) {
				final Long currentTime = lrcTime.get(currentIndex);
				final Integer textViewIndex = textViewIndexes.get(currentTime);
				
				if (textViewIndex != null) {
					TextView currentTextView = (TextView) lyricWrapper.getChildAt(textViewIndex);
					deactivateTextView(currentTextView);
				}
			}
		}
	}
	
	private interface CountRunnable extends Runnable {
		
		public void stop();
		
	}
	
}
