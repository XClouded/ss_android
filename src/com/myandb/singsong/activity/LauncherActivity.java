package com.myandb.singsong.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.myandb.singsong.App;
import com.myandb.singsong.PackageHelper;
import com.myandb.singsong.R;
import com.myandb.singsong.dialog.VersionDialog;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.net.JSONObjectSuccessListener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;
import android.content.Intent;

public class LauncherActivity extends FragmentActivity {

	private VersionDialog versionDialog;
	private Handler handler;
	private JSONObject latestNotice;
	
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
		}, 350);
	}
	
	private void requestAppMetadata() {
		JSONObjectRequest request = new JSONObjectRequest(
				"android", null, null,
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
			latestNotice = response.getJSONObject("latest_notice");
			
			PackageHelper packageHelper = new PackageHelper(getPackageManager());
			int versionCode = packageHelper.getVersionCode(getPackageName());
			if (latestVersion > versionCode) {
				versionDialog = new VersionDialog();
				if (forceUpdateVersion > versionCode) {
					versionDialog.setForceUpdate(true);
				}
				versionDialog.show(getSupportFragmentManager(), "");
			} else {
				startRootActivity();
			}
		} catch (JSONException e) {
			Toast.makeText(this, getString(R.string.t_critical_unknown_error), Toast.LENGTH_LONG).show();
			finish();
		}
	}
	
	public void onGetDataError() {
		Toast.makeText(LauncherActivity.this, getString(R.string.t_critical_poor_network_connection), Toast.LENGTH_LONG).show();
		finish();
	}
	
	public void startRootActivity() {
		final int enterAnim = android.R.anim.fade_in;
		final int exitAnim = android.R.anim.fade_out;
		
		Intent intent = new Intent(this, RootActivity.class);
		intent.putExtra(RootActivity.EXTRA_NOTICE, latestNotice.toString());
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

}
