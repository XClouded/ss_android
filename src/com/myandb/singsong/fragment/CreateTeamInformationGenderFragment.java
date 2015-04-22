package com.myandb.singsong.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.myandb.singsong.R;
import com.myandb.singsong.adapter.GenderSelectAdapter;
import com.myandb.singsong.fragment.CreateTeamFragment.OnTeamInformationUpdated;
import com.myandb.singsong.fragment.CreateTeamFragment.TeamInformation;
import com.myandb.singsong.model.Gender;

public class CreateTeamInformationGenderFragment extends ListFragment implements OnTeamInformationUpdated {
	
	@Override
	protected ListAdapter instantiateAdapter(Activity activity) {
		GenderSelectAdapter adapter = new GenderSelectAdapter();
		adapter.addAll(Gender.values());
		return adapter;
	}

	@Override
	protected int getFixedHeaderViewResId() {
		return R.layout.fragment_create_team_info_gender_fixed_header;
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		super.setupViews(savedInstanceState);
		if (getListView() instanceof ListView) {
			((ListView) getListView()).setItemsCanFocus(false);
			((ListView) getListView()).setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			((ListView) getListView()).setItemChecked(0, true);
		}
		setListShown(true);
	}

	@Override
	public boolean onUpdated(TeamInformation information) {
		if (getListView() instanceof ListView) {
			int position = ((ListView) getListView()).getCheckedItemPosition();
			Gender checked = (Gender) getListView().getItemAtPosition(position);
			information.setGender(checked);
			return true;
		}
		return false;
	}

}
