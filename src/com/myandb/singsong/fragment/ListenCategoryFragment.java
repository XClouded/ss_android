package com.myandb.singsong.fragment;

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
		SongCategoryAdapter adapter = new SongCategoryAdapter();
		adapter.addItem(new Category("πﬂ∂ÛµÂ", R.drawable.img_ballad));
		adapter.addItem(new Category("¥ÌΩ∫", R.drawable.img_dance));
		adapter.addItem(new Category("∑¶/»¸«’", R.drawable.img_hiphop));
		adapter.addItem(new Category("R&B/Soul", R.drawable.img_rnb));
		adapter.addItem(new Category("∑œ", R.drawable.img_rock));
		adapter.addItem(new Category("OST", R.drawable.img_ost));
		adapter.addItem(new Category("ø£≈Õ≈◊¿Œ∏’∆Æ", R.drawable.img_entertainment));
		return adapter;
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		super.setupViews(savedInstanceState);
		
		int padding = getResources().getDimensionPixelSize(R.dimen.margin);
		getListView().setDivider(new ColorDrawable(Color.TRANSPARENT));
		getListView().setDividerHeight(padding);
		getListView().setPadding(padding, padding, padding, 0);
	}

}
