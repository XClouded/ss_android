package com.myandb.singsong.dialog;

import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.OldBaseActivity;
import com.myandb.singsong.adapter.MySongAdapter;
import com.myandb.singsong.event.OnVolleyWeakError;
import com.myandb.singsong.event.OnVolleyWeakResponse;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.net.OAuthJsonObjectRequest;
import com.myandb.singsong.net.UrlBuilder;

public class ChangeSongStateDialog extends BaseDiaglog {
	
	private Song song;
	private ImageView ivCancel;
	private Button btnChangeState;
	private OldBaseActivity parent;
	private MySongAdapter adapter;
	private boolean isDeleted;

	public ChangeSongStateDialog(Context context, MySongAdapter adapter, boolean isDeleted) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
		
		if (context instanceof OldBaseActivity) {
			parent = (OldBaseActivity) context;
		}
		
		this.isDeleted = isDeleted;
		this.adapter = adapter;
	}
	
	public void setTargetSong(Song song) {
		this.song = song;
	}

	@Override
	protected void initializeView() {
		setContentView(R.layout.dialog_change_song_state);
		
		ivCancel = (ImageView) findViewById(R.id.iv_cancel);
		btnChangeState = (Button) findViewById(R.id.btn_change_state);
	}

	@Override
	protected void setupView() {
		if (isDeleted) {
			btnChangeState.setText("복구하기");
		} else {
			btnChangeState.setText("삭제하기");
		}
		
		ivCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ChangeSongStateDialog.this.dismiss();
			}
		});
	}

	@Override
	public void show() {
		super.show();
		
		btnChangeState.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (song != null) {
					int method = isDeleted ? Method.PUT : Method.DELETE;
					UrlBuilder urlBuilder = new UrlBuilder();
					String url = urlBuilder.s("songs").s(song.getId()).toString();
					
					OAuthJsonObjectRequest request = new OAuthJsonObjectRequest(
							method, url, null,
							new OnVolleyWeakResponse<ChangeSongStateDialog, JSONObject>(ChangeSongStateDialog.this, "onChangeStateResponse"),
							new OnVolleyWeakError<ChangeSongStateDialog>(ChangeSongStateDialog.this, "onChangeStateError")
					);
					
					RequestQueue queue = ((App) parent.getApplicationContext()).getQueueInstance();
					queue.add(request);
				}
			}
			
		});
	}
	
	public void onChangeStateResponse(JSONObject response) {
		adapter.removeItem(song);
		
		dismiss();
	}
	
	public void onChangeStateError() {
		if (isDeleted) {
			Toast.makeText(getContext(), "복구하기에 실패하였습니다. 네트워크 상태를 확인해 주세요.", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getContext(), "삭제하기에 실패하였습니다. 네트워크 상태를 확인해 주세요.", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void dismiss() {
		super.dismiss();
		
		song = null;
	}

}
