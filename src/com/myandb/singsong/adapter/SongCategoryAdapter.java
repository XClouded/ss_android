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
	
	private int currentCategoryId;

	public SongCategoryAdapter() {
		this(0);
	}
	
	public SongCategoryAdapter(int currentCategoryId) {
		super(Category.class);
		this.currentCategoryId = currentCategoryId;
	}

	@Override
	public CategoryHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.row_song_category, parent, false);
		return new CategoryHolder(view);
	}

	@Override
	public void onBindViewHolder(Context context, CategoryHolder viewHolder, final Category category, int position) {
		viewHolder.tvCategoryTitle.setText(category.getTitle());
		viewHolder.ivCategoryImage.setImageResource(category.getImageResourceId());
		if (currentCategoryId == category.getId()) {
			viewHolder.ivCategorySelected.setVisibility(View.VISIBLE);
		} else {
			viewHolder.ivCategorySelected.setVisibility(View.INVISIBLE);
		}
	}
	
	public static final class CategoryHolder extends ViewHolder {
		
		public ImageView ivCategoryImage;
		public ImageView ivCategorySelected; 
		public TextView tvCategoryTitle;
		
		public CategoryHolder(View view) {
			super(view);
			
			ivCategoryImage = (ImageView) view.findViewById(R.id.iv_category_image);
			ivCategorySelected = (ImageView) view.findViewById(R.id.iv_category_selected);
			tvCategoryTitle = (TextView) view.findViewById(R.id.tv_category_title);
		}
		
	}

}
