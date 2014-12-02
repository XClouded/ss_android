package com.myandb.singsong.pager;

import com.myandb.singsong.fragment.ConnectFacebookFragment;
import com.myandb.singsong.fragment.InviteContactFragment;
import com.myandb.singsong.fragment.InviteFacebookFragment;
import com.myandb.singsong.fragment.InviteKakaoFragment;
import com.myandb.singsong.model.User;
import com.myandb.singsong.secure.Authenticator;

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
			User user = Authenticator.getUser();
			if (user.isFacebookActivated()) {
				return new InviteFacebookFragment();
			} else {
				return new ConnectFacebookFragment();
			}
			
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
			return "���̽���";
			
		case 1:
			return "����ó";
			
		case 2:
			return "īī����";
			
		default:
			return null;
		}
	}

	@Override
	public int getCount() {
		return 3;
	}

}
