package com.myandb.singsong.fragment;

import com.myandb.singsong.adapter.MusicAdapter;
import com.myandb.singsong.adapter.MusicAdapter.LayoutType;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListAdapter;

public class MusicListFragment extends ListFragment {
	
	public static final String EXTRA_SHOW_NUM = "show_num";
	
	private boolean showNum;

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		showNum = bundle.getBoolean(EXTRA_SHOW_NUM);
	}

	@Override
	protected ListAdapter instantiateAdapter(Activity activity) {
		if (showNum) {
			return new MusicAdapter(LayoutType.NORMAL_POPULAR);
		} else {
			return new MusicAdapter();
		}
	}

}
