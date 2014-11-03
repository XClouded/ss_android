package com.myandb.singsong.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.widget.BaseAdapter;

import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.myandb.singsong.App;
import com.myandb.singsong.activity.OldBaseActivity;
import com.myandb.singsong.event.OnVolleyWeakError;
import com.myandb.singsong.event.OnVolleyWeakResponse;
import com.myandb.singsong.net.OAuthJsonArrayRequest;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.Utility;

public abstract class AutoLoadAdapter<T> extends BaseAdapter {
	
	private static final int INITIAL_LOAD_NUM = 25;
	private static final int ADDITIONAL_LOAD_NUM = 15;
	
	protected List<T> datas;
	private Date currentDate;
	private OldBaseActivity parent;
	private Context context;
	private boolean append  		   	= false;
	private boolean loading 		   	= false;
	private boolean endOfList			= false;
	private boolean showProgressDialog 	= true;
	private UrlBuilder urlBuilder;
	private Class<T> clazz;
	private int requiredTake = 0;
	private int initialLoadNum = INITIAL_LOAD_NUM;
	
	public AutoLoadAdapter(Context context, Class<T> clazz, boolean showProgressDialog) {
		this.context = context;
		this.clazz = clazz;
		this.showProgressDialog = showProgressDialog;
		
		currentDate = new Date();
		datas = new ArrayList<T>();
		
		if (context instanceof OldBaseActivity) {
			parent = (OldBaseActivity) context;
		}
	}
	
	public void resetRequest(UrlBuilder urlBuilder) {
		if (urlBuilder != null) {
			this.urlBuilder = urlBuilder;
			
			datas.clear();
			notifyDataSetChanged();
			
			append = false;
			loading = false;
			endOfList = false;
			
			try {
				if (urlBuilder.hasParam("take")) {
					initialLoadNum = Integer.parseInt(urlBuilder.getParam("take"));
				}
			} catch (NumberFormatException e) {
				initialLoadNum = INITIAL_LOAD_NUM;
			}
			
			executeQuery();
		}
	}
	
	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public T getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public Context getContext() {
		return context;
	}
	
	protected Date getCurrentDate() {
		return currentDate;
	}
	
	public void insertItem(T object) {
		insertItem(object, 0);
	}
	
	public void insertItem(T object, int index) {
		if (object != null) {
			datas.add(index, object);
			currentDate = new Date();
			
			notifyDataSetChanged();
		}
	}
	
	public void removeItem(T object) {
		if (object != null) {
			datas.remove(object);
			
			notifyDataSetChanged();
		}
	}
	
	public void executeQuery() {
		if (!append) {
			append = true;
			executeQuery(0, initialLoadNum);
		} else {
			if (!endOfList) {
				executeQuery(datas.size(), ADDITIONAL_LOAD_NUM);
			}
		}
	}
	
	private void executeQuery(int skip, int take) {
		if (parent != null && skip == 0 && showProgressDialog) {
			parent.showProgressDialog();
		}
		
		if (!loading && urlBuilder != null) {
			loading = true;
			urlBuilder.skip(skip);
			urlBuilder.take(take);
			requiredTake = take;
			
			OAuthJsonArrayRequest request = new OAuthJsonArrayRequest(
					urlBuilder.build(true),
					new OnVolleyWeakResponse<AutoLoadAdapter<T>, JSONArray>(this, "onLoadResponse"),
					new OnVolleyWeakError<AutoLoadAdapter<T>>(this, "onLoadError")
			);
			
			RequestQueue queue = ((App) getContext().getApplicationContext()).getQueueInstance();
			queue.add(request);
		}
	}
	
	public void onLoadResponse(JSONArray response) {
		loading = false;
		
		if (context == null) {
			return;
		}
		
		try {
			Gson gson = Utility.getGsonInstance();
			String iItemInJson = null;
			
			int responseLength = response.length();			
			for (int i = 0; i < responseLength; i++) {
				iItemInJson = response.getJSONObject(i).toString();
				datas.add(gson.fromJson(iItemInJson, clazz));
			}
			
			if (responseLength < requiredTake) {
				endOfList = true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} finally {
			dismissProgressDialog();
			notifyDataSetChanged();
		}
	}
	
	public void onLoadError() {
		dismissProgressDialog();
	}
	
	private void dismissProgressDialog() {
		if (parent != null) {
			parent.dismissProgressDialog();
		}
	}

}
