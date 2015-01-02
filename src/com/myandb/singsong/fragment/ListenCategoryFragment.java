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
		adapter.addItem(new Category(1, "�߶��", R.drawable.img_ballad));
		adapter.addItem(new Category(2, "��", R.drawable.img_dance));
		adapter.addItem(new Category(3, "��/����", R.drawable.img_hiphop));
		adapter.addItem(new Category(4, "R&B/Soul", R.drawable.img_rnb));
		adapter.addItem(new Category(5, "��", R.drawable.img_rock));
		adapter.addItem(new Category(6, "OST", R.drawable.img_ost));
		adapter.addItem(new Category(7, "Ʈ��Ʈ", R.drawable.img_ost));
		adapter.addItem(new Category(8, "��ũ", R.drawable.img_ost));
		adapter.addItem(new Category(9, "�ε�����", R.drawable.img_ost));
//		adapter.addItem(new Category(100, "�������θ�Ʈ", R.drawable.img_entertainment));
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
