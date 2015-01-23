package com.myandb.singsong.pager;

import java.util.Calendar;

import com.myandb.singsong.adapter.CollaboratedAdapter;
import com.myandb.singsong.adapter.SimpleSongAdapter;
import com.myandb.singsong.fragment.ListFragment;
import com.myandb.singsong.util.StringFormatter;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ListenPagerAdapter extends FragmentPagerAdapter {
	
	private int categoryId;

	public ListenPagerAdapter(FragmentManager fm) {
		super(fm);
	}
	
	public void setCategoryId(int id) {
		this.categoryId = id;
	}
	
	@Override
	public Fragment getItem(int position) {
		ListFragment fragment = new ListFragment();
		String segment = "songs/leaf";
		Bundle bundle = new Bundle();
		Bundle params = new Bundle();
		params.putString("genre_id", String.valueOf(categoryId));
		
		switch (position) {
		case 0:
			final String startDate = StringFormatter.getDateString(Calendar.DATE, -1);
			params.putString("order", "liking_num");
			params.putString("start", startDate);
			break;
			
		case 1:
			params.putString("order", "created_at");
			break;
			
		default:
			return null;
		}
		
		bundle.putString(ListFragment.EXTRA_URL_SEGMENT, segment);
		bundle.putBundle(ListFragment.EXTRA_QUERY_PARAMS, params);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
			bundle.putString(ListFragment.EXTRA_ADAPTER_NAME, CollaboratedAdapter.class.getName());
		} else {
			bundle.putString(ListFragment.EXTRA_ADAPTER_NAME, SimpleSongAdapter.class.getName());
			bundle.putBoolean(ListFragment.EXTRA_HORIZONTAL_PADDING, true);
		}
		bundle.putBoolean(ListFragment.EXTRA_VERTICAL_PADDING, true);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case 0:
			return "인기";
			
		case 1:
			return "최신";
			
		default:
			return null;
		}
	}

	@Override
	public int getCount() {
		return 2;
	}

}
