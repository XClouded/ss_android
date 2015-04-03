package com.myandb.singsong.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.fragment.CreateTeamFragment.TeamInformation;

public class CreateTeamInformationMemberNumFragment extends CreateTeamInformationFragment {
	
	private static final int MEMBER_MIN_NUM = 3;
	private static final int MEMBER_MAX_NUM = 20;
	private static final int MEMBER_DEFAULT_NUM = 8;
	
	private ImageView ivDecreaseNum;
	private ImageView ivIncreaseNum;
	private TextView tvMemberNum;
	private int currentMemberNum;
	
	@Override
	protected void updateInformation(TeamInformation information) {
		information.setMemberMaxNum(currentMemberNum);
	}

	@Override
	protected int getResourceId() {
		return R.layout.fragment_create_team_info_member_num;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		ivDecreaseNum = (ImageView) view.findViewById(R.id.iv_decrease_num);
		ivIncreaseNum = (ImageView) view.findViewById(R.id.iv_increase_num);
		tvMemberNum = (TextView) view.findViewById(R.id.tv_member_num);
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		setMemberNum(MEMBER_DEFAULT_NUM);
		ivDecreaseNum.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setMemberNum(getMemberNum() - 1);
			}
		});
		ivIncreaseNum.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setMemberNum(getMemberNum() + 1);
			}
		});
	}
	
	private void setMemberNum(int num) {
		currentMemberNum = Math.max(Math.min(num, MEMBER_MAX_NUM), MEMBER_MIN_NUM);
		tvMemberNum.setText(String.valueOf(currentMemberNum));
		updateInformation();
	}
	
	private int getMemberNum() {
		return currentMemberNum;
	}

	@Override
	protected void onDataChanged() {}
	
	@Override
	public boolean isValidated() {
		return currentMemberNum >= MEMBER_MIN_NUM && currentMemberNum <= MEMBER_MAX_NUM;
	}
}
