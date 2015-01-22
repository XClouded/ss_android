package com.myandb.singsong.widget;

import com.myandb.singsong.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class DrawerView extends LinearGridView {

	public DrawerView(Context context) {
		super(context);
	}

	public DrawerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected View getChild(int position) {
		View child = super.getChild(position);
		setChildBackground(child, position);
		return child;
	}

	private void setChildBackground(View child, int position) {
		switch (position) {
		case 0:
		case 2:
			child.setBackgroundResource(R.drawable.drawer_right_bottom_selector);
			break;
			
		case 1:
		case 3:
			child.setBackgroundResource(R.drawable.drawer_bottom_selector);
			break;
			
		case 4:
			child.setBackgroundResource(R.drawable.drawer_right_selector);
			break;
			
		case 5:
			child.setBackgroundResource(R.drawable.drawer_selector);
			break;

		default:
			break;
		}
	}

}
