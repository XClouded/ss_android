package com.myandb.singsong.fragment;

import com.myandb.singsong.R;
import com.myandb.singsong.widget.HorizontalListView;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class MemberSettingFragment extends BaseFragment {
	
	public static final String EXTRA_MEMBER = "member";
	
	private Button btnSubmit;
	private EditText etRolePrefix;
	private Spinner spRole;
	private ImageView ivMemberPhoto;
	private ImageView ivMemberRoleIcon;
	private TextView tvMemberNickname;
	private TextView tvMemberFollowersNum;
	private TextView tvMemberRolePrefix;
	private TextView tvMemberRole;
	private View vMemberWrapper;
	private HorizontalListView hlvSkin;

	@Override
	protected int getResourceId() {
		return R.layout.fragment_member_setting;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		btnSubmit = (Button) view.findViewById(R.id.btn_submit);
		etRolePrefix = (EditText) view.findViewById(R.id.et_role_prefix);
		spRole = (Spinner) view.findViewById(R.id.sp_role);
		ivMemberPhoto = (ImageView) view.findViewById(R.id.iv_member_photo);
		ivMemberRoleIcon = (ImageView) view.findViewById(R.id.iv_member_role_icon);
		tvMemberNickname = (TextView) view.findViewById(R.id.tv_member_nickname);
		tvMemberFollowersNum = (TextView) view.findViewById(R.id.tv_member_followers_num);
		tvMemberRolePrefix = (TextView) view.findViewById(R.id.tv_member_role_prefix);
		tvMemberRole = (TextView) view.findViewById(R.id.tv_member_role);
		vMemberWrapper = view.findViewById(R.id.ll_member_wrapper);
		hlvSkin = (HorizontalListView) view.findViewById(R.id.hlv_skin);
	}

	@Override
	protected void initialize(Activity activity) {}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		btnSubmit.setOnClickListener(submitClickListener);
		etRolePrefix.addTextChangedListener(rolePrefixWatcher);
	}
	
	private OnClickListener submitClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
		}
	};
	
	private TextWatcher rolePrefixWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			tvMemberRolePrefix.setText(s);
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		
		@Override
		public void afterTextChanged(Editable s) {}
	};

	@Override
	protected void onDataChanged() {}

}
