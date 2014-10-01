package com.myandb.singsong.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.RecordMainActivity;
import com.myandb.singsong.model.Music;

public class SelectorDialog extends BaseDiaglog {
	
	private Button btnMale;
	private Button btnFemale;
	private String maleName;
	private String femaleName;
	private RecordMainActivity parent;

	public SelectorDialog(Context context, String maleName, String femaleName) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		
		parent = (RecordMainActivity) context;
		this.maleName = maleName;
		this.femaleName = femaleName;
	}

	@Override
	protected void initializeView() {
		setContentView(R.layout.dialog_selector);
		
		btnMale = (Button) findViewById(R.id.btn_select_male);
		btnFemale = (Button) findViewById(R.id.btn_select_female);
	}

	@Override
	protected void setupView() {
		if (maleName != null) {
			btnMale.setText(maleName);
		}
		if (femaleName != null) {
			btnFemale.setText(femaleName);
		}
		
		btnMale.setOnClickListener(partSelectClickListener);
		btnFemale.setOnClickListener(partSelectClickListener);
	}
	
	private View.OnClickListener partSelectClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View view) {
			SelectorDialog.this.dismiss();
			
			if (view.getId() == R.id.btn_select_male) {
				parent.setThisUserPart(Music.PART_MALE);
			} else {
				parent.setThisUserPart(Music.PART_FEMALE);
			}
			
			parent.prepareRecording();
		}
	};
	
}