package com.myandb.singsong.dialog;

import org.json.JSONObject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.myandb.singsong.R;
import com.myandb.singsong.event.OnVolleyWeakError;
import com.myandb.singsong.event.OnVolleyWeakResponse;
import com.myandb.singsong.model.Comment;
import com.myandb.singsong.net.OAuthJsonObjectRequest;
import com.myandb.singsong.net.UrlBuilder;

public class DeleteCommentDialog extends BaseDialog {
	
	private Button btnDeleteComment;
	private Comment<?> comment;

	@Override
	protected void initialize(Activity activity) {}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		btnDeleteComment = (Button) view.findViewById(R.id.btn_delete_comment);
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_delete_comment;
	}

	@Override
	protected void setupViews() {
		if (comment != null) {
			btnDeleteComment.setOnClickListener(deleteCommentClickListener);
		}
	}
	
	private View.OnClickListener deleteCommentClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			UrlBuilder urlBuilder = new UrlBuilder();
			String url = urlBuilder.s("comments").s(comment.getId()).toString();
			
			OAuthJsonObjectRequest request = new OAuthJsonObjectRequest(
					Method.DELETE, url, null,
					new OnVolleyWeakResponse<DeleteCommentDialog, JSONObject>(DeleteCommentDialog.this, "onDeleteSuccess"),
					new OnVolleyWeakError<DeleteCommentDialog>(DeleteCommentDialog.this, "onDeleteError")
					);
			
			addRequest(request);
		}
	};
	
	public void onDeleteSuccess(JSONObject response) {
//		parent.removeComment(comment);
		
		dismiss();
	}
	
	public void onDeleteError() {
		Toast.makeText(getActivity(), getString(R.string.t_poor_network_connection), Toast.LENGTH_SHORT).show();
	}

}
