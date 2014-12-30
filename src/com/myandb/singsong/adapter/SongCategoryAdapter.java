package com.myandb.singsong.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.model.Category;

public class SongCategoryAdapter extends HolderAdapter<Category, SongCategoryAdapter.CategoryHolder> {

	public SongCategoryAdapter() {
		super(Category.class);
	}

	@Override
	public CategoryHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.row_song_category, parent, false);
		return new CategoryHolder(view);
	}

	@Override
	public void onBindViewHolder(Context context, CategoryHolder viewHolder, int position) {
		final Category category = getItem(position);
		
		viewHolder.tvCategoryText.setText(category.getTitle());
		viewHolder.ivCategoryImage.setImageResource(category.getImageResourceId());
	}
	
	public static final class CategoryHolder extends ViewHolder {
		
		public ImageView ivCategoryImage;
		public TextView tvCategoryText;
		
		public CategoryHolder(View view) {
			super(view);
			
			ivCategoryImage = (ImageView) view.findViewById(R.id.iv_category_image);
			tvCategoryText = (TextView) view.findViewById(R.id.tv_category_text);
		}
		
	}

}
