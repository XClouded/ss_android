package com.myandb.singsong.net;

import org.json.JSONArray;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.myandb.singsong.App;

public class GradualLoader implements OnScrollListener {
	
	public static final int INITIAL_LOAD_NUM = 25;
	public static final int ADDITIONAL_LOAD_NUM = 15;
	public static final int VISIBLE_THRESHOLD = 10; 

	private Context context;
	private OnLoadCompleteListener completeListener;
	private OnLoadErrorListener errorListener;
	private UrlBuilder urlBuilder;
	private Handler handler;
	private int count;
	private int requiredTake;
	private int initialLoadNum;
	private int previousTotalCount = 0;
	private boolean nothingToLoad;
	private boolean loading;

	public GradualLoader(Context context) {
		this.context = context;
		handler = new Handler(Looper.getMainLooper());
	}
	
	public void setUrlBuilder(UrlBuilder urlBuilder) {
		this.urlBuilder = urlBuilder;
		this.nothingToLoad = false;
		this.loading = false;
		this.requiredTake = 0;
		this.previousTotalCount = 0;
		
		try {
			this.count = Integer.parseInt(urlBuilder.getParam("skip"));
		} catch (NumberFormatException e) {
			this.count = 0;
		}
		
		try {
			int builderTake = Integer.parseInt(urlBuilder.getParam("take"));
			this.initialLoadNum = builderTake > 0 ? builderTake : INITIAL_LOAD_NUM;
		} catch (NumberFormatException e) {
			this.initialLoadNum = INITIAL_LOAD_NUM;
		}
	}
	
	public void setOnLoadCompleteListener(OnLoadCompleteListener listener) {
		completeListener = listener;
	}
	
	public void setOnLoadErrorListener(OnLoadErrorListener listener) {
		errorListener = listener;
	}
	
	public void load() {
		if (isLoadable() && !nothingToLoad) {
			int take = count > 0 ? ADDITIONAL_LOAD_NUM : initialLoadNum;
			executeQuery(count, take);
		}
	}
	
	private void executeQuery(int skip, int take) {
		if (!loading) {
			loading = true;
			
			urlBuilder.skip(skip);
			urlBuilder.take(take);
			requiredTake = take;
			
			final JSONArrayRequest request = new JSONArrayRequest(
				urlBuilder.build(),
				new JSONArraySuccessListener(this, "onLoadResponse"),
				new JSONErrorListener(this, "onLoadError")
			);
			
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					((App) context.getApplicationContext()).addShortLivedRequest(context, request);
				}
			}, 300);
		}
	}
	
	public void onLoadResponse(JSONArray response) {
		count += response.length();
		
		if (response.length() < requiredTake) {
			nothingToLoad = true;
		}
		
		if (completeListener != null) {
			completeListener.onComplete(response);
		}
		
		loading = false;
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
	
	public boolean isNothingToLoad() {
		return nothingToLoad;
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (totalItemCount > previousTotalCount) {
			previousTotalCount = totalItemCount;
		}
		
		if (!isLoading()) {
			if ((previousTotalCount - visibleItemCount) <= (firstVisibleItem + VISIBLE_THRESHOLD)) {
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
