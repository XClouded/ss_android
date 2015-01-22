package com.myandb.singsong.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.myandb.singsong.GoogleStore;
import com.myandb.singsong.PackageHelper;
import com.myandb.singsong.R;
import com.myandb.singsong.Store;
import com.myandb.singsong.activity.LauncherActivity;

public class VersionDialog extends BaseDialog {
	
	private Button btnUpdate;
	private Button btnNoUpdate;
	private Button btnExit;
	private Store store;
	private String packageName;
	private boolean forceUpdate = false;

	@Override
	protected void initialize(Activity activity) {
		getDialog().setCanceledOnTouchOutside(false);
		getDialog().setCancelable(false);
		
		packageName = activity.getPackageName();
		store = new GoogleStore(packageName);
		if (isStoreUnavailable(store)) {
			store = new GoogleStore(packageName);
		}
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_version;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		btnUpdate = (Button) view.findViewById(R.id.btn_update);
		btnNoUpdate = (Button) view.findViewById(R.id.btn_no_update);
		btnExit = (Button) view.findViewById(R.id.btn_exit);
	}

	@Override
	protected void setupViews() {
		btnUpdate.setOnClickListener(toStoreClickListener);
		btnExit.setOnClickListener(exitClickListener);
		if (forceUpdate) {
			btnNoUpdate.setVisibility(View.GONE);
		} else {
			btnNoUpdate.setVisibility(View.VISIBLE);
			btnNoUpdate.setOnClickListener(startClickListner);
		}
	}

	private OnClickListener toStoreClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			store.move(getActivity());
			finishActivity();
		}
	};
	
	private OnClickListener startClickListner = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			dismiss();
			((LauncherActivity) getActivity()).startRootActivity();
		}
	};
	
	private OnClickListener exitClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			finishActivity();
		}
	};
	
	private void finishActivity() {
		dismiss();
		getActivity().finish();
	}
	
	private boolean isStoreUnavailable(Store store) {
		PackageHelper helper = new PackageHelper(getActivity().getPackageManager());
		return helper.isAppInstalled(store.getStorePackageName());
	}
	
	public void setForceUpdate(boolean forceUpdate) {
		this.forceUpdate = forceUpdate;
	}
	
}
