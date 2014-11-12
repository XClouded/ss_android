package com.myandb.singsong.net;

import org.json.JSONArray;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.myandb.singsong.App;
import com.myandb.singsong.event.OnVolleyWeakError;
import com.myandb.singsong.event.OnVolleyWeakResponse;

public class GradualLoader {
	
	private static final int INITIAL_LOAD_NUM = 25;
	private static final int ADDITIONAL_LOAD_NUM = 15;

	private OnLoadCompleteListener completeListener;
	private OnLoadErrorListener errorListener;
	private RequestQueue queue;
	private UrlBuilder urlBuilder;
	private int count;
	private int requiredTake;
	private boolean nothingToLoad;
	private boolean loading;

	public GradualLoader(Context context) {
		this.queue = ((App) context.getApplicationContext()).getQueueInstance();
	}
	
	public void setUrlBuilder(UrlBuilder urlBuilder) {
		this.urlBuilder = urlBuilder;
		this.nothingToLoad = false;
		this.loading = false;
		this.count = 0;
		this.requiredTake = 0;
	}
	
	public void setOnLoadCompleteListener(OnLoadCompleteListener listener) {
		completeListener = listener;
	}
	
	public void setOnLoadErrorListener(OnLoadErrorListener listener) {
		errorListener = listener;
	}
	
	public void load() {
		if (isLoadable() && !nothingToLoad) {
			int take = count > 0 ? ADDITIONAL_LOAD_NUM : INITIAL_LOAD_NUM;
			executeQuery(count, take);
		}
	}
	
	private void executeQuery(int skip, int take) {
		if (!loading) {
			loading = true;
			
			urlBuilder.skip(skip);
			urlBuilder.take(take);
			requiredTake = take;
			
			OAuthJsonArrayRequest request = new OAuthJsonArrayRequest(
				urlBuilder.toString(),
				new OnVolleyWeakResponse<GradualLoader, JSONArray>(this, "onLoadResponse"),
				new OnVolleyWeakError<GradualLoader>(this, "onLoadError")
			);
			
			queue.add(request);
		}
	}
	
	public void onLoadResponse(JSONArray response) {
		loading = false;
		count += response.length();
		
		if (response.length() < requiredTake) {
			nothingToLoad = true;
		}
		
		if (completeListener != null) {
			completeListener.onComplete(response);
		}
	}
	
	public void onLoadError() {
		if (errorListener != null) {
			errorListener.onError();
		}
	}
	
	public boolean isLoading() {
		return loading;
	}
	
	public boolean isLoadable() {
		return urlBuilder != null;
	}
	
	public interface OnLoadCompleteListener {
		
		public void onComplete(JSONArray response);
		
	}
	
	public interface OnLoadErrorListener {
		
		public void onError();
		
	}
}
