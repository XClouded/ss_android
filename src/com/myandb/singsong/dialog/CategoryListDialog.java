package com.myandb.singsong.dialog;

import java.util.ArrayList;

import com.myandb.singsong.R;
import com.myandb.singsong.adapter.SongCategoryAdapter;
import com.myandb.singsong.fragment.ListenHomeFragment;
import com.myandb.singsong.model.Category;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class CategoryListDialog extends BaseDialog {
	
	public static final String EXTRA_CURRENT_CATEGORY_ID = "current_category_id";
	
	private ListView listView;
	private int currentCategoryId;

	@Override
	protected int getResourceId() {
		return R.layout.dialog_category_list;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		currentCategoryId = bundle.getInt(EXTRA_CURRENT_CATEGORY_ID);
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		listView = (ListView) view.findViewById(R.id.lv_full_width);
	}

	@Override
	protected void initialize(Activity activity) {}

	@Override
	protected void setupViews() {
		ArrayList<Category> cgs = new ArrayList<Category>();
		for (int i = 0; i < 10; i++) {
			cgs.add(new Category(i));
		}
		
		SongCategoryAdapter adapter = new SongCategoryAdapter(currentCategoryId);
		adapter.addAll(cgs);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(categoryClickListener);
	}
	
	private OnItemClickListener categoryClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Category category = (Category) parent.getItemAtPosition(position);
			if (getParentFragment() instanceof ListenHomeFragment) {
				((ListenHomeFragment) getParentFragment()).changeCategory(category);
				dismiss();
			}
		}
	};
	
	@Override
	protected void styleDialog(Dialog dialog) {
		super.styleDialog(dialog);
		DisplayMetrics metrics = new DisplayMetrics();
		dialog.getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		dialog.getWindow().getAttributes().height = getHeight(metrics);
		dialog.getWindow().setGravity(Gravity.BOTTOM);
	}
	
	private int getHeight(DisplayMetrics metrics) {
		int maxHeight = getResources().getDimensionPixelSize(R.dimen.dialog_bottom_max_height);
		return Math.min((int) (metrics.heightPixels * 0.8), maxHeight);
	}

	@Override
	protected float getWidthPercentage() {
		return 1f;
	}

}
