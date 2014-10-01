package com.myandb.singsong.widget;

import com.myandb.singsong.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class RotateProgressDialog extends ProgressDialog {
	
	private ImageView iv;
	private Animation anim;

	public RotateProgressDialog(Context context) {
		super(context);
		
		setIndeterminate(true);
		setCancelable(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.progress_dialog);
		iv = (ImageView)findViewById(R.id.iv_progress);
		anim = AnimationUtils.loadAnimation(getContext(), R.anim.progress_dialog);
	}

	@Override
	public void show() {
		super.show();
		
		iv.startAnimation(anim);
	}

	@Override
	public void dismiss() {
		super.dismiss();
		
		iv.clearAnimation();
	}

}
