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
import com.myandb.singsong.file.Storage;
import com.myandb.singsong.fragment.CollaboratedFragment;
import com.myandb.singsong.model.Notice;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Auth;
import com.myandb.singsong.util.Utility;

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
//		queue.add(request); 
		
		testStartRootActivity();
	}
	
	private void testStartRootActivity() {
		Intent intent = new Intent(this, RootActivity.class);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, CollaboratedFragment.class.getName());
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		finish();
	}
	
	public void onGetDataSuccess(JSONObject response) {
		try {
			int latestVersion = response.getInt("latest_version");
			int forceUpdateVersion = response.getInt("force_update_version");
			boolean isMusicUpdated = response.getBoolean("is_music_updated");
			Notice notice = Utility.getGsonInstance()
					.fromJson(response.getJSONObject("notice").toString(), Notice.class);
			
			PackageHelper packageHelper = new PackageHelper(getPackageManager());
			int versionCode = packageHelper.getVersionCode(getPackageName());
			if (latestVersion > versionCode) {
				// show there is new update version
			}
			
			if (forceUpdateVersion > versionCode) {
				versionDialog = new VersionDialog(this);
				versionDialog.show();
			} else {
				Storage storage = new Storage();
				storage.arriveNotice(notice);
				
				if (Auth.isLoggedIn()) {
					if (isMusicUpdated && !storage.hasCheckedUpdate()) {
						transitionToNextActivity(MusicUpdateActivity.class);
					} else {
						transitionToNextActivity(MainActivity.class);
					}
				} else {
					transitionToNextActivity(GuideActivity.class);
				}
			}
		} catch (JSONException e) {
			showErrorMessage();
		}
	}
	
	public void onGetDataError() {
		showErrorMessage();
	}
	
	private void transitionToNextActivity(Class<?> activityClass) {
		if (activityClass != null) {
			Intent intent = new Intent(this, activityClass);
			
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			finish();
		} else {
			showErrorMessage();
		}
	}
	
	public void showErrorMessage() {
		Toast.makeText(this, "네트워크 상태가 좋지 않습니다. 잠시 후에 다시 이용해주세요 :)", Toast.LENGTH_LONG).show();
		finish();
	}

	private static class VersionDialog extends BaseDiaglog {
		
		private Button btnUpdate;
		private Button btnExit;
		private Store store = new GoogleStore();

		public VersionDialog(Context context) {
			super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
			
			store = isStoreInstalled() ? store : new GoogleStore();
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
		
		private boolean isStoreInstalled() {
			PackageHelper helper = new PackageHelper(getContext().getPackageManager());
			return helper.isAppInstalled(store.getPackageName());
		}
		
	}

}
