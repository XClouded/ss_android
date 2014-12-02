package com.myandb.singsong.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.event.Listeners;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.SongComment;
import com.myandb.singsong.model.User;

public class CommentAdapter extends HolderAdapter<SongComment, CommentAdapter.CommentHolder> {

	public CommentAdapter() {
		super(SongComment.class);
	}

	@Override
	public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = View.inflate(parent.getContext(), R.layout.row_comment, null);
		return new CommentHolder(view);
	}

	@Override
	public void onBindViewHolder(CommentHolder viewHolder, int position) {
		final SongComment comment = getItem(position);
		final User writer = comment.getWriter();
		final Context context = viewHolder.view.getContext();
		
		viewHolder.tvNickname.setText(writer.getNickname());
		viewHolder.tvCreated.setText(comment.getWorkedCreatedTime(getCurrentDate()));
		viewHolder.tvCommentContent.setText(comment.getContent());
		
		ImageHelper.displayPhoto(writer, viewHolder.ivUserPhoto);
		viewHolder.ivUserPhoto.setOnClickListener(Listeners.getProfileClickListener(context, writer));
		
		// report and delete comment
		// using pop up menu
	}
	
	public static final class CommentHolder extends ViewHolder {
		
		public ImageView ivUserPhoto;
		public ImageView ivReportComment;
		public ImageView ivDeleteComment;
		public TextView tvNickname;
		public TextView tvCreated;
		public TextView tvCommentContent;
		
		public CommentHolder(View view) {
			super(view);
			
			ivUserPhoto = (ImageView) view.findViewById(R.id.iv_comment_user_photo);
			ivReportComment = (ImageView) view.findViewById(R.id.iv_report_comment);
			ivDeleteComment = (ImageView) view.findViewById(R.id.iv_delete_comment);
			tvNickname = (TextView) view.findViewById(R.id.tv_comment_user_nickname);
			tvCreated = (TextView) view.findViewById(R.id.tv_comment_created);
			tvCommentContent = (TextView) view.findViewById(R.id.tv_comment_content);
		}
		
	}
	
}
