package com.myandb.singsong.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.fragment.BaseFragment;
import com.myandb.singsong.fragment.ListFragment;
import com.myandb.singsong.fragment.MusicListFragment;
import com.myandb.singsong.model.Category;

public class MusicCategoryAdapter extends HolderAdapter<Category, MusicCategoryAdapter.CategoryHolder> {

	public MusicCategoryAdapter() {
		super(Category.class);
	}

	@Override
	public CategoryHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.row_music_category, parent, false);
		return new CategoryHolder(view);
	}

	@Override
	public void onBindViewHolder(Context context, CategoryHolder viewHolder, Category category, int position) {
		viewHolder.btnCategory.setText(category.getTitle());
		viewHolder.btnCategory.setTag(category);
		viewHolder.btnCategory.setOnClickListener(categoryClickListener);
	}
	
	private OnClickListener categoryClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			final Category category = (Category) v.getTag();
			
			String segment = "musics/";
			Bundle bundle = new Bundle();
			Bundle params = new Bundle();
			params.putString("genre_id", String.valueOf(category.getId()));
			bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, category.getTitle());
			bundle.putString(ListFragment.EXTRA_URL_SEGMENT, segment);
			bundle.putBundle(ListFragment.EXTRA_QUERY_PARAMS, params);
			Intent intent = new Intent(v.getContext(), RootActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, MusicListFragment.class.getName());
			((BaseActivity) v.getContext()).changePage(intent);			
		}
	};
	
	public static final class CategoryHolder extends ViewHolder {
		
		public Button btnCategory;

		public CategoryHolder(View view) {
			super(view);
			
			btnCategory = (Button) view.findViewById(R.id.btn_category);
		}
		
	}

}
