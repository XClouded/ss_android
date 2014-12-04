package com.myandb.singsong.dialog;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request.Method;
import com.myandb.singsong.R;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.SongComment;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.OnFailListener;
import com.myandb.singsong.net.JSONObjectSuccessListener;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.widget.SlidingPlayerLayout;

public class WriteCommentDialog extends BaseDialog {
	
	private ImageView ivWriterPhoto;
	private EditText etComment;
	private Button btnSubmit;
	private User user;
	private Song song;
	private SlidingPlayerLayout layout;

	@Override
	protected void initialize(Activity activity) {
		user = Authenticator.getUser();
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		ivWriterPhoto = (ImageView) view.findViewById(R.id.iv_writer_photo);
		etComment = (EditText) view.findViewById(R.id.et_comment);
		btnSubmit = (Button) view.findViewById(R.id.btn_submit);
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_write_comment;
	}

	@Override
	protected void setupViews() {
		ImageHelper.displayPhoto(user, ivWriterPhoto);
		btnSubmit.setOnClickListener(submitClickListener);
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
				
				JSONObjectRequest request = new JSONObjectRequest(
						Method.POST,
						"songs/" + song.getId() + "/comments",
						message,
						new JSONObjectSuccessListener(WriteCommentDialog.this, "onSubmitSuccess", SongComment.class),
						new OnFailListener(WriteCommentDialog.this, "onSubmitError")
				);
				
				addRequest(request);
				dismiss();
			} else {
				makeToast(R.string.t_comment_length_policy);
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
