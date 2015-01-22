package com.myandb.singsong.adapter;

import com.myandb.singsong.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ListenNavigationAdapter 
	extends HolderAdapter<ListenNavigationAdapter.NavigationItem, ListenNavigationAdapter.ItemHolder> {
	
	public ListenNavigationAdapter() {
		super(NavigationItem.class);
	}

	@Override
	public ItemHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.row_listen_navigation, parent, false);
		return new ItemHolder(view);
	}

	@Override
	public void onBindViewHolder(Context context, ItemHolder viewHolder, int position) {
		final NavigationItem item = getItem(position);
		
		viewHolder.tvTitle.setText(item.getTitle());
	}

	public static final class ItemHolder extends ViewHolder {
		
		public TextView tvTitle;

		public ItemHolder(View view) {
			super(view);
			
			tvTitle = (TextView) view.findViewById(R.id.tv_title);
		}
		
	}

	public static final class NavigationItem {
		
		private String title;
		
		public NavigationItem(String title) {
			this.title = title;
		}
		
		public String getTitle() {
			return title;
		}
		
	}
	
}
