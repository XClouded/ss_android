package com.myandb.singsong.fragment;

import com.myandb.singsong.R;
import com.myandb.singsong.pager.InviteFriendsPagerAdapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;

public class InviteFriendsFragment extends ViewPagerFragment {

	@Override
	protected void initialize(Activity activity) {
		// Nothing to run
	}

	@Override
	protected void setupViews() {
		PagerAdapter adapter = new InviteFriendsPagerAdapter(getChildFragmentManager());
		setAdapter(adapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		setActionBarTitle(R.string.fragment_invite_friends_title);
	}

}
