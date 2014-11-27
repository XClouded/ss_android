package com.myandb.singsong.dialog;

import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myandb.singsong.R;

public class LoadingDialog extends BaseDialog {
	
	private ProgressBar pbLoading;
	private TextView tvProgressTitle;
	private Button btnProgressControl;
	private Button btnCancel;
	private View controlWrapper;
	private String titlePrefix;
	private int enabledFontColor;
	private int disabledFontColor;
	
	public LoadingDialog(Fragment fragment) {
		super(fragment.getActivity(), android.R.style.Theme_Light_NoTitleBar_Fullscreen);
	}

	@Override
	protected void initialize() {
		try {
			enabledFontColor = getContext().getResources().getColor(R.color.font_default);
			disabledFontColor = getContext().getResources().getColor(R.color.font_grey);
		} catch (NotFoundException e) {
			enabledFontColor = Color.parseColor("#444444");
			disabledFontColor = Color.parseColor("#a7a9a6");
		}
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_progress;
	}

	@Override
	protected void onViewInflated() {
		pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
		tvProgressTitle = (TextView) findViewById(R.id.tv_progress_title);
		btnProgressControl = (Button) findViewById(R.id.btn_progress_control);
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		controlWrapper = findViewById(R.id.ll_control_wrapper);
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
	
	public void showControlButton(boolean show) {
		if (controlWrapper != null) {
			if (show) {
				controlWrapper.setVisibility(View.VISIBLE);
			} else {
				controlWrapper.setVisibility(View.GONE);
			}
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
				btnProgressControl.setTextColor(enabledFontColor);
			} else {
				btnProgressControl.setTextColor(disabledFontColor);
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

	@Override
	public void onBackPressed() {
		return;
	}
	
}