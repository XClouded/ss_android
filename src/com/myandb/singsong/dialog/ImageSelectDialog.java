package com.myandb.singsong.dialog;

import com.myandb.singsong.R;
import com.myandb.singsong.adapter.ImageAdapter;
import com.myandb.singsong.fragment.RecordSettingFragment;
import com.myandb.singsong.model.Image;
import com.myandb.singsong.net.UrlBuilder;

import android.widget.ListView;

public class ImageSelectDialog extends BaseDialog {
	
	private ListView listView;
	private RecordSettingFragment fragment;
	private ImageAdapter adapter;

	public ImageSelectDialog(RecordSettingFragment fragment) {
		super(fragment.getActivity(), android.R.style.Theme_Light_NoTitleBar_Fullscreen);
		this.fragment = fragment;
	}

	@Override
	protected void initialize() {
		adapter = new ImageAdapter(this);
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_image_select;
	}

	@Override
	protected void onViewInflated() {
		listView = (ListView) findViewById(R.id.lv_full_width);
	}

	@Override
	protected void setupViews() {
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
