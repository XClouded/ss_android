package com.myandb.singsong.fragment;

import com.myandb.singsong.R;
import com.myandb.singsong.adapter.RoleAdapter;
import com.myandb.singsong.model.Member;
import com.myandb.singsong.model.Role;
import com.myandb.singsong.util.Utility;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MemberSettingFragment extends BaseFragment {
	
	private Button btnSubmit;
	private EditText etRolePrefix;
	private ListView lvRoles;
	private Member member;

	@Override
	protected int getResourceId() {
		return R.layout.fragment_member_setting;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		
		String memberInString = bundle.getString(Member.class.getName());
		member = Utility.getGsonInstance().fromJson(memberInString, Member.class);
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		btnSubmit = (Button) view.findViewById(R.id.btn_submit);
		etRolePrefix = (EditText) view.findViewById(R.id.et_role_prefix);
		lvRoles = (ListView) view.findViewById(R.id.lv_roles);
	}

	@Override
	protected void initialize(Activity activity) {
		RoleAdapter adapter = new RoleAdapter();
		adapter.addAll(Role.values());
		lvRoles.setAdapter(adapter);
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		if (member == null) {
			return;
		}
		
		lvRoles.setItemsCanFocus(false);
		lvRoles.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lvRoles.setItemChecked(member.getRole().ordinal(), true);
		
		btnSubmit.setOnClickListener(submitClickListener);
		
		etRolePrefix.setText(member.getRolePrefix());
	}
	
	private OnClickListener submitClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String prefix = etRolePrefix.getText().toString();
		}
	};

	@Override
	protected void onDataChanged() {}

}
