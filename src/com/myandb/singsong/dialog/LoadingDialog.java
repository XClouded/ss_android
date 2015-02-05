package com.myandb.singsong.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.fragment.KaraokeFragment;
import com.nineoldandroids.view.ViewHelper;

public class LoadingDialog extends BaseDialog {
	
	private ProgressBar pbLoading;
	private TextView tvProgressTitle;
	private Button btnProgressControl;
	private Button btnCancel;
	private String titlePrefix;

	@Override
	protected void initialize(Activity activity) {
		getDialog().setCanceledOnTouchOutside(false);
		getDialog().setCancelable(false);
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		pbLoading = (ProgressBar) view.findViewById(R.id.pb_loading);
		tvProgressTitle = (TextView) view.findViewById(R.id.tv_progress_title);
		btnProgressControl = (Button) view.findViewById(R.id.btn_progress_control);
		btnCancel = (Button) view.findViewById(R.id.btn_cancel);
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_loading;
	}
	
	@Override
	protected void styleDialog(Dialog dialog) {
		super.styleDialog(dialog);
		dialog.getWindow().getAttributes().dimAmount = 0.8f;
	}

	@Override
	protected void setupViews() {
		titlePrefix = tvProgressTitle.getText().toString();
		btnProgressControl.setOnClickListener(controlButtonClickListener);
		enableControlButton(false);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
	}
	
	private OnClickListener controlButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (getParentFragment() == null) {
				return;
			}
			
			if (getParentFragment() instanceof KaraokeFragment) {
				dismiss();
				((KaraokeFragment) getParentFragment()).prepareRecording();
			}
		}
	};
	
	public void enableControlButton(boolean enabled) {
		if (btnProgressControl != null) {
			btnProgressControl.setEnabled(enabled);
			
			if (enabled) {
				ViewHelper.setAlpha(btnProgressControl, 1f);
			} else {
				ViewHelper.setAlpha(btnProgressControl, 0.5f);
			}
		}
	}
	
	public void updateProgressBar(int progress) {
		if (pbLoading != null) {
			pbLoading.setProgress(progress);
		}
		
		if (tvProgressTitle != null) {
			tvProgressTitle.setText(titlePrefix);
			tvProgressTitle.append(String.valueOf(progress));
			tvProgressTitle.append("%");
		}
	}
	
}