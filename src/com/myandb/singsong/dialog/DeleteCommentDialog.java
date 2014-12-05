package com.myandb.singsong.dialog;

import org.json.JSONObject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.myandb.singsong.R;
import com.myandb.singsong.model.Comment;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.net.JSONObjectSuccessListener;

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
			JSONObjectRequest request = new JSONObjectRequest(
					Method.DELETE, "comments/" + comment.getId(), null,
					new JSONObjectSuccessListener(DeleteCommentDialog.this, "onDeleteSuccess"),
					new JSONErrorListener(DeleteCommentDialog.this, "onDeleteError")
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
