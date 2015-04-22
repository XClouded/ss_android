package com.myandb.singsong.fragment;

import com.myandb.singsong.R;
import com.myandb.singsong.dialog.PhotoIntentDialog;
import com.myandb.singsong.model.Team;
import com.myandb.singsong.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class TeamSettingFragment extends BaseFragment {
	
	private static final int REQUEST_CODE_EMBLEM = 300;
	private static final int REQUEST_CODE_BACKGROUND = 400;
	
	public static final String EXTRA_TEAM = "team";
	
	private ImageView ivTeamBackgroundPhoto;
	private ImageView ivTeamEmblem;
	private TextView tvTeamTitle;
	private TextView tvTeamDescription;
	private TextView tvTeamStatus;
	private View vChangeStatus;
	private View vChangeMaster;
	private Team team;

	@Override
	protected int getResourceId() {
		return R.layout.fragment_team_setting;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		
		String teamInString = bundle.getString(EXTRA_TEAM);
		team = Utility.getGsonInstance().fromJson(teamInString, Team.class);
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		ivTeamBackgroundPhoto = (ImageView) view.findViewById(R.id.iv_team_background_photo);
		ivTeamEmblem = (ImageView) view.findViewById(R.id.iv_team_emblem);
		tvTeamTitle = (TextView) view.findViewById(R.id.tv_team_title);
		tvTeamDescription = (TextView) view.findViewById(R.id.tv_team_description);
		tvTeamStatus = (TextView) view.findViewById(R.id.tv_team_status);
		vChangeStatus = view.findViewById(R.id.ll_change_status);
		vChangeStatus = view.findViewById(R.id.ll_change_master);
	}

	@Override
	protected void initialize(Activity activity) {}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		if (team == null) {
			return;
		}
		
		tvTeamTitle.setText(team.getName());
		tvTeamDescription.setText(team.getDescription());
		tvTeamStatus.setText(team.getStatusMessage());
		
		vChangeStatus.setOnClickListener(changeStatusClickListener);
		vChangeMaster.setOnClickListener(changeMasterClickListener);
		
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
						ivTeamEmblem.setImageResource(team.getCategory().getImageResourceId());
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
	
	private OnClickListener changeStatusClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
		}
	};
	
	private OnClickListener changeMasterClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
		}
	};

	@Override
	protected void onDataChanged() {}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Uri uri = PhotoIntentDialog.getUriOnActivityResult(requestCode, resultCode, data);
		if (uri != null) {
			//
		}
	}

}
