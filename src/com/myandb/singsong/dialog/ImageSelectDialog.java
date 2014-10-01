package com.myandb.singsong.dialog;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.RecordSettingActivity;
import com.myandb.singsong.adapter.ImageAdapter;
import com.myandb.singsong.model.Image;
import com.myandb.singsong.net.UrlBuilder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

public class ImageSelectDialog extends BaseDiaglog {
	
	private ImageView ivCancel;
	private ListView listView;
	private RecordSettingActivity parent;
	private ImageAdapter adapter;

	public ImageSelectDialog(Context context) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		
		parent = (RecordSettingActivity) context;
		adapter = new ImageAdapter(context, this);
	}

	@Override
	protected void initializeView() {
		setContentView(R.layout.dialog_image_select);
		
		ivCancel = (ImageView) findViewById(R.id.iv_cancel);
		listView = (ListView) findViewById(R.id.lv_full_width);
	}

	@Override
	protected void setupView() {
		ivCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ImageSelectDialog.this.dismiss();
			}
		});
		
		listView.setAdapter(adapter);
	}
	
	public void selectImage(Image image) {
		parent.setImage(image);
		
		dismiss();
	}

	@Override
	public void show() {
		super.show();
		
		UrlBuilder urlBuilder = UrlBuilder.create();
		urlBuilder.l("images");
		
		adapter.resetRequest(urlBuilder);
	}

}
