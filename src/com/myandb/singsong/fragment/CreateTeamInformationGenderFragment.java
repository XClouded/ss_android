package com.myandb.singsong.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.fragment.CreateTeamFragment.TeamInformation;
import com.myandb.singsong.model.Gender;

public class CreateTeamInformationGenderFragment extends CreateTeamInformationFragment {
	
	private Spinner spGender;
	private Gender currentGender;

	@Override
	protected void updateInformation(TeamInformation information) {
		information.setGender(currentGender);
	}

	@Override
	public boolean isValidated() {
		return currentGender != null && !currentGender.equals(Gender.NULL);
	}

	@Override
	protected int getResourceId() {
		return R.layout.fragment_create_team_info_gender;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		spGender = (Spinner) view.findViewById(R.id.sp_gender);
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		spGender.setAdapter(new GenderAdapter(getActivity(), R.layout.row_gender));
		spGender.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Gender gender = (Gender) parent.getItemAtPosition(position);
				currentGender = gender;
				updateInformation();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
	}

	@Override
	protected void onDataChanged() {}
	
	private static class GenderAdapter extends ArrayAdapter<Gender> {
		
		private int layoutResourceId;
		
		public GenderAdapter(Context context, int layoutResourceId) {
			super(context, layoutResourceId, Gender.values());
			this.layoutResourceId = layoutResourceId;
		}

		@SuppressLint("ViewHolder")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			View row = inflater.inflate(layoutResourceId, parent, false);
			TextView tvGender = (TextView) row.findViewById(R.id.tv_gender);
			Gender gender = getItem(position);
			tvGender.setText(gender.getTitle());
			return row;
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			return getView(position, convertView, parent);
		}
		
	}

}
