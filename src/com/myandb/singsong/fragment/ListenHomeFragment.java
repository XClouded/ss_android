package com.myandb.singsong.fragment;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListAdapter;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.adapter.CollaboratedAdapter;
import com.myandb.singsong.fragment.SearchFragment.SearchType;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.StringFormatter;

public class ListenHomeFragment extends ListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	protected ListAdapter instantiateAdapter(Activity activity) {
		return new CollaboratedAdapter();
	}

	@Override
	protected UrlBuilder instantiateUrlBuilder(Activity activity) {
		final String startDate = StringFormatter.getDateString(Calendar.DATE, -1);
		return new UrlBuilder().s("songs").s("leaf").p("order", "liking_num").start(startDate);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		/*
		setActionBarTitle("");
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		RelativeLayout relative=new RelativeLayout(getApplicationContext());
		TextView tv = new TextView(getApplicationContext());
		tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Logger.log("asdfasdf");
			}
		});
		tv.setText("Asdfasdf");
		relative.addView(tv);
		getSupportActionBar().setCustomView(relative);
		*/
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
			bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, "노래 검색");
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
