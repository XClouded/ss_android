package com.myandb.singsong.fragment;

import java.util.ArrayList;

import com.myandb.singsong.R;
import com.myandb.singsong.adapter.SongCategoryAdapter;
import com.myandb.singsong.model.Category;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ListAdapter;

public class ListenCategoryFragment extends ListFragment {

	@Override
	protected ListAdapter instantiateAdapter(Activity activity) {
		ArrayList<Category> cgs = new ArrayList<Category>();
		for (int i = 1; i < 10; i++) {
			cgs.add(new Category(i));
		}
//		adapter.addItem(new Category(100, "엔터테인먼트", R.drawable.img_entertainment));
		
		SongCategoryAdapter adapter = new SongCategoryAdapter();
		adapter.addAll(cgs);
		return adapter;
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		super.setupViews(savedInstanceState);
		
		int padding = getResources().getDimensionPixelSize(R.dimen.margin);
		getListView().setDivider(new ColorDrawable(Color.TRANSPARENT));
		getListView().setDividerHeight(padding);
		getListView().setPadding(padding, padding, padding, 0);
		getListView().setVerticalScrollBarEnabled(false);
	}

}
