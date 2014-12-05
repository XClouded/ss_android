package com.myandb.singsong.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.myandb.singsong.App;
import com.myandb.singsong.GoogleStore;
import com.myandb.singsong.PackageHelper;
import com.myandb.singsong.R;
import com.myandb.singsong.Store;
import com.myandb.singsong.dialog.BaseDialog;
import com.myandb.singsong.fragment.BaseFragment;
import com.myandb.singsong.fragment.HomeFragment;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.net.JSONObjectSuccessListener;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

public class LauncherActivity extends FragmentActivity {

	private VersionDialog versionDialog;
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_launcher);
		
		handler = new Handler(Looper.getMainLooper());
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				requestAppMetadata();
			}
		}, 500);
	}
	
	private void requestAppMetadata() {
		JSONObjectRequest request = new JSONObjectRequest(
				"android", null,
				new JSONObjectSuccessListener(this, "onGetDataSuccess"),
				new JSONErrorListener(this, "onGetDataError")
		);
		request.setRequireAccessToken(false);
		((App) getApplicationContext()).addShortLivedRequest(this, request);
	}
	
	public void onGetDataSuccess(JSONObject response) {
		try {
			int latestVersion = response.getInt("latest_version");
			int forceUpdateVersion = response.getInt("force_update_version");
			int latestNoticeId = 0;/*response.getInt("latest_notice_id");*/
			
			PackageHelper packageHelper = new PackageHelper(getPackageManager());
			int versionCode = packageHelper.getVersionCode(getPackageName());
			if (latestVersion > versionCode) {
				versionDialog = new VersionDialog();
				if (forceUpdateVersion > versionCode) {
					versionDialog.setForceUpdate(true);
				}
				versionDialog.show(getSupportFragmentManager(), "");
			} else {
				startRootActivity(latestNoticeId);
			}
		} catch (JSONException e) {
			Toast.makeText(this, getString(R.string.t_unknown_error), Toast.LENGTH_LONG).show();
			finish();
		}
	}
	
	public void onGetDataError() {
		Toast.makeText(LauncherActivity.this, getString(R.string.t_poor_network_connection), Toast.LENGTH_LONG).show();
		finish();
	}
	
	private void startRootActivity(int noticeId) {
		final int enterAnim = android.R.anim.fade_in;
		final int exitAnim = android.R.anim.fade_out;
		
		Bundle bundle = new Bundle();
		bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, getString(R.string.fragment_home_title));
		Intent intent = new Intent(this, RootActivity.class);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, HomeFragment.class.getName());
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
		intent.putExtra(RootActivity.EXTRA_NOTICE_ID, noticeId);
		startActivity(intent);
		overridePendingTransition(enterAnim, exitAnim);
		finish();
	}

	@Override
	protected void onDestroy() {
		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
			handler = null;
		}
		((App) getApplicationContext()).cancelRequests(this);
		super.onDestroy();
	}

	private static class VersionDialog extends BaseDialog {
		
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
			return R.layout.dialog_update;
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

		private View.OnClickListener toStoreClickListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				moveToStore();
				finishActivity();
			}
		};
		
		private View.OnClickListener exitClickListener = new View.OnClickListener() {
			
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

}
