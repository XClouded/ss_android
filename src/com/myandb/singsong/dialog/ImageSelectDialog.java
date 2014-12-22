package com.myandb.singsong.dialog;

import org.json.JSONArray;

import com.myandb.singsong.R;
import com.myandb.singsong.adapter.HolderAdapter;
import com.myandb.singsong.adapter.ImageAdapter;
import com.myandb.singsong.fragment.RecordSettingFragment;
import com.myandb.singsong.model.Image;
import com.myandb.singsong.net.GradualLoader;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.net.GradualLoader.OnLoadCompleteListener;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

public class ImageSelectDialog extends BaseDialog {
	
	private ListView listView;
	private ImageAdapter adapter;
	private GradualLoader loader;
	
	@Override
	protected int getResourceId() {
		return R.layout.dialog_image_select;
	}
	
	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		listView = (ListView) view.findViewById(R.id.lv_full_width);
	}

	@Override
	protected void initialize(Activity activity) {
		adapter = new ImageAdapter(this);
		
		loader = new GradualLoader(activity);
		loader.setUrlBuilder(new UrlBuilder().s("images"));
	}

	@Override
	protected void setupViews() {
		listView.setAdapter(adapter);
		listView.setOnScrollListener(loader);
		loader.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			
			@Override
			public void onComplete(JSONArray response) {
				if (adapter instanceof HolderAdapter) {
					((HolderAdapter<?, ?>) adapter).addAll(response);
				}
			}
		});
	}
	
	public void selectImage(Image image) {
		((RecordSettingFragment) getParentFragment()).setImage(image);
		dismiss();
	}

}
