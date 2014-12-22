package com.myandb.singsong.fragment;

import org.json.JSONArray;

import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.adapter.HolderAdapter;
import com.myandb.singsong.net.GradualLoader;
import com.myandb.singsong.net.GradualLoader.OnLoadCompleteListener;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.widget.FadingActionBarHelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ListFragment extends BaseFragment {
	
	public static final String EXTRA_URL_SEGMENT = "url_segment";
	public static final String EXTRA_QUERY_PARAMS = "query_params";
	public static final String EXTRA_ADAPTER_NAME = "adapter_name";
	
	private ListView listView;
	private ViewGroup fixedHeaderContainer;
	private ViewGroup listViewContainer;
	private View listHeaderView;
	private View fixedHeaderView;
	private View progressContainer;
	private View emptyView;
	private Animation fadeIn;
	private Animation fadeOut;
	private GradualLoader loader;
	private FadingActionBarHelper fadingActionBarHelper;
	private UrlBuilder internalUrlBuilder;
	private ListAdapter adapter;
	private int listViewIndex;
	private int listViewTop;

	@Override
	protected final int getResourceId() {
		return R.layout.fragment_list;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		
		if (isFragmentCreated()) {
			internalUrlBuilder = getUrlBuilderOnArgumentPassed(bundle);
			adapter = getListAdapterOnArgumentPassed(bundle);
			if (internalUrlBuilder == null || adapter == null) {
				internalUrlBuilder = null;
				adapter = null;
			}
		}
	}
	
	private boolean isFragmentCreated() {
		return internalUrlBuilder == null && adapter == null;
	}
	
	private UrlBuilder getUrlBuilderOnArgumentPassed(Bundle bundle) {
		final String segment = bundle.getString(EXTRA_URL_SEGMENT);
		final Bundle params = bundle.getBundle(EXTRA_QUERY_PARAMS);
		
		if (segment != null) {
			UrlBuilder builder = new UrlBuilder();
			builder.s(segment);
			if (params != null) {
				builder.p(params);
			}
			return builder;
		}
		return null;
	}
	
	private ListAdapter getListAdapterOnArgumentPassed(Bundle bundle) {
		final String adapterName = bundle.getString(EXTRA_ADAPTER_NAME);
		if (adapterName != null) {
			return instantiateAdapter(adapterName);
		}
		return null;
	}
	
	private ListAdapter instantiateAdapter(String adapterName) {
		if (adapterName != null) {
			try {
				Class<?> classForAdapter = Class.forName(adapterName);
				return (ListAdapter) classForAdapter.newInstance();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (java.lang.InstantiationException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		fixedHeaderContainer = (ViewGroup) view.findViewById(R.id.fl_fixed_header_container);
		listViewContainer = (ViewGroup) view.findViewById(R.id.fl_listview_container);
		progressContainer = view.findViewById(R.id.fl_progress_container);
		listView = (ListView) view.findViewById(R.id.listview);
		
		if (getEmptyViewResId() != App.INVALID_RESOURCE_ID) {
			emptyView = inflater.inflate(getEmptyViewResId(), listView, false);
			listView.setEmptyView(emptyView);
		}
		
		if (getListHeaderViewResId() != App.INVALID_RESOURCE_ID) {
			listHeaderView = inflater.inflate(getListHeaderViewResId(), listView, false);
			listView.addHeaderView(listHeaderView);
		}
		
		if (getFixedHeaderViewResId() != App.INVALID_RESOURCE_ID) {
			fixedHeaderView = inflater.inflate(getFixedHeaderViewResId(), fixedHeaderContainer, false);
			fixedHeaderContainer.setVisibility(View.VISIBLE);
			fixedHeaderContainer.addView(fixedHeaderView);
		} else {
			fixedHeaderContainer.setVisibility(View.GONE);
		}
	}

	@Override
	protected void initialize(Activity activity) {
		fadeIn = AnimationUtils.loadAnimation(activity, android.R.anim.fade_in);
		fadeOut = AnimationUtils.loadAnimation(activity, android.R.anim.fade_out);
		
		if (loader == null) {
			loader = new GradualLoader(activity);
			setUrlBuilder(internalUrlBuilder);
		}
	}
	
	@Override
	protected void setupViews(Bundle savedInstanceState) {
		listView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (loader != null) {
					loader.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
				}
				
				if (fadingActionBarHelper != null) {
					fadingActionBarHelper.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
				}
			}
		});
	}

	@Override
	protected void onDataChanged() {}
	
	@Override
	public void onResume() {
		super.onResume();
		
		setListViewAdapter();
		
		if (isDataAlive() || loader.isNothingToLoad()) {
			scrollToPreviousPosition();
		} else if (isDataLoadable()) {
			loader.load();
		}
		
		if (listHeaderView != null && fixedHeaderView == null) {
			setActionBarOverlay(true);
			listHeaderView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			fadingActionBarHelper = new FadingActionBarHelper();
			fadingActionBarHelper.setBackground(R.drawable.actionbar_background)
			.setFullyVisiblePosition(listHeaderView.getMeasuredHeight())
			.initialize(getActivity());
		}
	}

	private void setListViewAdapter() {
		if (adapter != null) {
			listView.setAdapter(adapter);
		}
	}
	
	private boolean isDataAlive() {
		return adapter != null && adapter.getCount() > 0;
	}
	
	private boolean isDataLoadable() {
		return adapter != null && loader.isLoadable();
	}
	
	private void scrollToPreviousPosition() {
		if (listViewIndex > 0) {
			listView.setSelectionFromTop(listViewIndex, listViewTop);
		}
		setListShown(true, false);
	}

	@Override
	public void onDestroyView() {
		saveCurrentPosition();
		super.onDestroyView();
	}
	
	private void saveCurrentPosition() {
		listViewIndex = listView.getFirstVisiblePosition();
		View child = listView.getChildAt(0);
		listViewTop = (child == null) ? 0 : child.getTop();
	}
	
	public void setAdapter(ListAdapter adapter) {
		if (adapter != null) {
			this.adapter = adapter;
		}
	}

	public void setFixedHeaderShown(boolean shown) {
		if (fixedHeaderContainer.isShown() == shown) {
			return;
		}
		
		if (shown) {
			fixedHeaderContainer.setVisibility(View.VISIBLE);
		} else {
			fixedHeaderContainer.setVisibility(View.GONE);
		}
	}
	
	public ListAdapter getAdapter() {
		return adapter;
	}
	
	public void setUrlBuilder(UrlBuilder urlBuilder) {
		if (urlBuilder == null) {
			return;
		}
		
		setListShown(false);
		loader.setUrlBuilder(urlBuilder);
		loader.setOnLoadCompleteListener(loadCompleteListener);
		if (adapter != null) {
			if (adapter instanceof HolderAdapter) {
				((HolderAdapter<?, ?>) adapter).clear();
			}
		}
	}
	
	private OnLoadCompleteListener loadCompleteListener = new OnLoadCompleteListener() {
		
		@Override
		public void onComplete(JSONArray response) {
			if (adapter == null) {
				return;
			}
			
			if (adapter instanceof HolderAdapter) {
				setListShown(true);
				((HolderAdapter<?, ?>) adapter).addAll(response);
			}
		}
	};
	
	public void load() {
		loader.load();
	}
	
	public void setListShown(boolean shown) {
		setListShown(shown, true);
    }
	
	private void setListShown(boolean shown, boolean animate) {
		if (listViewContainer.isShown() == shown) {
			return;
		}
		
		if (shown) {
			if (animate) {
				listViewContainer.startAnimation(fadeIn);
			}
			progressContainer.setVisibility(View.GONE);
			listViewContainer.setVisibility(View.VISIBLE);
		} else {
			if (animate) {
				listViewContainer.startAnimation(fadeOut);
			}
			progressContainer.setVisibility(View.VISIBLE);
			listViewContainer.setVisibility(View.GONE);
		}
	}
	
	public void setFadingActionBarTitle(CharSequence title) {
		if (fadingActionBarHelper != null) {
			fadingActionBarHelper.setTitle(title);
		}
	}
	
	public View getEmptyView() {
		return emptyView;
	}
	
	public View getListHeaderView() {
		return listHeaderView;
	}
	
	public View getFixedHeaderView() {
		return fixedHeaderView;
	}
	
	protected int getEmptyViewResId() {
		return App.INVALID_RESOURCE_ID;
	}
	
	protected int getListHeaderViewResId() {
		return App.INVALID_RESOURCE_ID;
	}
	
	protected int getFixedHeaderViewResId() {
		return App.INVALID_RESOURCE_ID;
	}

}
