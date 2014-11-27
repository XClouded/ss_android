package com.myandb.singsong.dialog;

import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.event.OnVolleyWeakError;
import com.myandb.singsong.event.OnVolleyWeakResponse;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.SongComment;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.OAuthJsonObjectRequest;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.widget.SlidingPlayerLayout;

public class WriteCommentDialog extends BaseDialog {
	
	private ImageView ivWriterPhoto;
	private EditText etComment;
	private Button btnSubmit;
	private User user;
	private Song song;
	private SlidingPlayerLayout layout;

	public WriteCommentDialog(SlidingPlayerLayout layout) {
		super(layout.getContext(), android.R.style.Theme_Translucent_NoTitleBar);
		this.layout = layout;
	}

	@Override
	protected void initialize() {
		user = Authenticator.getUser();
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_write_comment;
	}

	@Override
	protected void onViewInflated() {
		ivWriterPhoto = (ImageView)findViewById(R.id.iv_writer_photo);
		etComment = (EditText)findViewById(R.id.et_comment);
		btnSubmit = (Button)findViewById(R.id.btn_submit);
	}

	@Override
	protected void setupViews() {
		ImageHelper.displayPhoto(user, ivWriterPhoto);
		btnSubmit.setOnClickListener(submitClickListener);
	}
	
	public void setSong(Song song) {
		this.song = song;
	}

	@Override
	public void dismiss() {
		super.dismiss();
//		parent.closeEditText(etComment);
	}

	private View.OnClickListener submitClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			btnSubmit.setEnabled(false);
			
			String content = etComment.getText().toString();
			
			if (content.trim().length() > 0) {
//				parent.showProgressDialog();
				
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
						new OnVolleyWeakResponse<WriteCommentDialog, JSONObject>(WriteCommentDialog.this, "onSubmitSuccess", SongComment.class),
						new OnVolleyWeakError<WriteCommentDialog>(WriteCommentDialog.this, "onSubmitError")
				);
				
				RequestQueue queue = ((App) getContext().getApplicationContext()).getQueueInstance();
				queue.add(request);
				
				WriteCommentDialog.this.dismiss();
			} else {
				Toast.makeText(getContext().getApplicationContext(), getContext().getString(R.string.t_comment_length_policy), Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	public void onSubmitSuccess(SongComment comment) {
		layout.addComment(comment);
//		parent.dismissProgressDialog();
		
		btnSubmit.setEnabled(true);
	}
	
	public void onSubmitError() {
//		parent.dismissProgressDialog();
		
		btnSubmit.setEnabled(true);
	}

}
