package com.myandb.singsong.dialog;

import com.myandb.singsong.R;
import com.myandb.singsong.adapter.ImageAdapter;
import com.myandb.singsong.fragment.RecordSettingFragment;
import com.myandb.singsong.model.Image;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

public class ImageSelectDialog extends BaseDialog {
	
	private ListView listView;
	private RecordSettingFragment fragment;
	private ImageAdapter adapter;

	@Override
	protected void initialize(Activity activity) {
		adapter = new ImageAdapter(this);
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		listView = (ListView) view.findViewById(R.id.lv_full_width);
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_image_select;
	}

	@Override
	protected void setupViews() {
		listView.setAdapter(adapter);
	}
	
	public void selectImage(Image image) {
		fragment.setImage(image);
		dismiss();
	}

}
