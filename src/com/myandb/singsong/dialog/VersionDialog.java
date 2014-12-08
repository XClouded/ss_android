package com.myandb.singsong.dialog;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.myandb.singsong.GoogleStore;
import com.myandb.singsong.PackageHelper;
import com.myandb.singsong.R;
import com.myandb.singsong.Store;

public class VersionDialog extends BaseDialog {
	
	private Button btnUpdate;
	private Button btnExit;
	private Store store;
	private boolean forceUpdate = false;

	@Override
	protected void initialize(Activity activity) {
		store = new GoogleStore();
		if (isStoreUnavailable(store)) {
			store = new GoogleStore();
		}
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_version;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		btnUpdate = (Button) view.findViewById(R.id.btn_update);
		btnExit = (Button) view.findViewById(R.id.btn_exit);
	}

	@Override
	protected void setupViews() {
		btnUpdate.setOnClickListener(toStoreClickListener);
		btnExit.setOnClickListener(exitClickListener);
	}
	
	@Override
	public void show(FragmentManager manager, String tag) {
		super.show(manager, tag);
		if (forceUpdate) {
			// Hide 'skip update'
		}
	}

	private OnClickListener toStoreClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			moveToStore();
			finishActivity();
		}
	};
	
	private OnClickListener exitClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			finishActivity();
		}
	};
	
	private void moveToStore() {
		String packageName = getActivity().getPackageName();
		Uri uri = store.getDetailViewUri(packageName);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(uri);
		getActivity().startActivity(intent);
	}
	
	private void finishActivity() {
		dismiss();
		getActivity().finish();
	}
	
	private boolean isStoreUnavailable(Store store) {
		PackageHelper helper = new PackageHelper(getActivity().getPackageManager());
		return helper.isAppInstalled(store.getPackageName());
	}
	
	public void setForceUpdate(boolean forceUpdate) {
		this.forceUpdate = forceUpdate;
	}
	
}
