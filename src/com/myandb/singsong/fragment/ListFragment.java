package com.myandb.singsong.fragment;

import org.json.JSONArray;

import com.myandb.singsong.R;
import com.myandb.singsong.adapter.HolderAdapter;
import com.myandb.singsong.net.GradualLoader;
import com.myandb.singsong.net.GradualLoader.OnLoadCompleteListener;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.widget.FadingActionBarHelper;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public abstract class ListFragment extends BaseFragment {
	
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

	@Override
	protected int getResourceId() {
		return R.layout.fragment_list;
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
		
		loader = new GradualLoader(activity);
	}
	
	@Override
	protected void setupViews() {
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
	public void onResume() {
		super.onResume();
		
		if (listHeaderView != null) {
			setActionBarOverlay(true);
			fadingActionBarHelper = new FadingActionBarHelper();
			fadingActionBarHelper.setBackground(R.drawable.actionbar_background)
			.setFullyVisiblePosition(2000)
			.setHome(R.drawable.ic_action_back)
			.initialize(getActivity());
		}
	}

	public void setAdapter(final ListAdapter adapter) {
		listView.setAdapter(adapter);
		getGradualLoader().setOnLoadCompleteListener(new OnLoadCompleteListener() {
			
			@Override
			public void onComplete(JSONArray response) {
				if (adapter instanceof HolderAdapter) {
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
		return listView.getAdapter();
	}
	
	public GradualLoader getGradualLoader() {
		return loader;
	}
	
	public void setUrlBuilder(UrlBuilder urlBuilder) {
		getGradualLoader().setUrlBuilder(urlBuilder);
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
