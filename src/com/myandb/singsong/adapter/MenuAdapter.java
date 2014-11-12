package com.myandb.singsong.adapter;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.model.GlobalMenu;

import android.content.Context;
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
	public MenuHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = View.inflate(parent.getContext(), R.layout.row_menu, null);
		return new MenuHolder(view);
	}

	@Override
	public void onBindViewHolder(MenuHolder viewHolder, int position) {
		final GlobalMenu menu = (GlobalMenu) getItem(position);
		
		viewHolder.ivMenuIcon.setImageResource(menu.getIconResId());
		viewHolder.tvMenuTitle.setText(menu.getTitleResId());
		viewHolder.view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Context context = v.getContext();
				if (context instanceof BaseActivity) {
					((BaseActivity) context).changePage(menu.getIntent());
				}
			}
		});
	}
	
	public static final class MenuHolder extends ViewHolder {
		
		public ImageView ivMenuIcon;
		public TextView tvMenuTitle;
		
		public MenuHolder(View view) {
			super(view);
		}
		
	}

}
