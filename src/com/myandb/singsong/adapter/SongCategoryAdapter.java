package com.myandb.singsong.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.fragment.BaseFragment;
import com.myandb.singsong.fragment.ListenHomeFragment;
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
	public void onBindViewHolder(Context context, CategoryHolder viewHolder, final Category category, int position) {
		viewHolder.tvCategoryText.setText(category.getTitle());
		viewHolder.ivCategoryImage.setImageResource(category.getImageResourceId());
		viewHolder.view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, category.getTitle());
				bundle.putInt(ListenHomeFragment.EXTRA_CATEGORY_ID, category.getId());
				Intent intent = new Intent(v.getContext(), RootActivity.class);
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, ListenHomeFragment.class.getName());
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
				((BaseActivity) v.getContext()).changePage(intent);
			}
		});
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
