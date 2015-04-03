package com.myandb.singsong.pager;

import com.myandb.singsong.fragment.CreateTeamInformationFragment;
import com.myandb.singsong.fragment.CreateTeamInformationGenderFragment;
import com.myandb.singsong.fragment.CreateTeamInformationCategoryFragment;
import com.myandb.singsong.fragment.CreateTeamInformationMemberNumFragment;
import com.myandb.singsong.fragment.CreateTeamInformationTitleFragment;
import com.myandb.singsong.fragment.CreateTeamFragment.TeamInformation;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

public class CreateTeamPagerAdapter extends FragmentPagerAdapter {
	
	private FragmentManager fragmentManager;
	private TeamInformation information;
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
	
	public void setTeamInformation(TeamInformation information) {
		this.information = information;
	}

	@Override
	public Fragment getItem(int position) {
		CreateTeamInformationFragment fragment = null;
		switch (position) {
		case 0:
			fragment = new CreateTeamInformationTitleFragment();
			break;
			
		case 1:
			fragment = new CreateTeamInformationMemberNumFragment();
			break;
			
		case 2:
			fragment = new CreateTeamInformationGenderFragment();
			break;
			
		case 3:
			fragment = new CreateTeamInformationCategoryFragment();
			break;

		default:
			break;
		}
		
		fragment.setTeamInformation(information);
		return fragment;
	}

	@Override
	public int getCount() {
		return 4;
	}
	
	private Fragment getCurrentItem(int position) {
		String fragmentName = makeFragmentName(containerId, position);
		return fragmentManager.findFragmentByTag(fragmentName);
	}
	
	private static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }
	
	public boolean isValidated(int position) {
		Fragment fragment = getCurrentItem(position);
		if (fragment instanceof CreateTeamInformationFragment) {
			return ((CreateTeamInformationFragment) fragment).isValidated();
		}
		return false;
	}

}
