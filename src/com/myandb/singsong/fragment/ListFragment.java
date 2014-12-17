package com.myandb.singsong.fragment;

import org.json.JSONArray;

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
	private View listHeaderView;
	private View listViewContainer;
	private View fixedHeaderContainer;
	private View fixedHeaderView;
	private View progressContainer;
	private View emptyView;
	private Animation fadeIn;
	private Animation fadeOut;
	private GradualLoader loader;
	private FadingActionBarHelper fadingActionBarHelper;
	private UrlBuilder internalUrlBuilder;
	private ListAdapter internalAdapter;
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
			internalAdapter = getListAdapterOnArgumentPassed(bundle);
			if (internalUrlBuilder == null || internalAdapter == null) {
				internalUrlBuilder = null;
				internalAdapter = null;
			}
		}
	}
	
	private boolean isFragmentCreated() {
		return internalUrlBuilder == null && internalAdapter == null;
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
		fixedHeaderContainer = view.findViewById(R.id.fl_fixed_header_container);
		listViewContainer = view.findViewById(R.id.fl_listview_container);
		progressContainer = view.findViewById(R.id.fl_progress_container);
		listView = (ListView) view.findViewById(R.id.listview);
		
		emptyView = inflateEmptyView(inflater);
		listHeaderView = inflateListHeaderView(inflater);
		fixedHeaderView = inflateFixedHeaderView(inflater);
		
		if (emptyView != null) {
			listView.setEmptyView(emptyView);
		}
		
		if (listHeaderView != null) {
			listView.addHeaderView(listHeaderView);
		}
		
		if (fixedHeaderView != null) {
			if (fixedHeaderContainer instanceof ViewGroup) {
				((ViewGroup) fixedHeaderContainer).addView(fixedHeaderView);
				fixedHeaderContainer.setVisibility(View.VISIBLE);
			} else {
				throw new IllegalStateException("Fixed Header Container must be instance of ViewGroup");
			}
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
		
		if (internalAdapter != null) {
			setAdapter(internalAdapter);
			if (internalAdapter.getCount() == 0 && loader.isLoadable()) {
				load();
			} else {
				setListShown(true);
				if (listViewIndex > 0) {
					listView.setSelectionFromTop(listViewIndex, listViewTop);
				}
			}
		}
		
		if (listHeaderView != null) {
			setActionBarOverlay(true);
			listHeaderView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			fadingActionBarHelper = new FadingActionBarHelper();
			fadingActionBarHelper.setBackground(R.drawable.actionbar_background)
			.setFullyVisiblePosition(listHeaderView.getMeasuredHeight())
			.initialize(getActivity());
		}
	}

	@Override
	public void onDestroyView() {
		listViewIndex = listView.getFirstVisiblePosition();
		View child = listView.getChildAt(0);
		listViewTop = (child == null) ? 0 : child.getTop();
		super.onDestroyView();
	}
	
	public void setInternalAdapter(ListAdapter adapter) {
		if (internalAdapter == null) {
			internalAdapter = adapter;
		}
	}

	private void setAdapter(final ListAdapter adapter) {
		if (adapter == null) {
			return;
		}
		
		listView.setAdapter(adapter);
		getGradualLoader().setOnLoadCompleteListener(new OnLoadCompleteListener() {
			
			@Override
			public void onComplete(JSONArray response) {
				if (internalAdapter instanceof HolderAdapter) {
					setListShown(true);
					((HolderAdapter<?, ?>) adapter).addAll(response);
				}
			}
		});
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
	
	public ListView getListView() {
		return listView;
	}
	
	public ListAdapter getAdapter() {
		return internalAdapter;
	}
	
	public GradualLoader getGradualLoader() {
		return loader;
	}
	
	public void setUrlBuilder(UrlBuilder urlBuilder) {
		if (urlBuilder == null || loader.isLoadable()) {
			return;
		}
		
		loader.setUrlBuilder(urlBuilder);
	}
	
	public void load() {
		getGradualLoader().load();
	}
	
	public void setListShown(boolean shown) {
        if (listViewContainer.isShown() == shown) {
            return;
        }
        
        if (shown) {
        	listViewContainer.startAnimation(fadeIn);
        	progressContainer.setVisibility(View.GONE);
            listViewContainer.setVisibility(View.VISIBLE);
        } else {
        	listViewContainer.startAnimation(fadeOut);
        	progressContainer.setVisibility(View.VISIBLE);
        	listViewContainer.setVisibility(View.GONE);
        }
    }
	
	public void setFadingActionBarTitle(CharSequence title) {
		if (fadingActionBarHelper != null) {
			fadingActionBarHelper.setTitle(title);
		}
	}
	
	protected View inflateEmptyView(LayoutInflater inflater) {
		return null;
	}
	
	protected View inflateListHeaderView(LayoutInflater inflater) {
		return null;
	}
	
	protected View inflateFixedHeaderView(LayoutInflater inflater) {
		return null;
	}

}
