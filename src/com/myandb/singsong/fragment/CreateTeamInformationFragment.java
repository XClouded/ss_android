package com.myandb.singsong.fragment;

import android.app.Activity;

import com.myandb.singsong.fragment.CreateTeamFragment.TeamInformation;

public abstract class CreateTeamInformationFragment extends BaseFragment {
	
	private TeamInformation information;
	
	public void setTeamInformation(TeamInformation information) {
		this.information = information;
	}

	@Override
	protected void initialize(Activity activity) {
		updateInformation();
	}
	
	protected void updateInformation() {
		if (information != null) {
			updateInformation(information);
		}
	}
	
	protected abstract void updateInformation(TeamInformation information);
	
	public abstract boolean isValidated();

}
