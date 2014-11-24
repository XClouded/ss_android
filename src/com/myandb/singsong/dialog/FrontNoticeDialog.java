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

	public FrontNoticeDialog(Context context, Notice notice) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		
		this.notice = notice;
	}

	@Override
	protected void initializeView() {
		setContentView(R.layout.dialog_front_notice);
		
		btnClose = findViewById(R.id.btn_close);
		ivFrontImage = (ImageView) findViewById(R.id.iv_front_image);
	}

	@Override
	protected void setupView() {
		ImageHelper.displayPhoto(notice.getFrontImageUrl(), ivFrontImage);
		btnClose.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				FileHelper storage = new FileHelper();
//				storage.readNotice();
				
				FrontNoticeDialog.this.dismiss();
			}
		});
	}

}
