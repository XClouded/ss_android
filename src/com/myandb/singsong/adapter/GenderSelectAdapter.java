package com.myandb.singsong.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.model.Gender;

public class GenderSelectAdapter extends HolderAdapter<Gender, GenderSelectAdapter.GenderHolder> {
	
	public GenderSelectAdapter() {
		super(Gender.class);
	}
	
	@Override
	public GenderHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.row_gender, parent, false);
		return new GenderHolder(view);
	}
	
	@Override
	public void onBindViewHolder(Context context, GenderHolder viewHolder, Gender item, int position) {
		viewHolder.tvTitle.setText(item.getTitle());
		viewHolder.tvDescription.setText(item.getDescription());
	}

	public static final class GenderHolder extends ViewHolder {
		
		public TextView tvTitle;
		public TextView tvDescription;
		public ImageView ivSelected;
		
		public GenderHolder(View view) {
			super(view);
			
			tvTitle = (TextView) view.findViewById(R.id.tv_title);
			tvDescription = (TextView) view.findViewById(R.id.tv_description);
		}
		
	}

}
