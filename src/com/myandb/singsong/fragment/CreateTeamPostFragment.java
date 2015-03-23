package com.myandb.singsong.fragment;

import com.myandb.singsong.R;
import com.myandb.singsong.adapter.UploadImageAdapter;
import com.myandb.singsong.widget.FloatableLayout;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class CreateTeamPostFragment extends BaseFragment {
	
	private FloatableLayout fltUploadImage;

	@Override
	protected int getResourceId() {
		return R.layout.fragment_create_team_post;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		fltUploadImage = (FloatableLayout) view.findViewById(R.id.flt_upload_image);
	}

	@Override
	protected void initialize(Activity activity) {
		UploadImageAdapter adapter = new UploadImageAdapter();
		fltUploadImage.setAdapter(adapter);
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		
	}

	@Override
	protected void onDataChanged() {
		
	}

}
