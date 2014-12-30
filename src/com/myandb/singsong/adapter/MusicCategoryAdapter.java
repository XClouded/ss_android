package com.myandb.singsong.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.myandb.singsong.R;
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
	public void onBindViewHolder(Context context, CategoryHolder viewHolder, int position) {
		final Category category = getItem(position);
		
		viewHolder.btnCategory.setText(category.getTitle());
	}
	
	public static final class CategoryHolder extends ViewHolder {
		
		public Button btnCategory;

		public CategoryHolder(View view) {
			super(view);
			
			btnCategory = (Button) view.findViewById(R.id.btn_category);
		}
		
	}

}
