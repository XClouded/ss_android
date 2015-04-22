package com.myandb.singsong.adapter;

import com.myandb.singsong.R;
import com.myandb.singsong.model.Role;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class RoleAdapter extends HolderAdapter<Role, RoleAdapter.RoleHolder> {
	
	public RoleAdapter() {
		super(Role.class);
	}
	
	@Override
	public RoleHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.row_role, parent, false);
		return new RoleHolder(view);
	}
	
	@Override
	public void onBindViewHolder(Context context, RoleHolder viewHolder, Role item, int position) {
		viewHolder.ivRoleIcon.setImageResource(item.getImageResourceId());
		viewHolder.tvRoleTitle.setText(item.getTitle());
	}

	public static final class RoleHolder extends ViewHolder {
		
		public ImageView ivRoleIcon;
		public TextView tvRoleTitle;

		public RoleHolder(View view) {
			super(view);
			
			ivRoleIcon = (ImageView) view.findViewById(R.id.iv_role_icon);
			tvRoleTitle = (TextView) view.findViewById(R.id.tv_role_title);
		}
		
	}

}
