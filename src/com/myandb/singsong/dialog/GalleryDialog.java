package com.myandb.singsong.dialog;

import com.myandb.singsong.R;
import com.myandb.singsong.image.ImageHelper;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class GalleryDialog extends BaseDialog {
	
	public static final String EXTRA_PHOTO_URL = "photo_url";
	
	private ImageView ivPhoto;
	private String url;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.getWindow().setWindowAnimations(R.anim.hold);
		return dialog;
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_gallery;
	}

	@Override
	protected float getWidthPercentage() {
		return 1.0f;
	}
	
	@Override
	protected void styleDialog(Dialog dialog) {
		super.styleDialog(dialog);
		dialog.getWindow().getAttributes().dimAmount = 0.8f;
	}
	
	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		url = bundle.getString(EXTRA_PHOTO_URL);
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		ivPhoto = (ImageView) view.findViewById(R.id.iv_photo);
	}

	@Override
	protected void initialize(Activity activity) {
		if (url == null) {
			dismiss();
		}
	}

	@Override
	protected void setupViews() {
		ImageHelper.displayPhoto(url, ivPhoto);
	}

}
