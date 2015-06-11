package com.myandb.singsong.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.myandb.singsong.util.GsonUtils;

import android.content.Context;
import android.view.LayoutInflater;
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
		List<T> converted = new ArrayList<T>();
		try {
			for (int i = 0, l = jsonItems.length(); i < l; i++) {
				JSONObject item = jsonItems.getJSONObject(i);
				converted.add(GsonUtils.fromJson(item, clazz));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			addAll(converted);
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
			LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			viewHolder = onCreateViewHolder(inflater, parent, getItemViewType(position));
			view = viewHolder.view;
		} else {
			viewHolder = (E) view.getTag();
		}
		
		if (position >= 0 && position < getCount()) {
			T item = getItem(position);
			if (item != null) {
				onBindViewHolder(view.getContext(), viewHolder, item, position);
			}
		}
		
		return view;
	}
	
	public abstract E onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType);
	
	public abstract void onBindViewHolder(Context context, E viewHolder, T item, int position);
	
}
