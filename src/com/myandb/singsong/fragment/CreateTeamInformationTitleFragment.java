package com.myandb.singsong.fragment;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.myandb.singsong.R;
import com.myandb.singsong.fragment.CreateTeamFragment.OnTeamInformationUpdated;
import com.myandb.singsong.fragment.CreateTeamFragment.TeamInformation;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JSONObjectSuccessListener;

public class CreateTeamInformationTitleFragment extends BaseFragment implements OnTeamInformationUpdated {
	
	private EditText etTitle;
	private Button btnCheckUnique;
	private boolean uniqueTitle;
	private String requestTitle;

	@Override
	public boolean onUpdated(TeamInformation information) {
		if (isLongEnoughTitle()) {
			information.setTitle(etTitle.getText().toString());
			return true;
		}
		return false;
	}
	
	@Override
	protected void initialize(Activity activity) {}

	@Override
	protected int getResourceId() {
		return R.layout.fragment_create_team_info_title;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		etTitle = (EditText) view.findViewById(R.id.et_title);
		btnCheckUnique = (Button) view.findViewById(R.id.btn_check_unique);
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		btnCheckUnique.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showProgressDialog();
				
				requestTitle = etTitle.getText().toString();
				
				Bundle params = new Bundle();
				params.putString("title", requestTitle);
				JSONObjectRequest request = new JSONObjectRequest(
						"teams", params, null,
						new JSONObjectSuccessListener(CreateTeamInformationTitleFragment.this, "onTitleFound"),
						new JSONErrorListener(CreateTeamInformationTitleFragment.this, "onTitleNotFound"));
				addRequest(request);
			}
		});
	}
	
	public void onTitleFound(JSONObject response) {
		dismissProgressDialog();
		if (requestTitle != null) {
			makeToast(requestTitle + "은 이미 사용중인 팀명입니다.");
		}
		setUniqueTitle(false);
	}
	
	public void onTitleNotFound() {
		dismissProgressDialog();
		if (requestTitle != null) {
			makeToast(requestTitle + "은 사용하실 수 있는 팀명입니다.");
		}
		setUniqueTitle(true);
	}

	@Override
	protected void onDataChanged() {}
	
	private void setUniqueTitle(boolean unique) {
		uniqueTitle = unique;
	}
	
	private boolean isUniqueTitle() {
		return uniqueTitle;
	}
	
	private boolean isLongEnoughTitle() {
		return etTitle.getText().length() > 0;
	}

}
