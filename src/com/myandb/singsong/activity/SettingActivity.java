package com.myandb.singsong.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.google.android.gcm.GCMRegistrar;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.dialog.WithdrawDialog;
import com.myandb.singsong.event.OnVolleyWeakError;
import com.myandb.singsong.event.OnVolleyWeakResponse;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JustRequest;
import com.myandb.singsong.secure.Authenticator;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingActivity extends Activity implements OnClickListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		final FileHelper storage = new FileHelper();
		
		Button btnLogout = (Button) findViewById(R.id.btn_logout);
		Button btnVersion = (Button) findViewById(R.id.btn_version);
		Button btnTutorial = (Button) findViewById(R.id.btn_to_tutorial);
		Button btnRequest = (Button) findViewById(R.id.btn_request);
		Button btnFacebook = (Button) findViewById(R.id.btn_to_facebook);
		Button btnWithdraw = (Button) findViewById(R.id.btn_withdraw);
		CheckBox cbAllowPush = (CheckBox)findViewById(R.id.cb_allow_push);
		
		btnLogout.setOnClickListener(this);
		btnTutorial.setOnClickListener(this);
		btnRequest.setOnClickListener(this);
		btnFacebook.setOnClickListener(this);
		btnWithdraw.setOnClickListener(this);
		cbAllowPush.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				storage.setAllowPush(isChecked);
			}
		});
		
		try {
			String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;  
			btnVersion.setText("Version " + versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
//		if (storage.isAllowPush()) {
		if (true) {
			cbAllowPush.setChecked(true);
		} else {
			cbAllowPush.setChecked(false);
		}
	}
	
	private void unregisterGCM() {
		try {
			JSONObject message = new JSONObject();
			message.put("push_id", "");
			
			JustRequest request = new JustRequest(Method.PUT, "users", message);
			RequestQueue queue = ((App) getApplicationContext()).getQueueInstance();
			queue.add(request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void deleteTokenOnServer() {
		JSONObjectRequest request = new JSONObjectRequest(
				Method.DELETE, "token", null,
				new OnDeleteTokenListener(this),
				new OnDeleteTokenErrorListener(this));
		
		RequestQueue queue = ((App) getApplicationContext()).getQueueInstance();
		queue.add(request);
	}
	
	private static class OnDeleteTokenListener extends OnVolleyWeakResponse<SettingActivity, JSONObject> {

		public OnDeleteTokenListener(SettingActivity reference) {
			super(reference);
		}

		@Override
		public void onFilteredResponse(SettingActivity reference, JSONObject response) {
			reference.logout();
		}
		
	}
	
	private static class OnDeleteTokenErrorListener extends OnVolleyWeakError<SettingActivity> {

		public OnDeleteTokenErrorListener(SettingActivity reference) {
			super(reference);
		}

		@Override
		public void onFilteredResponse(SettingActivity reference, VolleyError error) {
			reference.logout();
		}
		
	}
	
	public void logout() {
		Authenticator auth = new Authenticator();
		auth.logout();
		
		finish();
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		String url = null;
		
		switch (v.getId()) {
		case R.id.btn_logout:
			if (GCMRegistrar.isRegistered(SettingActivity.this)) {
				GCMRegistrar.unregister(SettingActivity.this);
				unregisterGCM();
			}
			
			/*
			if (getService() != null) {
				getService().stopPlaying(false);
			}
			*/
			
//			File photo = FileManager.get(FileManager.USER_PHOTO);
//			if (photo.exists()) {
//				photo.delete();
//			}
			
			deleteTokenOnServer();
			
			break;
			
		case R.id.btn_to_tutorial:
//			intent = new Intent(this, GuideActivity.class);
			startActivity(intent); 
			
			break;
			
		case R.id.btn_request:
			url = "https://m.facebook.com/photo.php?fbid=566661093415735&id=503254239756421&set=a.550264828388695.1073741830.503254239756421";
			
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			startActivity(intent);
			
			break;
			
		case R.id.btn_to_facebook:
			url = "http://www.facebook.com/collabokaraoke";
			
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			startActivity(intent);
			
			break;
			
		case R.id.btn_withdraw:
			DialogFragment dialog = new WithdrawDialog();
//			dialog.show(null, "");
			
			break;

		}
	}
}
