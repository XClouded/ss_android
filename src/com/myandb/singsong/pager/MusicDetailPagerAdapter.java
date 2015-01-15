package com.myandb.singsong.pager;

import com.myandb.singsong.adapter.ChildrenSongAdapter;
import com.myandb.singsong.fragment.ListFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MusicDetailPagerAdapter extends FragmentPagerAdapter {
	
	private int musicId;

	public MusicDetailPagerAdapter(FragmentManager fm) {
		super(fm);
	}
	
	public void setMusicId(int id) {
		musicId = id;
	}

	@Override
	public Fragment getItem(int position) {
		ListFragment fragment = new ListFragment();
		String segment = "";
		Bundle bundle = new Bundle();
		Bundle params = new Bundle();
		
		switch (position) {
		case 0:
			segment += "musics/";
			segment += String.valueOf(musicId) + "/";
			segment += "songs/root";
			params.putString("order", "liking_num");
			break;
			
		case 1:
			segment += "musics/";
			segment += String.valueOf(musicId) + "/";
			segment += "songs/root";
			params.putString("order", "created_at");
			break;
			
		case 2:
			segment += "followings/musics/";
			segment += String.valueOf(musicId) + "/";
			segment += "songs/root";
			break;
			

		default:
			return null;
		}
		
		bundle.putString(ListFragment.EXTRA_URL_SEGMENT, segment);
		bundle.putBundle(ListFragment.EXTRA_QUERY_PARAMS, params);
		bundle.putString(ListFragment.EXTRA_ADAPTER_NAME, ChildrenSongAdapter.class.getName());
		bundle.putInt(ListFragment.EXTRA_COLUMN_NUM, 2);
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
			
		case 2:
			return "친구";
			
		default:
			return "";
		}
	}

	@Override
	public int getCount() {
		return 3;
	}

}
