package com.myandb.singsong.event;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.myandb.singsong.App;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JSONObjectSuccessListener;
import com.myandb.singsong.net.MelonHttpScheme;
import com.myandb.singsong.net.MelonResponseHooker;
import com.myandb.singsong.net.MelonResponseHooker.MelonAuthorizationException;
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
			message.put("POCID", MelonHttpScheme.POC_CODE);
			message.put("TIME", System.currentTimeMillis());
			message.put("TOKEN", Authenticator.getAccessToken());
			message.put("USERID", user.getMelonUsername());
			
			if (song != null) {
				message.put("USERCONTSID", song.getId());
			}
			
			JSONObjectRequest request = new JSONObjectRequest(
					"melon/check/auth/stream", null, message,
					new JSONObjectSuccessListener(this, "onCheckStreamAuthSuccess"),
					new JSONErrorListener(this, "onSystemError"));
			((App) context.getApplicationContext()).addShortLivedRequest(context, request);
		} catch (Exception e) {
			e.printStackTrace();
			onSystemError();
		}
	}
	
	public void onCheckStreamAuthSuccess(JSONObject response) {
		try {
			MelonResponseHooker.hook(context, ((BaseActivity) context).getSupportFragmentManager(), response);
			onPassed(view);
			dismissProgressDialog();
		} catch (MelonAuthorizationException e) {
			e.printStackTrace();
			checkRealNameAuthorization();
		} catch (MelonResponseException e) {
			e.printStackTrace();
			dismissProgressDialog();
		} catch (Exception e) {
			e.printStackTrace();
			dismissProgressDialog();
		}
	}
	
	public void onSystemError() {
		dismissProgressDialog();
		Toast.makeText(context, "시스템 오류", Toast.LENGTH_SHORT).show();
	}
	
	private void checkRealNameAuthorization() {
		try {
			JSONObject message = new JSONObject();
			message.put("memberKey", Authenticator.getUser().getMelonId());
			message.put("authType", "nineteen");
			
			JSONObjectRequest request = new JSONObjectRequest(
					"melon/check/auth/nineteen", null, message,
					new JSONObjectSuccessListener(this, "onCheckRealNameSuccess"),
					new JSONErrorListener(this, "onSystemError"));
			((App) context.getApplicationContext()).addShortLivedRequest(context, request);
		} catch (Exception e) {
			e.printStackTrace();
			onSystemError();
		}
	}
	
	public void onCheckRealNameSuccess(JSONObject response) {
		try {
			MelonResponseHooker.hook(context, ((BaseActivity) context).getSupportFragmentManager(), response);
			onPassed(view);
			dismissProgressDialog();
		} catch (MelonResponseException e) {
			e.printStackTrace();
			dismissProgressDialog();
		} catch (Exception e) {
			e.printStackTrace();
			onSystemError();
		}
	}
	
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
