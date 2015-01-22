package com.myandb.singsong.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.myandb.singsong.R;
import com.myandb.singsong.fragment.KaraokeFragment;

public class HeadsetDialog extends BaseDialog {
	
	private Button btnNoHeadset;
	
	@Override
	protected int getResourceId() {
		return R.layout.dialog_headset;
	}
	
	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		btnNoHeadset = (Button) view.findViewById(R.id.btn_no_headset);
	}
	
	@Override
	protected void styleDialog(Dialog dialog) {
		super.styleDialog(dialog);
		dialog.getWindow().getAttributes().dimAmount = 0.8f;
	}

	@Override
	protected void initialize(Activity activity) {
		getDialog().setCanceledOnTouchOutside(false);
		getDialog().setCancelable(false);
	}

	@Override
	protected void setupViews() {
		btnNoHeadset.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((KaraokeFragment) getParentFragment()).startRecordingWithoutHeadset();
				dismiss();
			}
		});
	}
	
}