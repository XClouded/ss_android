package com.myandb.singsong.dialog;

import com.myandb.singsong.R;
import com.myandb.singsong.adapter.ImageAdapter;
import com.myandb.singsong.fragment.RecordSettingFragment;
import com.myandb.singsong.model.Image;
import com.myandb.singsong.net.UrlBuilder;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

public class ImageSelectDialog extends BaseDialog {
	
	private ImageView ivCancel;
	private ListView listView;
	private RecordSettingFragment fragment;
	private ImageAdapter adapter;

	public ImageSelectDialog(RecordSettingFragment fragment) {
		super(fragment.getActivity(), android.R.style.Theme_Light_NoTitleBar_Fullscreen);
		
		this.fragment = fragment;
		adapter = new ImageAdapter(this);
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
		fragment.setImage(image);
		
		dismiss();
	}

	@Override
	public void show() {
		super.show();
		
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.s("images");
		
//		adapter.resetRequest(urlBuilder);
	}

}
