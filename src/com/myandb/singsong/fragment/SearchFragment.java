package com.myandb.singsong.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListAdapter;

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
		
		USER(R.string.hint_search_user, FriendsAdapter.class, "users"),
		
		MUSIC(R.string.hint_search_music, MusicAdapter.class, "musics"),
		
		WAITING(R.string.hint_search_music, SimpleSongAdapter.class, "songs/root"),
		
		COLLABORATED(R.string.hint_search_music, SimpleSongAdapter.class, "songs/leaf"),
		
		ALL_SONG(R.string.hint_search_music, SimpleSongAdapter.class, "songs/all");
		
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
	
	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		searchType = (SearchType) bundle.getSerializable(EXTRA_SEARCH_TYPE);
	}

	@Override
	protected int getFixedHeaderViewResId() {
		return R.layout.search_view;
	}

	@Override
	protected ListAdapter instantiateAdapter(Activity activity) {
		try {
			return searchType.newAdapterInstance();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (java.lang.InstantiationException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void initialize(Activity activity) {
		super.initialize(activity);
		switch (searchType) {
		case WAITING:
		case COLLABORATED:
		case ALL_SONG:
			horizontalPadding = true;
			verticalPadding = true;
			break;

		default:
			break;
		}
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		super.setupViews(savedInstanceState);
		
		if (getFixedHeaderView() instanceof SearchView) {
			searchView = (SearchView) getFixedHeaderView();
			searchView.setSearchHint(getString(searchType.getHintResId()));
			searchView.setOnActionSearchListener(actionSearchListener);
		}
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
			UrlBuilder urlBuilder = searchType.getUrlBuilder();
			urlBuilder.keyword(keyword);
			if (searchType.equals(SearchType.USER)) {
				urlBuilder.p("req[]", "profile");
			}
			setUrlBuilder(urlBuilder);
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
