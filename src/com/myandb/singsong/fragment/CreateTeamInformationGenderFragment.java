package com.myandb.singsong.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.fragment.CreateTeamFragment.OnTeamInformationUpdated;
import com.myandb.singsong.fragment.CreateTeamFragment.TeamInformation;
import com.myandb.singsong.model.Gender;

public class CreateTeamInformationGenderFragment extends ListFragment implements OnTeamInformationUpdated {
	
	private Gender currentGender;
	
	@Override
	protected ListAdapter instantiateAdapter(Activity activity) {
		return new GenderAdapter(activity, R.layout.row_gender);
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		super.setupViews(savedInstanceState);
		setListShown(true);
	}

	@Override
	public boolean onUpdated(TeamInformation information) {
		information.setGender(currentGender);
		return true;
	}
	
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
