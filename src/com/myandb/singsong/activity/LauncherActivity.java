package com.myandb.singsong.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.myandb.singsong.App;
import com.myandb.singsong.GoogleStore;
import com.myandb.singsong.PackageHelper;
import com.myandb.singsong.R;
import com.myandb.singsong.Store;
import com.myandb.singsong.dialog.BaseDiaglog;
import com.myandb.singsong.event.OnVolleyWeakError;
import com.myandb.singsong.event.OnVolleyWeakResponse;
import com.myandb.singsong.fragment.CollaboratedFragment;
import com.myandb.singsong.net.UrlBuilder;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class LauncherActivity extends Activity {

	private VersionDialog versionDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_logo);
		
		requestAppMetadata();
	}
	
	private void requestAppMetadata() {
		UrlBuilder urlBuilder = new UrlBuilder();
		String url = urlBuilder.s("android").toString();
		
		JsonObjectRequest request = new JsonObjectRequest(
				Method.GET, url, null,
				new OnVolleyWeakResponse<LauncherActivity, JSONObject>(this, "onGetDataSuccess"),
				new OnVolleyWeakError<LauncherActivity>(this, "onGetDataError")
		);
		
		RequestQueue queue = ((App) getApplicationContext()).getQueueInstance();
		queue.add(request);
	}
	
	public void onGetDataSuccess(JSONObject response) {
		try {
			int latestVersion = response.getInt("latest_version");
			int forceUpdateVersion = response.getInt("force_update_version");
			int latestNoticeId = response.getInt("latest_notice_id");
			
			PackageHelper packageHelper = new PackageHelper(getPackageManager());
			int versionCode = packageHelper.getVersionCode(getPackageName());
			if (latestVersion > versionCode) {
				versionDialog = new VersionDialog(this);
				if (forceUpdateVersion > versionCode) {
					versionDialog.setForceUpdate(true);
				}
				versionDialog.show();
			} else {
				startRootActivity(latestNoticeId);
			}
		} catch (JSONException e) {
			Toast.makeText(this, getString(R.string.t_unknown_error), Toast.LENGTH_LONG).show();
			finish();
		}
	}
	
	public void onGetDataError() {
		Toast.makeText(this, getString(R.string.t_poor_network_connection), Toast.LENGTH_LONG).show();
		finish();
	}
	
	private void startRootActivity(int noticeId) {
		final int enterAnim = android.R.anim.fade_in;
		final int exitAnim = android.R.anim.fade_out;
		
		Intent intent = new Intent(this, RootActivity.class);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, CollaboratedFragment.class.getName());
		startActivity(intent);
		overridePendingTransition(enterAnim, exitAnim);
		finish();
	}

	private static class VersionDialog extends BaseDiaglog {
		
		private Button btnUpdate;
		private Button btnExit;
		private Store store;
		private boolean forceUpdate = false;

		public VersionDialog(Context context) {
			super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
			
			store = new GoogleStore();
			if (isStoreUnavailable(store)) {
				store = new GoogleStore();
			}
		}

		@Override
		protected void initializeView() {
			setContentView(R.layout.dialog_update);
			
			btnUpdate = (Button)findViewById(R.id.btn_update);
			btnExit = (Button)findViewById(R.id.btn_exit);
		}

		@Override
		protected void setupView() {
			btnUpdate.setOnClickListener(toStoreClickListener);
			btnExit.setOnClickListener(exitClickListener);
		}
		
		@Override
		public void show() {
			super.show();
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
			Activity activity = ((Activity) getContext());
			String packageName = activity.getPackageName();
			Uri uri = store.getDetailViewUri(packageName);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(uri);
			activity.startActivity(intent);
		}
		
		private void finishActivity() {
			dismiss();
			((Activity) getContext()).finish();
		}
		
		private boolean isStoreUnavailable(Store store) {
			PackageHelper helper = new PackageHelper(getContext().getPackageManager());
			return helper.isAppInstalled(store.getPackageName());
		}
		
		public void setForceUpdate(boolean forceUpdate) {
			this.forceUpdate = forceUpdate;
		}
		
	}

}
