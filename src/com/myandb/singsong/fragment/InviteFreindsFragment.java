package com.myandb.singsong.fragment;

import com.myandb.singsong.pager.InviteFriendsPagerAdapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;

public class InviteFreindsFragment extends ViewPagerFragment {

	@Override
	protected void initialize(Activity activity) {
		
	}

	@Override
	protected void setupViews() {
		PagerAdapter adapter = new InviteFriendsPagerAdapter(getChildFragmentManager());
		setAdapter(adapter);
	}

}
