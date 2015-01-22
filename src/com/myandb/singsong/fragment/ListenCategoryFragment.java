package com.myandb.singsong.fragment;

import java.util.ArrayList;

import com.myandb.singsong.adapter.SongCategoryAdapter;
import com.myandb.singsong.model.Category;

import android.app.Activity;
import android.widget.ListAdapter;

public class ListenCategoryFragment extends ListFragment {

	@Override
	protected ListAdapter instantiateAdapter(Activity activity) {
		ArrayList<Category> cgs = new ArrayList<Category>();
		for (int i = 1; i < 10; i++) {
			cgs.add(new Category(i));
		}
		
		SongCategoryAdapter adapter = new SongCategoryAdapter();
		adapter.addAll(cgs);
		return adapter;
	}

}
