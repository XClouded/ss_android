package com.myandb.singsong.fragment;

import org.json.JSONArray;

import com.myandb.singsong.R;
import com.myandb.singsong.adapter.HolderAdapter;
import com.myandb.singsong.net.GradualLoader;
import com.myandb.singsong.net.GradualLoader.OnLoadCompleteListener;
import com.myandb.singsong.net.UrlBuilder;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
	}

	@Override
	protected void initialize(Activity activity) {
		fadeIn = AnimationUtils.loadAnimation(activity, android.R.anim.fade_in);
		fadeOut = AnimationUtils.loadAnimation(activity, android.R.anim.fade_out);
		
		loader = new GradualLoader(activity);
	}

	@Override
	protected void setupViews() {
		loader.setListView(listView);
		
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
        	progressContainer.startAnimation(fadeOut);
        	listViewContainer.startAnimation(fadeIn);
        	progressContainer.setVisibility(View.GONE);
            listViewContainer.setVisibility(View.VISIBLE);
        } else {
        	progressContainer.startAnimation(fadeIn);
        	listViewContainer.startAnimation(fadeOut);
        	progressContainer.setVisibility(View.VISIBLE);
        	listViewContainer.setVisibility(View.GONE);
        }
    }
	
	protected abstract View inflateEmptyView(LayoutInflater inflater);
	
	protected abstract View inflateListHeaderView(LayoutInflater inflater);
	
	protected abstract View inflateFixedHeaderView(LayoutInflater inflater);

}
