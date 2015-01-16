package com.myandb.singsong.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.fragment.SearchFragment.SearchType;
import com.myandb.singsong.pager.ListenPagerAdapter;

public class ListenHomeFragment extends FragmentPagerFragment {
	
	public static final String EXTRA_CATEGORY_ID = "category_id";
	
	private int categoryId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		categoryId = bundle.getInt(EXTRA_CATEGORY_ID);
	}

	@Override
	protected FragmentPagerAdapter instantiatePagerAdapter() {
		ListenPagerAdapter pagerAdapter = new ListenPagerAdapter(getChildFragmentManager());
		pagerAdapter.setCategoryId(categoryId);
		return pagerAdapter;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.search, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_search:
			Bundle bundle = new Bundle();
			bundle.putSerializable(SearchFragment.EXTRA_SEARCH_TYPE, SearchType.COLLABORATED);
			bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, getString(R.string.fragment_search_song_action_title));
			Intent intent = new Intent(getActivity(), RootActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, SearchFragment.class.getName());
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			startFragment(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
