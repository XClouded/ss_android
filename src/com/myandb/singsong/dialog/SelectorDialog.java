package com.myandb.singsong.dialog;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;

import com.myandb.singsong.R;
import com.myandb.singsong.fragment.KaraokeFragment;
import com.myandb.singsong.model.Music;

public class SelectorDialog extends BaseDialog {
	
	private Button btnMale;
	private Button btnFemale;
	private String maleName;
	private String femaleName;
	private Fragment fragment;

	public SelectorDialog(Fragment fragment, String maleName, String femaleName) {
		super(fragment.getActivity(), android.R.style.Theme_NoTitleBar_Fullscreen);
		
		this.fragment = fragment;
		this.maleName = maleName;
		this.femaleName = femaleName;
	}

	@Override
	protected void initialize() {
		// Nothing to run
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_selector;
	}

	@Override
	protected void onViewInflated() {
		btnMale = (Button) findViewById(R.id.btn_select_male);
		btnFemale = (Button) findViewById(R.id.btn_select_female);
	}

	@Override
	protected void setupViews() {
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
			dismiss();
			
			if (fragment instanceof KaraokeFragment) {
				KaraokeFragment karaoke = (KaraokeFragment) fragment;
				
				if (view.getId() == R.id.btn_select_male) {
					karaoke.setThisUserPart(Music.PART_MALE);
				} else {
					karaoke.setThisUserPart(Music.PART_FEMALE);
				}
				
				karaoke.prepareRecording();
			}
		}
	};

	@Override
	public void onBackPressed() {
		return;
	}
	
}