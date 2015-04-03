package com.myandb.singsong.fragment;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.myandb.singsong.R;
import com.myandb.singsong.adapter.SongCategoryAdapter;
import com.myandb.singsong.fragment.CreateTeamFragment.TeamInformation;
import com.myandb.singsong.model.Category;

public class CreateTeamInformationCategoryFragment extends CreateTeamInformationFragment {
	
	private Spinner spCategory;
	private Category currentCategory;

	@Override
	protected void updateInformation(TeamInformation information) {
		information.setCategory(currentCategory);
	}

	@Override
	public boolean isValidated() {
		return currentCategory != null && currentCategory.canRepresentTeam();
	}

	@Override
	protected int getResourceId() {
		return R.layout.fragment_create_team_info_category;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		spCategory = (Spinner) view.findViewById(R.id.sp_category);
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		spCategory.setAdapter(new CategoryAdapter(getActivity(), R.layout.row_song_category, Category.getCategories(1, 10)));
		spCategory.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Category category = (Category) parent.getItemAtPosition(position);
				currentCategory = category;
				updateInformation();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
	}

	@Override
	protected void onDataChanged() {}
	
	private static class CategoryAdapter extends ArrayAdapter<Category> {
		
		private SongCategoryAdapter internalAdapter;
		
		public CategoryAdapter(Context context, int layoutResourceId, List<Category> categories) {
			super(context, layoutResourceId, categories);
			internalAdapter = new SongCategoryAdapter();
			internalAdapter.addAll(categories);
		}

		@SuppressLint("ViewHolder")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return internalAdapter.getView(position, convertView, parent);
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			return getView(position, convertView, parent);
		}
		
	}

}
