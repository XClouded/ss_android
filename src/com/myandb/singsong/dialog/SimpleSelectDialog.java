package com.myandb.singsong.dialog;

import com.myandb.singsong.R;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public abstract class SimpleSelectDialog extends BaseDialog {
	
	private TextView tvSelectContent;
	private Button btnOk;
	private Button btnCancel;

	@Override
	protected int getResourceId() {
		return R.layout.dialog_simple_select;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		tvSelectContent = (TextView) view.findViewById(R.id.tv_select_content);
		btnOk = (Button) view.findViewById(R.id.btn_ok);
		btnCancel = (Button) view.findViewById(R.id.btn_cancel);
	}

	@Override
	protected void initialize(Activity activity) {
	}

	@Override
	protected void setupViews() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void styleDialog(Dialog dialog) {
		super.styleDialog(dialog);
		dialog.getWindow().getAttributes().dimAmount = 0.8f;
	}
	
}
