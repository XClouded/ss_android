package com.myandb.singsong.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.myandb.singsong.R;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Notice;

public class FrontNoticeDialog extends BaseDialog {
	
	private ImageView ivFrontImage;
	private View btnClose;
	private Notice notice;

	@Override
	protected void initialize(Activity activity) {
		getDialog().setCanceledOnTouchOutside(false);
		getDialog().setCancelable(false);
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		btnClose = view.findViewById(R.id.btn_close);
		ivFrontImage = (ImageView) view.findViewById(R.id.iv_front_image);
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_front_notice;
	}

	@Override
	protected void setupViews() {
		ImageHelper.displayPhoto(notice.getFrontImageUrl(), ivFrontImage);
		btnClose.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				FileHelper storage = new FileHelper();
//				storage.readNotice();
				dismiss();
			}
		});
	}

}
