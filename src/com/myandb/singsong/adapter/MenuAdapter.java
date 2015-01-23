package com.myandb.singsong.adapter;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.event.MemberOnlyClickListener;
import com.myandb.singsong.model.GlobalMenu;
import com.myandb.singsong.model.User;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuAdapter extends HolderAdapter<GlobalMenu, MenuAdapter.MenuHolder> {
	
	public MenuAdapter() {
		super(GlobalMenu.class);
	}

	@Override
	public MenuHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.row_menu, parent, false);
		return new MenuHolder(view);
	}

	@Override
	public void onBindViewHolder(Context context, MenuHolder viewHolder, final GlobalMenu menu, int position) {
		viewHolder.ivMenuIcon.setImageResource(menu.getIconResId());
		viewHolder.tvMenuTitle.setText(menu.getTitleResId());
		
		if (menu.isLoginRequired()) {
			viewHolder.view.setOnClickListener(new MemberOnlyClickListener() {
				
				@Override
				public void onLoggedIn(View v, User user) {
					changePage(v.getContext(), menu.getIntent());
				}
			});
		} else {
			viewHolder.view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					changePage(v.getContext(), menu.getIntent());
				}
			});
		}
	}
	
	private void changePage(Context context, Intent intent) {
		if (context instanceof BaseActivity) {
			((BaseActivity) context).changePage(intent);
		}
	}
	
	public static final class MenuHolder extends ViewHolder {
		
		public ImageView ivMenuIcon;
		public TextView tvMenuTitle;
		
		public MenuHolder(View view) {
			super(view);
			
			ivMenuIcon = (ImageView) view.findViewById(R.id.iv_gnb_icon);
			tvMenuTitle = (TextView) view.findViewById(R.id.tv_gnb_title);
		}
		
	}

}
