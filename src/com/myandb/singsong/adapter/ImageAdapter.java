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

public class ImageAdapter extends AutoLoadAdapter<Image> {
	
	private ImageSelectDialog dialog;

	public ImageAdapter(Context context, ImageSelectDialog dialog) {
		super(context, Image.class, true);
		
		this.dialog = dialog;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final ImageHolder imageHolder;
		final Image image = (Image) getItem(position);
		
		if (view == null) {
			view = View.inflate(getContext(), R.layout.row_image, null);
			
			imageHolder = new ImageHolder();
			imageHolder.ivImage = (ImageView) view.findViewById(R.id.iv_image);
			imageHolder.tvCreatedTime = (TextView) view.findViewById(R.id.tv_created_time);
			
			view.setTag(imageHolder);
		} else {
			imageHolder = (ImageHolder) view.getTag();
		}
		
		imageHolder.tvCreatedTime.setText(image.getWorkedCreatedTime(getCurrentDate()));
		ImageHelper.displayPhoto(image.getUrl(), imageHolder.ivImage);
		
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.selectImage(image);
			}
		});
		
		return view;
	}
	
	private static class ImageHolder {
		
		public ImageView ivImage;
		public TextView tvCreatedTime;
		
	}

}
