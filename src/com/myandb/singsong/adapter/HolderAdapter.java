package com.myandb.singsong.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;

import com.google.gson.Gson;
import com.myandb.singsong.util.Utility;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class HolderAdapter<T, E extends ViewHolder> extends BaseAdapter {
	
	protected List<T> items;
	private Class<T> clazz;
	private Date currentDate;

	public HolderAdapter(Class<T> clazz) {
		this.clazz = clazz;
		this.items = new ArrayList<T>();
		this.currentDate = new Date();
	}
	
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public T getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	protected Date getCurrentDate() {
		return currentDate;
	}
	
	public void addItem(T item) {
		if (item != null) {
			items.add(item);
			currentDate = new Date();
			notifyDataSetChanged();
		}
	}
	
	public void addItem(int index, T item) {
		if (item != null) {
			items.add(index, item);
			currentDate = new Date();
			notifyDataSetChanged();
		}
	}
	
	public void addItemToHead(T item) {
		addItem(0, item);
	}
	
	public void addAll(List<T> items) {
		this.items.addAll(items);
		notifyDataSetChanged();
	}
	
	public void addAll(JSONArray jsonItems) {
		try {
			Gson gson = Utility.getGsonInstance();
			String iItemInJson = null;
			
			for (int i = 0, l = jsonItems.length(); i < l; i++) {
				iItemInJson = jsonItems.getJSONObject(i).toString();
				items.add(gson.fromJson(iItemInJson, clazz));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			notifyDataSetChanged();
		}
	}

	public void removeItem(T item) {
		if (item != null) {
			items.remove(item);
			notifyDataSetChanged();
		}
	}
	
	public void clear() {
		items.clear();
		notifyDataSetChanged();
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final E viewHolder;
		
		if (view == null) {
			viewHolder = onCreateViewHolder(parent, getItemViewType(position));
			view = viewHolder.view;
		} else {
			viewHolder = (E) view.getTag();
		}
		
		onBindViewHolder(viewHolder, position);
		
		return view;
	}
	
	public abstract E onCreateViewHolder(ViewGroup parent, int viewType);
	
	public abstract void onBindViewHolder(E viewHolder, int position);
	
}
