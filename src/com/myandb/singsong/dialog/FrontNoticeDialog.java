package com.myandb.singsong.dialog;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.myandb.singsong.R;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Notice;

public class FrontNoticeDialog extends BaseDialog {
	
	private ImageView ivFrontImage;
	private View btnClose;
	private Notice notice;

	public FrontNoticeDialog(Context context) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
	}

	@Override
	protected void initialize() {
		// Nothing to run
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_front_notice;
	}

	@Override
	protected void onViewInflated() {
		btnClose = findViewById(R.id.btn_close);
		ivFrontImage = (ImageView) findViewById(R.id.iv_front_image);
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
	
	public void setNotice(Notice notice) {
		this.notice = notice;
	}

	@Override
	public void onBackPressed() {
		return;
	}

}
