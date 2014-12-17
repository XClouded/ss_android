package com.myandb.singsong.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.myandb.singsong.R;
import com.myandb.singsong.adapter.FriendsAdapter;
import com.myandb.singsong.adapter.HolderAdapter;
import com.myandb.singsong.adapter.MusicAdapter;
import com.myandb.singsong.adapter.SimpleSongAdapter;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.widget.SearchView;
import com.myandb.singsong.widget.SearchView.OnActionSearchListener;

public class SearchFragment extends ListFragment {
	
	public enum SearchType {
		
		USER(R.string.hint_id, FriendsAdapter.class, "users"),
		
		MUSIC(R.string.hint_id, MusicAdapter.class, "musics"),
		
		WAITING(R.string.hint_id, SimpleSongAdapter.class, "songs/root"),
		
		COLLABORATED(R.string.hint_id, SimpleSongAdapter.class, "songs/leaf"),
		
		ALL_SONG(R.string.hint_id, SimpleSongAdapter.class, "songs/all");
		
		private int hintResId;
		private Class<?> adapterClass;
		private UrlBuilder builder;
		
		SearchType(int hintResId, Class<?> adapterClass, String segment) {
			this.hintResId = hintResId;
			this.adapterClass = adapterClass;
			this.builder = new UrlBuilder().s(segment);
		}
		
		public UrlBuilder getUrlBuilder() {
			return builder;
		}
		
		public HolderAdapter<?, ?> newAdapterInstance() 
				throws IllegalAccessException, java.lang.InstantiationException {
			return (HolderAdapter<?, ?>) adapterClass.newInstance();
		}
		
		public int getHintResId() {
			return hintResId;
		}
	}
	
	public static final String EXTRA_SEARCH_TYPE = "search_type";

	private SearchView searchView;
	private SearchType searchType;
	private HolderAdapter<?, ?> adapter;
	
	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		searchType = (SearchType) bundle.getSerializable(EXTRA_SEARCH_TYPE);
	}

	@SuppressLint("InflateParams")
	@Override
	protected View inflateFixedHeaderView(LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.search_view, null);
		if (view instanceof SearchView) {
			searchView = (SearchView) view;
		}
		return view; 
	}

	@Override
	protected void initialize(Activity activity) {
		super.initialize(activity);
		if (getAdapter() == null) {
			try {
				adapter = searchType.newAdapterInstance();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (java.lang.InstantiationException e) {
				e.printStackTrace();
			}
			setInternalAdapter(adapter);
		}
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		super.setupViews(savedInstanceState);
		searchView.setSearchHint(getString(searchType.getHintResId()));
		searchView.setOnActionSearchListener(actionSearchListener);
	}
	
	private OnActionSearchListener actionSearchListener = new OnActionSearchListener() {
		
		@Override
		public void onSearch(CharSequence text) {
			if (text.length() > 0) {
				search(text.toString());
			}
		}
	};
	
	private void search(String keyword) {
		if (keyword != null && keyword.length() > 0) {
			searchView.hideSoftKeyboard();
			adapter.clear();
			setListShown(false);
			UrlBuilder urlBuilder = searchType.getUrlBuilder();
			urlBuilder.keyword(keyword);
			if (searchType.equals(SearchType.USER)) {
				urlBuilder.p("req[]", "profile");
			}
			getGradualLoader().setUrlBuilder(urlBuilder);
			load();
		}
	}
	
	@Override
	protected void onDataChanged() {}

	@Override
	public void onResume() {
		super.onResume();
		setListShown(true);
		searchView.showSoftKeyboard();
	}
	
}
