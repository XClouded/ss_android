package com.myandb.singsong.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListAdapter;

import com.myandb.singsong.adapter.SongCategoryAdapter;
import com.myandb.singsong.fragment.CreateTeamFragment.OnTeamInformationUpdated;
import com.myandb.singsong.fragment.CreateTeamFragment.TeamInformation;
import com.myandb.singsong.model.Category;

public class CreateTeamInformationCategoryFragment extends ListFragment implements OnTeamInformationUpdated {
	
	private Category currentCategory;
	
	@Override
	protected ListAdapter instantiateAdapter(Activity activity) {
		SongCategoryAdapter adapter = new SongCategoryAdapter();
		adapter.addAll(Category.getCategories());
		return adapter;
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		super.setupViews(savedInstanceState);
		setListShown(true);
	}

	@Override
	public boolean onUpdated(TeamInformation information) {
		information.setCategory(currentCategory);
		return true;
	}

}
