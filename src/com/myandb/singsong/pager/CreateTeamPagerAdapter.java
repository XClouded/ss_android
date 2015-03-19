package com.myandb.singsong.pager;

import com.myandb.singsong.R;
import com.myandb.singsong.fragment.CreateTeamFragment.TeamInformation;
import com.myandb.singsong.model.Category;
import com.myandb.singsong.model.Gender;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CreateTeamPagerAdapter extends FragmentPagerAdapter {
	
	private FragmentManager fragmentManager;
	private int containerId;
	private TeamInformation information;
	
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
		AFragment f = new AFragment();
		f.setIndex(position);
		f.setInformation(information);
		return f;
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
		if (fragment instanceof InformationProvider) {
			return ((InformationProvider) fragment).isValidated();
		}
		return false;
	}
	
	public interface InformationProvider {
		
		
		public boolean isValidated();
		
	}
	
	public static class AFragment extends Fragment implements InformationProvider {
		
		private int index = 0;
		private TeamInformation information;
		
		public void setInformation(TeamInformation information) {
			this.information = information;
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			return inflater.inflate(R.layout.fragment_create_team_test, container, false);
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onViewCreated(view, savedInstanceState);
			TextView tv = (TextView) view.findViewById(R.id.tv);
			tv.setText(String.valueOf(index));
			updateInformation();
		}
		
		private void updateInformation() {
			switch (index) {
			case 0:
				information.setTitle("team title");
				break;
				
			case 1:
				information.setMemberMaxNum(7);
				break;
				
			case 2:
				information.setGender(Gender.BOYS);
				break;
				
			case 3:
				information.setCategory(new Category(4));
				break;
				
			default:
				break;
			}
		}
		
		public void setIndex(int index) {
			this.index = index;
		}

		@Override
		public boolean isValidated() {
			return true;
		}
		
	}

}
