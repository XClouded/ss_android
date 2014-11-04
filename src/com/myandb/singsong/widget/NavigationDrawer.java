package com.myandb.singsong.widget;

import android.app.Activity;
import android.content.Context;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.myandb.singsong.R;

public class NavigationDrawer extends SlidingMenu {
	
	private NavigationDrawer(Context context) {
		super(context);
	}
	
	public static NavigationDrawer instantiateAndAttach(Activity activity) {
		NavigationDrawer drawer = new NavigationDrawer(activity);
		drawer.configureSlidingBehavior();
		drawer.configureStyle();
		drawer.setMenu(R.layout.row_child_song);
		drawer.attachToActivity(activity, SlidingMenu.SLIDING_WINDOW);
		
		return drawer;
	}
	
	private void configureSlidingBehavior() {
		setMode(SlidingMenu.LEFT);
		setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
	}
	
	private void configureStyle() {
		setShadowWidthRes(R.dimen.margin_tiny);
		setBehindOffsetRes(R.dimen.photo_profile);
		setFadeDegree(0.35f);
	}

}
