package com.myandb.singsong.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.myandb.singsong.R;
import com.myandb.singsong.adapter.SongCategoryAdapter;
import com.myandb.singsong.fragment.CreateTeamFragment.OnTeamInformationUpdated;
import com.myandb.singsong.fragment.CreateTeamFragment.TeamInformation;
import com.myandb.singsong.model.Category;

public class CreateTeamInformationCategoryFragment extends ListFragment implements OnTeamInformationUpdated {
	
	@Override
	protected ListAdapter instantiateAdapter(Activity activity) {
		SongCategoryAdapter adapter = new SongCategoryAdapter();
		adapter.addAll(Category.getCategories(1, 10));
		return adapter;
	}
	
	@Override
	protected int getFixedHeaderViewResId() {
		return R.layout.fragment_create_team_info_category_fixed_header;
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
			Category checked = (Category) getListView().getItemAtPosition(position);
			information.setCategory(checked);
			return true;
		}
		return false;
	}

}
