package com.myandb.singsong.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.dialog.DeleteCommentDialog;
import com.myandb.singsong.dialog.ReportCommentDialog;
import com.myandb.singsong.event.Listeners;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Comment;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Authenticator;

public class CommentAdapter extends AutoLoadAdapter<Comment> {

	private final User currentUser;
	private final ReportCommentDialog reportDialog;
	private final DeleteCommentDialog deleteDialog;
	
	public CommentAdapter(Context context) {
		super(context, Comment.class, false);
		
		this.currentUser = Authenticator.getUser();
		this.reportDialog = new ReportCommentDialog(context, currentUser);
		this.deleteDialog = new DeleteCommentDialog(context);
	}
	
	public CommentAdapter(Context context, UrlBuilder urlBuilder) {
		this(context);
		
		resetRequest(urlBuilder);
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final CommentHolder commentHolder;
		final Comment<?> comment = (Comment<?>) getItem(position);
		final User writer = comment.getWriter();
		
		if (view == null) {
			view = View.inflate(getContext(), R.layout.row_comment, null);
			
			commentHolder = new CommentHolder();
			commentHolder.ivUserPhoto = (ImageView) view.findViewById(R.id.iv_comment_user_photo);
			commentHolder.ivReportComment = (ImageView) view.findViewById(R.id.iv_report_comment);
			commentHolder.ivDeleteComment = (ImageView) view.findViewById(R.id.iv_delete_comment);
			commentHolder.tvNickname = (TextView) view.findViewById(R.id.tv_comment_user_nickname);
			commentHolder.tvCreated = (TextView) view.findViewById(R.id.tv_comment_created);
			commentHolder.tvCommentContent = (TextView) view.findViewById(R.id.tv_comment_content);
			
			view.setTag(commentHolder);
		} else {
			commentHolder = (CommentHolder) view.getTag();
		}
		
		commentHolder.tvNickname.setText(writer.getNickname());
		commentHolder.tvCreated.setText(comment.getWorkedCreatedTime(getCurrentDate()));
		commentHolder.tvCommentContent.setText(comment.getContent());
		
		ImageHelper.displayPhoto(writer, commentHolder.ivUserPhoto);
		commentHolder.ivUserPhoto.setOnClickListener(Listeners.getProfileClickListener(getContext(), writer));
		
		if (currentUser.getId() == writer.getId()) {
			commentHolder.ivReportComment.setVisibility(View.GONE);
			commentHolder.ivDeleteComment.setVisibility(View.VISIBLE);
			commentHolder.ivDeleteComment.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					deleteDialog.setComment(comment);
					deleteDialog.show();
				}
				
			});
		} else {
			commentHolder.ivDeleteComment.setVisibility(View.GONE);
			commentHolder.ivReportComment.setVisibility(View.VISIBLE);
			commentHolder.ivReportComment.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					reportDialog.setComment(comment);
					reportDialog.show();
				}
				
			});
		}
		
		return view;
	}
	
	public void onDestroy() {
		if (deleteDialog != null) {
			deleteDialog.dismiss();
		}
		
		if (reportDialog != null) {
			reportDialog.dismiss();
		}
	}
	
	private static class CommentHolder {
		
		public ImageView ivUserPhoto,
						 ivReportComment,
						 ivDeleteComment;
		public TextView tvNickname,
						tvCreated,
						tvCommentContent;
		
	}
	
}
