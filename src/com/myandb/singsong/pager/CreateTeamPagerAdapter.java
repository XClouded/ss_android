package com.myandb.singsong.pager;

import com.myandb.singsong.fragment.CreateTeamInformationGenderFragment;
import com.myandb.singsong.fragment.CreateTeamInformationCategoryFragment;
import com.myandb.singsong.fragment.CreateTeamInformationMemberNumFragment;
import com.myandb.singsong.fragment.CreateTeamInformationTitleFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

public class CreateTeamPagerAdapter extends FragmentPagerAdapter {
	
	private FragmentManager fragmentManager;
	private int containerId;
	
	public CreateTeamPagerAdapter(FragmentManager fm) {
		super(fm);
		fragmentManager = fm;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		containerId = container.getId();
		return super.instantiateItem(container, position);
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new CreateTeamInformationMemberNumFragment();
			break;
			
		case 1:
			fragment = new CreateTeamInformationGenderFragment();
			break;
			
		case 2:
			fragment = new CreateTeamInformationCategoryFragment();
			break;
			
		case 3:
			fragment = new CreateTeamInformationTitleFragment();
			break;

		default:
			break;
		}
		
		return fragment;
	}

	@Override
	public int getCount() {
		return 4;
	}
	
	public Fragment getCurrentItem(int position) {
		String fragmentName = makeFragmentName(containerId, position);
		return fragmentManager.findFragmentByTag(fragmentName);
	}
	
	private static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

}
