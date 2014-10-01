package com.myandb.singsong.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.RecordMainActivity;

public class HeadsetDialog extends BaseDiaglog {
	
	private Button btnNoHeadset;
	private RecordMainActivity parent;

	public HeadsetDialog(Context context) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		
		parent = (RecordMainActivity) context;
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
				HeadsetDialog.this.dismiss();
				
				parent.startRecording(false);
			}
		});
	}
	
}