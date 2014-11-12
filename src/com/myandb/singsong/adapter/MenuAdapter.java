package com.myandb.singsong.adapter;

import java.util.List;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.model.GlobalMenu;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuAdapter extends BaseAdapter {
	
	private List<GlobalMenu> menuItems;
	private Context context;
	
	public MenuAdapter(Context context, List<GlobalMenu> menuDatas) {
		this.context = context;
		this.menuItems = menuDatas;
	}
	
	@Override
	public int getCount() {
		return menuItems.size();
	}

	@Override
	public Object getItem(int position) {
		return menuItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public Context getContext() {
		return context;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final MenuHolder menuHolder;
		final GlobalMenu menu = (GlobalMenu) getItem(position);
		
		if (view == null) {
			view = View.inflate(getContext(), R.layout.row_menu, null);
			
			menuHolder = new MenuHolder();
			menuHolder.ivMenuIcon = (ImageView) view.findViewById(R.id.iv_gnb_icon);
			menuHolder.tvMenuTitle = (TextView) view.findViewById(R.id.tv_gnb_title);
			
			view.setTag(menuHolder);
		} else {
			menuHolder = (MenuHolder) view.getTag();
		}
		
		menuHolder.ivMenuIcon.setImageResource(menu.getIconResId());
		menuHolder.tvMenuTitle.setText(menu.getTitleResId());
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Context context = v.getContext();
				if (context instanceof BaseActivity) {
					((BaseActivity) context).changePage(menu.getIntent());
				}
			}
		});
		
		return view;
	}
	
	private static class MenuHolder {
		
		public ImageView ivMenuIcon;
		public TextView tvMenuTitle;
		
	}

}
