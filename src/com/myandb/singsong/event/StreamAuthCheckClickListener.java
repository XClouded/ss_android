package com.myandb.singsong.event;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.myandb.singsong.App;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.MelonHttpHeaderScheme;
import com.myandb.singsong.net.MelonResponseHooker;
import com.myandb.singsong.net.MelonResponseHooker.MelonResponseException;
import com.myandb.singsong.secure.Authenticator;

public abstract class StreamAuthCheckClickListener extends MemberOnlyClickListener {
	
	private Song song;
	private Music music;
	private Context context;
	private ProgressDialog progressDialog;
	private View view;
	
	public StreamAuthCheckClickListener(Song song) {
		this.song = song;
	}
	
	public StreamAuthCheckClickListener(Music music) {
		this.music = music;
	}

	@Override
	public void onLoggedIn(View v, User user) {
		view = v;
		context = v.getContext();
		showProgressDialog(context);
		
		try {
			if (song != null) {
				music = song.getMusic();
			}
			
			JSONObject message = new JSONObject();
			message.put("MCONSTID", music.getMelonContentsId());
			message.put("MRID", music.getId());
			message.put("USERMKEY", user.getMelonId());
			message.put("CONTSTYPE", "3C0007");
			message.put("POCID", MelonHttpHeaderScheme.POC_CODE);
			message.put("TIME", System.currentTimeMillis());
			message.put("TOKEN", Authenticator.getAccessToken());
			message.put("USERID", user.getMelonUsername());
			
			if (song != null) {
				message.put("USERCONTSID", song.getId());
			}
			
			JSONObjectRequest request = new JSONObjectRequest(
					"check/token", null, message,
					successListener,
					errorListener);
			((App) context.getApplicationContext()).addShortLivedRequest(context, request);
		} catch (Exception e) {
			e.printStackTrace();
			dismissProgressDialog();
		}
	}
	
	private Listener<JSONObject> successListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject response) {
			dismissProgressDialog();
			
			try {
				MelonResponseHooker.hook(context, ((BaseActivity) context).getSupportFragmentManager(), response);
				onPassed(view);
			} catch (MelonResponseException e) {
				e.printStackTrace();
			}
		}
	};
	
	private ErrorListener errorListener = new ErrorListener() {

		@Override
		public void onErrorResponse(VolleyError error) {
			dismissProgressDialog();
			Toast.makeText(context, "권한 에러", Toast.LENGTH_SHORT).show();
		}
	};
	
	private void showProgressDialog(Context context) {
		ProgressDialog dialog = getProgressDialog(context);
		if (!dialog.isShowing()) {
			dialog.show();
		}
	}
	
	private ProgressDialog getProgressDialog(Context context) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(context);
			progressDialog.setIndeterminate(true);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(false);
			progressDialog.setMessage("잠시만 기다려주세요");
		}
		
		return progressDialog;
	}
	
	private void dismissProgressDialog() {
		try {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();
			// ignore
		}
	}
	
	public abstract void onPassed(View v);

}
