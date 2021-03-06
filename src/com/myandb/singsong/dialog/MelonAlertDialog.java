package com.myandb.singsong.dialog;

import com.google.gson.Gson;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.UpActivity;
import com.myandb.singsong.fragment.BaseFragment;
import com.myandb.singsong.fragment.WebViewFragment;
import com.myandb.singsong.net.MelonMemberResponse.AlertInfo;
import com.myandb.singsong.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MelonAlertDialog extends BaseDialog {
	
	private AlertInfo alertInfo;
	private TextView tvMessage;
	private Button btnOk;
	private Button btnCancel;
	
	public static void show(FragmentManager fm, AlertInfo info) {
		if (fm != null) {
			Bundle bundle = new Bundle();
			bundle.putString(AlertInfo.class.getName(), Utility.getGsonInstance().toJson(info));
			
			BaseDialog dialog = new MelonAlertDialog();
			dialog.setArguments(bundle);
			dialog.show(fm, "");
		}
	}
	
	@Override
	protected int getResourceId() {
		return R.layout.dialog_melon_alert;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		
		Gson gson = Utility.getGsonInstance();
		alertInfo = gson.fromJson(bundle.getString(AlertInfo.class.getName()), AlertInfo.class);
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		tvMessage = (TextView) view.findViewById(R.id.tv_message);
		btnOk = (Button) view.findViewById(R.id.btn_ok);
		btnCancel = (Button) view.findViewById(R.id.btn_cancel);
	}

	@Override
	protected void initialize(Activity activity) {}

	@Override
	protected void setupViews() {
		tvMessage.setText(alertInfo.MESSAGE);
		
		if ("confirm".equals(alertInfo.POPUPTYPE)) {
			btnCancel.setVisibility(View.VISIBLE);
			btnCancel.setOnClickListener(dismissClickListener);
		} else {
			btnCancel.setVisibility(View.GONE);
		}
		
		if (!"".equals(alertInfo.PAGEURL)) {
			btnOk.setOnClickListener(gotoWebViewClickListener);
		} else {
			btnOk.setOnClickListener(dismissClickListener);
		}
		
		if (!"".equals(alertInfo.OKTITLE)) {
			btnOk.setText(alertInfo.OKTITLE);
		} else {
			btnOk.setText("Ȯ��");
		}
	}
	
	private OnClickListener dismissClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			dismiss();
		}
		
	};
	
	private OnClickListener gotoWebViewClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Bundle bundle = new Bundle();
			bundle.putString(BaseFragment.EXTRA_TITLE, alertInfo.LINKTITLE);
			bundle.putString(WebViewFragment.EXTRA_TYPE, alertInfo.LINKTYPE);
			bundle.putString(WebViewFragment.EXTRA_URL, alertInfo.PAGEURL);
			
			Intent intent = new Intent(getActivity(), UpActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, WebViewFragment.class.getName());
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			
			startActivity(intent);
			dismiss();
		}
	};

}
