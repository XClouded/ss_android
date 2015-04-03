package com.myandb.singsong.fragment;

import org.json.JSONObject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.myandb.singsong.R;
import com.myandb.singsong.fragment.CreateTeamFragment.TeamInformation;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JSONObjectSuccessListener;

public class CreateTeamInformationTitleFragment extends CreateTeamInformationFragment {
	
	private EditText etTitle;
	private Button btnCheckUnique;
	private boolean uniqueTitle;
	private String requestTitle;

	@Override
	protected void updateInformation(TeamInformation information) {
		if (isLongEnoughTitle()) {
			information.setTitle(etTitle.getText().toString());
		}
	}

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
		etTitle.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {
				updateInformation();
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
	
	@Override
	public boolean isValidated() {
		return isUniqueTitle() && isLongEnoughTitle();
	}

}
