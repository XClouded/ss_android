package com.myandb.singsong.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

public class CountViewFactory implements ViewFactory {
	
	private Context context;
	
	public CountViewFactory(Context context) {
		this.context = context;
	}

	@Override
	public View makeView() {
		TextView textView = null;
		
		if (context != null) {
			textView = new TextView(context);
			textView.setGravity(Gravity.CENTER);
			textView.setTextSize(150.0f);
			textView.setTextColor(Color.WHITE);
		}
		
		return textView;
	}

}
