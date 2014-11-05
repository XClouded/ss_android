package com.myandb.singsong.dialog;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.PlayerActivity;
import com.myandb.singsong.event.OnVolleyWeakError;
import com.myandb.singsong.event.OnVolleyWeakResponse;
import com.myandb.singsong.model.Comment;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.OAuthJsonObjectRequest;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.ImageHelper;

public class WriteCommentDialog extends BaseDiaglog {
	
	private ImageView ivCancel;
	private ImageView ivWriterPhoto;
	private EditText etComment;
	private Button btnSubmit;
	private PlayerActivity parent;
	private User user;
	private Song song;

	public WriteCommentDialog(Context context, User user, Song song) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
		
		parent = (PlayerActivity) context;
		this.user = user;
		this.song = song;
	}

	@Override
	protected void initializeView() {
		setContentView(R.layout.dialog_write_comment);
		
		ivCancel = (ImageView)findViewById(R.id.iv_cancel);
		ivWriterPhoto = (ImageView)findViewById(R.id.iv_writer_photo);
		etComment = (EditText)findViewById(R.id.et_comment);
		btnSubmit = (Button)findViewById(R.id.btn_submit);
	}

	@Override
	protected void setupView() {
		if (user != null && song != null) {
			ImageHelper.displayPhoto(user, ivWriterPhoto);
			btnSubmit.setOnClickListener(submitClickListener);
		}
		
		ivCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				WriteCommentDialog.this.dismiss();
			}
			
		});
	}

	@Override
	public void dismiss() {
		super.dismiss();
		
		parent.closeEditText(etComment);
	}

	private View.OnClickListener submitClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			btnSubmit.setEnabled(false);
			
			String content = etComment.getText().toString();
			
			if (content.trim().length() > 0) {
				parent.showProgressDialog();
				
				JSONObject message = new JSONObject();
				try {
					message.put("user_id", user.getId());
					message.put("content", content);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				UrlBuilder urlBuilder = new UrlBuilder();
				String url = urlBuilder.s("songs").s(song.getId()).s("comments").toString();
				OAuthJsonObjectRequest request = new OAuthJsonObjectRequest(
						Method.POST, url, message,
						new OnVolleyWeakResponse<WriteCommentDialog, JSONObject>(WriteCommentDialog.this, "onSubmitSuccess", Comment.class),
						new OnVolleyWeakError<WriteCommentDialog>(WriteCommentDialog.this, "onSubmitError")
				);
				
				RequestQueue queue = ((App) parent.getApplicationContext()).getQueueInstance();
				queue.add(request);
				
				WriteCommentDialog.this.dismiss();
			} else {
				Toast.makeText(parent, "댓글을 입력하세요. :)", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	public void onSubmitSuccess(Comment<?> comment) {
		parent.insertComment(comment);
		parent.dismissProgressDialog();
		
		btnSubmit.setEnabled(true);
	}
	
	public void onSubmitError() {
		parent.dismissProgressDialog();
		
		btnSubmit.setEnabled(true);
	}

}
