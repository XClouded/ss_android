package com.myandb.singsong.util;

import com.myandb.singsong.util.Lrc.Line;
import com.myandb.singsong.util.Lrc.Line.Type;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class StaticLrcDisplayer extends LrcDisplayer {

	public StaticLrcDisplayer(Context context) {
		super(context);
	}
	
	@Override
	protected View getLine(Line line) throws Exception {
		Type type = line.getType();
		TextView lyric = new TextView(getContext());
		lyric.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize());
		lyric.setGravity(Gravity.CENTER_HORIZONTAL);
		lyric.setTextColor(getActiveColor(type));
		lyric.setText(line.toString());
		return lyric;
	}

}
