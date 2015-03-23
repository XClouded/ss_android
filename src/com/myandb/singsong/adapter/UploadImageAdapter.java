package com.myandb.singsong.adapter;

import com.myandb.singsong.R;
import com.myandb.singsong.image.ImageHelper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class UploadImageAdapter extends HolderAdapter<UploadImageAdapter.UploadImage, UploadImageAdapter.ImageHolder> {
	
	private Fragment fragment;
	
	public UploadImageAdapter() {
		super(UploadImage.class);
		addAddImageItem();
	}
	
	private void addAddImageItem() {
		UploadImage add = new UploadImage();
		add.url = "";
		addItem(add);
	}
	
	public UploadImageAdapter(Fragment fragment) {
		this();
		this.fragment = fragment;
	}

	@Override
	public ImageHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.row_upload_image, parent, false);
		return new ImageHolder(view);
	}

	@Override
	public void onBindViewHolder(Context context, ImageHolder viewHolder, final UploadImage item, int position) {
		if (isAddButton(position)) {
			viewHolder.ivRemoveImage.setVisibility(View.GONE);
			viewHolder.ivUploadImage.setImageResource(R.drawable.ic_play);
			viewHolder.view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					UploadImage ui = new UploadImage();
					ui.url = "https://www.google.co.kr/logos/doodles/2015/emmy-noethers-133rd-birthday-5681045017985024-hp.jpg";
					addItem(getCount() - 1, ui);
				}
			});
		} else {
			ImageHelper.displayPhoto(item.url, viewHolder.ivUploadImage);
			viewHolder.ivRemoveImage.setVisibility(View.VISIBLE);
			viewHolder.ivRemoveImage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					removeItem(item);
				}
			});
		}
	}
	
	private boolean isAddButton(int position) {
		return position == getCount() - 1;
	}

	public static final class ImageHolder extends ViewHolder {
		
		public ImageView ivUploadImage;
		public ImageView ivRemoveImage;

		public ImageHolder(View view) {
			super(view);
			
			ivUploadImage = (ImageView) view.findViewById(R.id.iv_upload_image);
			ivRemoveImage = (ImageView) view.findViewById(R.id.iv_remove_image);
		}
		
	}
	
	public static final class UploadImage {
		
		public String url;
		
	}

}
