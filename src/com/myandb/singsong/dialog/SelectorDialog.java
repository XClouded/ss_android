package com.myandb.singsong.dialog;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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

	@Override
	protected void initialize(Activity activity) {
		getDialog().setCanceledOnTouchOutside(false);
		getDialog().setCancelable(false);
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		btnMale = (Button) view.findViewById(R.id.btn_select_male);
		btnFemale = (Button) view.findViewById(R.id.btn_select_female);
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_selector;
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
	
}