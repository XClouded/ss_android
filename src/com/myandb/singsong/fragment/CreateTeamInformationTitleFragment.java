package com.myandb.singsong.fragment;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.dialog.PhotoIntentDialog;
import com.myandb.singsong.fragment.CreateTeamFragment.OnTeamInformationUpdated;
import com.myandb.singsong.fragment.CreateTeamFragment.TeamInformation;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JSONObjectSuccessListener;

public class CreateTeamInformationTitleFragment extends BaseFragment implements OnTeamInformationUpdated {
	
	private static final int REQUEST_CODE_EMBLEM = 300;
	private static final int REQUEST_CODE_BACKGROUND = 400;
	
	private ImageView ivTeamBackgroundPhoto;
	private ImageView ivTeamEmblem;
	private EditText etTitle;
	private TextView tvTeamDescription;
	private String requestTitle;
	private TeamInformation information;
	private boolean uniqueTitle;

	@Override
	public boolean onUpdated(TeamInformation information) {
		if (isLongEnoughTitle() && isUniqueTitle()) {
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
		ivTeamBackgroundPhoto = (ImageView) view.findViewById(R.id.iv_team_background_photo);
		ivTeamEmblem = (ImageView) view.findViewById(R.id.iv_team_emblem);
		etTitle = (EditText) view.findViewById(R.id.et_title);
		tvTeamDescription = (TextView) view.findViewById(R.id.tv_team_description);
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		ivTeamBackgroundPhoto.setOnClickListener(photoClickListener);
		ivTeamEmblem.setOnClickListener(photoClickListener);
	}
	
	private OnClickListener photoClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			PhotoIntentDialog dialog = new PhotoIntentDialog();
			int requestCode = 0;
			
			switch (v.getId()) {
			case R.id.iv_team_background_photo:
				requestCode = REQUEST_CODE_BACKGROUND;
				dialog.setOnDefaultImageClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//
					}
				});
				break;
				
			case R.id.iv_team_emblem:
				requestCode = REQUEST_CODE_EMBLEM;
				dialog.setOnDefaultImageClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (information != null) {
							ivTeamEmblem.setImageResource(information.getCategory().getImageResourceId());
						}
					}
				});
				break;

			default:
				return;
			}
			
			Bundle bundle = new Bundle();
			bundle.putInt(PhotoIntentDialog.EXTRA_REQUEST_CODE, requestCode);
			dialog.setArguments(bundle);
			dialog.show(getParentFragment().getChildFragmentManager(), "");
		}
	};
	
	private void checkTitleDuplicated() {
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
	
	public void setTeamInformation(TeamInformation information) {
		this.information = information;
	}

	@Override
	protected void onDataChanged() {
		if (information != null) {
			String description = "";
			description += information.getMemberMaxNum();
			description += "인조 ";
			description += information.getGender().getTitle();
			description += " ";
			description += information.getCategory().getTitle();
			description += "그룹";
			tvTeamDescription.setText(description);
			
			ivTeamEmblem.setImageResource(information.getCategory().getImageResourceId());
		}
	}
	
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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Uri uri = PhotoIntentDialog.getUriOnActivityResult(requestCode, resultCode, data);
		if (uri != null) {
			if (requestCode == REQUEST_CODE_BACKGROUND) {
				ivTeamBackgroundPhoto.setImageURI(uri);
			} else if (requestCode == REQUEST_CODE_EMBLEM) {
				ivTeamEmblem.setImageURI(uri);
			}
		}
	}

}
