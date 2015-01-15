package com.myandb.singsong.pager;

import com.myandb.singsong.fragment.MusicDetailListFragment;
import com.myandb.singsong.fragment.MusicDetailListFragment.Type;

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
		Fragment fragment = new MusicDetailListFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(MusicDetailListFragment.EXTRA_MUSIC_ID, musicId);
		
		switch (position) {
		case 0:
			bundle.putSerializable(MusicDetailListFragment.EXTRA_LIST_TYPE, Type.POPULAR);
			break;
			
		case 1:
			bundle.putSerializable(MusicDetailListFragment.EXTRA_LIST_TYPE, Type.RECENT);
			break;
			
		case 2:
			bundle.putSerializable(MusicDetailListFragment.EXTRA_LIST_TYPE, Type.FRIEND);
			break;

		default:
			return null;
		}
		
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
