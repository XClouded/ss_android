package com.myandb.singsong.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myandb.singsong.R;
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
	protected void setupViews() {
		titlePrefix = tvProgressTitle.getText().toString();
	}

	public void setTitlePrefix(String text) {
		titlePrefix = text;
		updateProgressBar(0);
	}
	
	public void setOnCancelButtonClickListener(View.OnClickListener listener) {
		if (btnCancel != null) {
			btnCancel.setOnClickListener(listener);
		}
	}
	
	public void setControlButtonShown(boolean shown) {
		if (btnProgressControl.isShown() == shown) {
			return;
		}
		
		if (shown) {
			btnProgressControl.setVisibility(View.VISIBLE);
		} else {
			btnProgressControl.setVisibility(View.GONE);
		}
	}
	
	public void setControlButtonText(String text) {
		if (btnProgressControl != null) {
			btnProgressControl.setText(text);
		}
	}
	
	public void setOnControlButtonClickListener(View.OnClickListener listener) {
		if (btnProgressControl != null) {
			btnProgressControl.setOnClickListener(listener);
		}
	}
	
	public void enableControlButton(boolean enabled) {
		if (btnProgressControl != null) {
			btnProgressControl.setEnabled(enabled);
			
			if (enabled) {
				ViewHelper.setAlpha(btnProgressControl, 0.5f);
			} else {
				ViewHelper.setAlpha(btnProgressControl, 1f);
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