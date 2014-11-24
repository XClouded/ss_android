package com.myandb.singsong.dialog;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;

import com.myandb.singsong.R;
import com.myandb.singsong.fragment.KaraokeFragment;

public class HeadsetDialog extends BaseDialog {
	
	private Button btnNoHeadset;
	private Fragment fragment;

	public HeadsetDialog(Fragment fragment) {
		super(fragment.getActivity(), android.R.style.Theme_Light_NoTitleBar_Fullscreen);
		
		this.fragment = fragment;
	}

	@Override
	protected void initializeView() {
		setContentView(R.layout.dialog_headset);
		
		btnNoHeadset = (Button) findViewById(R.id.btn_no_headset);
	}

	@Override
	protected void setupView() {
		btnNoHeadset.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (fragment instanceof KaraokeFragment) {
					HeadsetDialog.this.dismiss();
					((KaraokeFragment) fragment).startRecordingWithoutHeadset();
				}
			}
		});
	}
	
}