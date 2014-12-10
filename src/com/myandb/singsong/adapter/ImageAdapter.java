package com.myandb.singsong.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.dialog.ImageSelectDialog;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Image;

public class ImageAdapter extends HolderAdapter<Image, ImageAdapter.ImageHolder> {
	
	private ImageSelectDialog dialog;

	public ImageAdapter(ImageSelectDialog dialog) {
		super(Image.class);
		this.dialog = dialog;
	}

	@Override
	public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = View.inflate(parent.getContext(), R.layout.row_image, null);
		return new ImageHolder(view);
	}

	@Override
	public void onBindViewHolder(Context context, ImageHolder viewHolder, int position) {
		final Image image = getItem(position);
		
		viewHolder.tvCreatedTime.setText(image.getWorkedCreatedTime(getCurrentDate()));
		ImageHelper.displayPhoto(image.getUrl(), viewHolder.ivImage);
		
		viewHolder.view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.selectImage(image);
			}
		});
	}
	
	public static final class ImageHolder extends ViewHolder {
		
		public ImageView ivImage;
		public TextView tvCreatedTime;
		
		public ImageHolder(View view) {
			super(view);
			
			ivImage = (ImageView) view.findViewById(R.id.iv_image);
			tvCreatedTime = (TextView) view.findViewById(R.id.tv_created_time);
		}
		
	}

}
