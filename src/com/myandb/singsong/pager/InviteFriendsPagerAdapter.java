package com.myandb.singsong.pager;

import com.myandb.singsong.fragment.InviteContactFragment;
import com.myandb.singsong.fragment.InviteFacebookFragment;
import com.myandb.singsong.fragment.InviteKakaoFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class InviteFriendsPagerAdapter extends FragmentPagerAdapter {
	
	public InviteFriendsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0:
			return new InviteFacebookFragment();
			
		case 1:
			return new InviteContactFragment();
			
		case 2:
			return new InviteKakaoFragment();
			
		default:
			return null;
		}
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case 0:
			return "페이스북";
			
		case 1:
			return "연락처";
			
		case 2:
			return "카카오톡";
			
		default:
			return null;
		}
	}

	@Override
	public int getCount() {
		return 3;
	}

}
