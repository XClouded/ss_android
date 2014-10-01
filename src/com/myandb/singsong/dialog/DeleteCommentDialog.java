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
import com.myandb.singsong.activity.PlayerActivity;
import com.myandb.singsong.event.OnVolleyWeakError;
import com.myandb.singsong.event.OnVolleyWeakResponse;
import com.myandb.singsong.model.Comment;
import com.myandb.singsong.net.OAuthJsonObjectRequest;
import com.myandb.singsong.net.UrlBuilder;

public class DeleteCommentDialog extends BaseDiaglog {
	
	private ImageView ivCancel;
	private Button btnDeleteComment;
	private Comment<?> comment;
	private PlayerActivity parent;

	public DeleteCommentDialog(Context context) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
		
		parent = (PlayerActivity) context;
	}

	@Override
	protected void initializeView() {
		setContentView(R.layout.dialog_delete_comment);
		
		ivCancel = (ImageView)findViewById(R.id.iv_cancel);
		btnDeleteComment = (Button)findViewById(R.id.btn_delete_comment);
	}

	@Override
	protected void setupView() {
		ivCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DeleteCommentDialog.this.dismiss();
			}
		});
	}
	
	@Override
	public void show() {
		super.show();
		
		if (comment != null) {
			btnDeleteComment.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					UrlBuilder urlBuilder = UrlBuilder.getInstance();
					String url = urlBuilder.l("comments").l(comment.getId()).build();
					
					OAuthJsonObjectRequest request = new OAuthJsonObjectRequest(
							Method.DELETE, url, null,
							new OnVolleyWeakResponse<DeleteCommentDialog, JSONObject>(DeleteCommentDialog.this, "onDeleteSuccess"),
							new OnVolleyWeakError<DeleteCommentDialog>(DeleteCommentDialog.this, "onDeleteError")
							);
					
					RequestQueue queue = ((App) getContext().getApplicationContext()).getQueueInstance();
					queue.add(request);
				}
			});			
		}
	}
	
	public void onDeleteSuccess(JSONObject response) {
		parent.removeComment(comment);
		
		dismiss();
	}
	
	public void onDeleteError() {
		Toast.makeText(getContext(), "삭제하기에 실패하였습니다. 네트워크 상태를 확인해 주세요.", Toast.LENGTH_SHORT).show();
	}

	public void setComment(Comment<?> comment) {
		this.comment = comment;
	}

}
