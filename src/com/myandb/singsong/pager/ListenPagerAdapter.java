package com.myandb.singsong.pager;

import java.util.Calendar;

import com.myandb.singsong.R;
import com.myandb.singsong.adapter.SongAdapter;
import com.myandb.singsong.adapter.SimpleSongAdapter;
import com.myandb.singsong.fragment.BaseFragment;
import com.myandb.singsong.fragment.ListFragment;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.Utils;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ListenPagerAdapter extends FragmentPagerAdapter {
	
	public enum SongType {
		
		WAITING("root", R.string.song_waiting),
		
		COLLABORATED("leaf", R.string.song_collaborated),
		
		ALL("all", R.string.song_collaborated);
		
		private String segment;
		private int titleResId;
		
		SongType(String segment, int titleResId) {
			this.segment = segment;
			this.titleResId = titleResId;
		}
		
		public String getSegment() {
			return segment;
		}
		
		public int getTitleResId() {
			return titleResId;
		}
		
	}
	
	private int categoryId;
	private SongType songType;
	private ListFragment popularFragment;
	private ListFragment recentFragment;

	public ListenPagerAdapter(FragmentManager fm) {
		super(fm);
		songType = SongType.COLLABORATED;
	}
	
	public void setCategoryId(int id) {
		this.categoryId = id;
	}
	
	public void setSongType(SongType songType) {
		this.songType = songType;
	}
	
	public void refresh() {
		if (popularFragment != null) {
			final String startDate = Utils.getDateString(Calendar.DATE, -1);
			UrlBuilder urlBuilder = new UrlBuilder();
			String order = "liking_num";
			if (songType.equals(SongType.WAITING)) {
				order = "collabo_num";
			}
			urlBuilder.s("songs").s(songType.getSegment()).p("genre_id", categoryId).p("order", order).p("start", startDate);
			popularFragment.setUrlBuilder(urlBuilder);
			popularFragment.load();
		}
		
		if (recentFragment != null) {
			UrlBuilder urlBuilder = new UrlBuilder();
			urlBuilder.s("songs").s(songType.getSegment()).p("genre_id", categoryId).p("order", "created_at");
			recentFragment.setUrlBuilder(urlBuilder);
			recentFragment.load();
		}
	}
	
	@Override
	public Fragment getItem(int position) {
		ListFragment fragment = new ListFragment();
		String segment = "songs/" + songType.getSegment();
		Bundle bundle = new Bundle();
		Bundle params = new Bundle();
		params.putString("genre_id", String.valueOf(categoryId));
		
		switch (position) {
		case 0:
			final String startDate = Utils.getDateString(Calendar.DATE, -1);
			if (songType.equals(SongType.WAITING)) {
				params.putString("order", "collabo_num");
			} else {
				params.putString("order", "liking_num");
			}
			params.putString("start", startDate);
			popularFragment = fragment;
			break;
			
		case 1:
			params.putString("order", "created_at");
			recentFragment = fragment;
			break;
			
		default:
			return null;
		}
		
		bundle.putString(ListFragment.EXTRA_URL_SEGMENT, segment);
		bundle.putBundle(ListFragment.EXTRA_QUERY_PARAMS, params);
		bundle.putBoolean(BaseFragment.EXTRA_ACTIONBAR_DISABLED, true);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
			bundle.putString(ListFragment.EXTRA_ADAPTER_NAME, SongAdapter.class.getName());
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

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

}
