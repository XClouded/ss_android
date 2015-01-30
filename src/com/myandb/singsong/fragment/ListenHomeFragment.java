package com.myandb.singsong.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.dialog.CategoryListDialog;
import com.myandb.singsong.fragment.SearchFragment.SearchType;
import com.myandb.singsong.model.Category;
import com.myandb.singsong.pager.ListenPagerAdapter;
import com.myandb.singsong.pager.ListenPagerAdapter.SongType;
import com.myandb.singsong.util.Utility;

public class ListenHomeFragment extends FragmentPagerFragment {
	
	public static final String EXTRA_CATEGORY = "category";
	public static final String EXTRA_SONG_TYPE = "song_type";
	
	private SongType songType;
	private Category category;
	private MenuItem categoryMenuItem;
	private ListenPagerAdapter pagerAdapter;
	private View songTypeChangeView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		
		if (category == null) {
			Gson gson = Utility.getGsonInstance();
			String categoryInJson = bundle.getString(EXTRA_CATEGORY);
			category = gson.fromJson(categoryInJson, Category.class);
		}
		
		if (songType == null) {
			songType = (SongType) bundle.getSerializable(EXTRA_SONG_TYPE);
		}
	}
	
	@SuppressLint("InflateParams")
	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		super.onViewInflated(view, inflater);
		songTypeChangeView = inflater.inflate(R.layout.action_song_type, null, false);
	}

	@Override
	protected void initialize(Activity activity) {
		super.initialize(activity);
		
		if (category == null) {
			category = new Category(0);
		}
		
		if (songType == null) {
			songType = SongType.COLLABORATED;
		}
	}
	
	@Override
	protected void setupViews(Bundle savedInstanceState) {
		super.setupViews(savedInstanceState);
		
		songTypeChangeView.setOnClickListener(songTypeChangeClickListener);
		changeMenuItemTitle(songType);
	}
	
	private OnClickListener songTypeChangeClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
			if (songType.equals(SongType.COLLABORATED)) {
				popupMenu.inflate(R.menu.waiting);
			} else {
				popupMenu.inflate(R.menu.collaborated);
			}
			popupMenu.setOnMenuItemClickListener(menuItemClickListener);
			popupMenu.show();
		}
	};
	
	private OnMenuItemClickListener menuItemClickListener = new OnMenuItemClickListener() {
		
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.action_song_type_waiting:
				changeSongType(SongType.WAITING);
				return true;
				
			case R.id.action_song_type_collaborted:
				changeSongType(SongType.COLLABORATED);
				return true;

			default:
				return false;
			}
		}
	};
	
	@Override
	protected FragmentPagerAdapter instantiatePagerAdapter() {
		pagerAdapter = new ListenPagerAdapter(getChildFragmentManager());
		if (category != null) {
			pagerAdapter.setCategoryId(category.getId());
		}
		
		if (songType != null) {
			pagerAdapter.setSongType(songType);
		}
		return pagerAdapter;
	}
	
	public void changeSongType(SongType songType) {
		this.songType = songType;
		changeMenuItemTitle(songType);
		changePagerAdapter(songType);
	}

	public void changeCategory(Category category) {
		this.category = category;
		changeMenuItemTitle(category);
		changePagerAdapter(category);
	}
	
	private void changeMenuItemTitle(Category category) {
		if (categoryMenuItem != null && category != null) {
			categoryMenuItem.setTitle(category.getTitle());
		}
	}
	
	private void changeMenuItemTitle(SongType songType) {
		if (songTypeChangeView != null && songType != null) {
			TextView title = (TextView) songTypeChangeView.findViewById(R.id.tv_song_type_title);
			title.setText(songType.getTitleResId());
		}
	}
	
	private void changePagerAdapter(Category category) {
		if (pagerAdapter != null && category != null) {
			pagerAdapter.setCategoryId(category.getId());
			pagerAdapter.refresh();
		}
	}
	
	private void changePagerAdapter(SongType songType) {
		if (pagerAdapter != null && songType != null) {
			pagerAdapter.setSongType(songType);
			pagerAdapter.refresh();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.search, menu);
		inflater.inflate(R.menu.category, menu);
		categoryMenuItem = menu.findItem(R.id.action_category);
		changeMenuItemTitle(category);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_search:
			Bundle bundle = new Bundle();
			if (songType != null) {
				if (songType.equals(SongType.COLLABORATED)) {
					bundle.putSerializable(SearchFragment.EXTRA_SEARCH_TYPE, SearchType.COLLABORATED);
				} else {
					bundle.putSerializable(SearchFragment.EXTRA_SEARCH_TYPE, SearchType.WAITING);
				}
			} else {
				bundle.putSerializable(SearchFragment.EXTRA_SEARCH_TYPE, SearchType.COLLABORATED);
			}
			bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, getString(R.string.fragment_search_song_action_title));
			Intent intent = new Intent(getActivity(), RootActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, SearchFragment.class.getName());
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			startFragment(intent);
			return true;
			
		case R.id.action_category:
			Bundle arguments = new Bundle();
			arguments.putInt(CategoryListDialog.EXTRA_CURRENT_CATEGORY_ID, category != null ? category.getId() : 0);
			CategoryListDialog dialog = new CategoryListDialog();
			dialog.setArguments(arguments);
			dialog.show(getChildFragmentManager(), "");
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			getSupportActionBar().setCustomView(songTypeChangeView);
			getSupportActionBar().setDisplayShowTitleEnabled(false);
			getSupportActionBar().setDisplayShowCustomEnabled(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
