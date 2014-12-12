package com.myandb.singsong.net;

import org.json.JSONArray;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.myandb.singsong.App;

public class GradualLoader implements OnScrollListener {
	
	private static final int INITIAL_LOAD_NUM = 25;
	private static final int ADDITIONAL_LOAD_NUM = 15;
	private static final int VISIBLE_THRESHOLD = 10; 

	private Context context;
	private OnLoadCompleteListener completeListener;
	private OnLoadErrorListener errorListener;
	private UrlBuilder urlBuilder;
	private int count;
	private int requiredTake;
	private boolean nothingToLoad;
	private boolean loading;
    private int previousVisibleTotal = 0;

	public GradualLoader(Context context) {
		this.context = context;
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
			
			JSONArrayRequest request = new JSONArrayRequest(
				urlBuilder.build(),
				new JSONArraySuccessListener(this, "onLoadResponse"),
				new JSONErrorListener(this, "onLoadError")
			);
			((App) context.getApplicationContext()).addShortLivedRequest(context, request);
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
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (isLoading()) {
			if (totalItemCount > previousVisibleTotal) {
				previousVisibleTotal = totalItemCount;
			}
		} else {
			if ((totalItemCount - visibleItemCount) <= (firstVisibleItem + VISIBLE_THRESHOLD)) {
				load();
			}
		}
	}
	
	public interface OnLoadCompleteListener {
		
		public void onComplete(JSONArray response);
		
	}
	
	public interface OnLoadErrorListener {
		
		public void onError();
		
	}
	
}
