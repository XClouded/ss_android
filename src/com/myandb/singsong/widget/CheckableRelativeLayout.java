package com.myandb.singsong.widget;

import com.myandb.singsong.App;
import com.myandb.singsong.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

public class CheckableRelativeLayout extends RelativeLayout implements Checkable {
	
	private int checkableId;
	private Checkable checkable;

	public CheckableRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray styleable = context.obtainStyledAttributes(attrs, R.styleable.CheckableRelativeLayout);
		checkableId = styleable.getResourceId(R.styleable.CheckableRelativeLayout_checkable, App.INVALID_RESOURCE_ID);
		styleable.recycle();
	}

	@Override
	public void setChecked(boolean checked) {
		checkable = (Checkable) findViewById(checkableId);
		if (checkable != null) {
			checkable.setChecked(checked);
		}
	}

	@Override
	public boolean isChecked() {
		checkable = (Checkable) findViewById(checkableId);
		if (checkable != null) {
			return checkable.isChecked();
		}
		return false;
	}

	@Override
	public void toggle() {
		checkable = (Checkable) findViewById(checkableId);
		if (checkable != null) {
			checkable.toggle();
		}
	}

}
