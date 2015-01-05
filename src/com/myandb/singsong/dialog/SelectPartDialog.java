package com.myandb.singsong.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.myandb.singsong.R;
import com.myandb.singsong.fragment.KaraokeFragment;
import com.myandb.singsong.model.Music;

public class SelectPartDialog extends BaseDialog {
	
	public static final String EXTRA_PART_MALE = "part_male";
	public static final String EXTRA_PART_FEMALE = "part_female";
	
	private Button btnMale;
	private Button btnFemale;
	private String maleName;
	private String femaleName;
	
	@Override
	protected int getResourceId() {
		return R.layout.dialog_select_part;
	}
	
	@Override
	protected void styleDialog(Dialog dialog) {
		super.styleDialog(dialog);
		dialog.getWindow().getAttributes().dimAmount = 0.8f;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		maleName = bundle.getString(EXTRA_PART_MALE);
		femaleName = bundle.getString(EXTRA_PART_FEMALE);
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		btnMale = (Button) view.findViewById(R.id.btn_select_male);
		btnFemale = (Button) view.findViewById(R.id.btn_select_female);
	}
	
	@Override
	protected void initialize(Activity activity) {
		getDialog().setCanceledOnTouchOutside(false);
		getDialog().setCancelable(false);
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
			Fragment fragment = getParentFragment();
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