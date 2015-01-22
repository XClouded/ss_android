package com.myandb.singsong.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.dialog.BaseDialog;
import com.myandb.singsong.dialog.ReportCommentDialog;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Comment;
import com.myandb.singsong.model.User;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.widget.SlidingPlayerLayout;

@SuppressWarnings("rawtypes")
public class CommentAdapter extends HolderAdapter<Comment, CommentAdapter.CommentHolder> {
	
	private Comment selectedComment;
	private SlidingPlayerLayout layout;
	private FragmentManager fragmentManager;
	private int textColorGrey = Color.parseColor("#777777");
	private int textColorDefault = Color.parseColor("#444444");
	
	public CommentAdapter() {
		super(Comment.class);
	}

	public CommentAdapter(SlidingPlayerLayout layout) {
		super(Comment.class);
		this.layout = layout;
		Context context = layout.getContext();
		this.fragmentManager = ((ActionBarActivity) context).getSupportFragmentManager();
	}

	@Override
	public CommentHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.row_comment, parent, false);
		return new CommentHolder(view);
	}

	@Override
	public void onBindViewHolder(Context context, CommentHolder viewHolder, int position) {
		final Comment<?> comment = getItem(position);
		final User writer = comment.getWriter();
		
		viewHolder.tvNickname.setText(writer.getNickname());
		viewHolder.tvCreated.setText(comment.getWorkedCreatedTime(getCurrentDate()));
		viewHolder.tvCommentContent.setText(comment.getContent());
		viewHolder.ivUserPhoto.setOnClickListener(writer.getProfileClickListener());
		
		if (fragmentManager != null) {
			if (Authenticator.isLoggedIn()) {
				viewHolder.ivMenu.setVisibility(View.VISIBLE);
			} else {
				viewHolder.ivMenu.setVisibility(View.INVISIBLE);
			}
			viewHolder.ivMenu.setTag(comment);
			viewHolder.ivMenu.setOnClickListener(menuClickListener);
		} else {
			viewHolder.ivMenu.setVisibility(View.INVISIBLE);
			viewHolder.tvNickname.setTextColor(textColorDefault);
			viewHolder.tvCommentContent.setTextColor(textColorGrey);
		}
		
		ImageHelper.displayPhoto(writer, viewHolder.ivUserPhoto);
	}
	
	private OnClickListener menuClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (!Authenticator.isLoggedIn()) {
				return;
			}
			
			selectedComment = (Comment) v.getTag();
			PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
			if (isUserWriter(selectedComment)) {
				popupMenu.inflate(R.menu.delete_comment);
			} else {
				popupMenu.inflate(R.menu.report_comment);
			}
			popupMenu.setOnMenuItemClickListener(menuItemClickListener);
			popupMenu.show();
		}
		
		private boolean isUserWriter(Comment comment) {
			return Authenticator.getUser().getId() == comment.getWriter().getId();
		}
	};
	
	private OnMenuItemClickListener menuItemClickListener = new OnMenuItemClickListener() {
		
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.action_report_comment:
				BaseDialog dialog = new ReportCommentDialog();
				Bundle bundle = new Bundle();
				bundle.putInt(ReportCommentDialog.EXTRA_COMMENT_ID, selectedComment.getId());
				bundle.putString(ReportCommentDialog.EXTRA_COMMENT_CONTENT, selectedComment.getContent());
				dialog.setArguments(bundle);
				dialog.show(fragmentManager, "");
				return true;
				
			case R.id.action_delete_comment:
				layout.deleteComment(selectedComment);
				break;

			default:
				return false;
			}
			
			return true;
		}
	};
	
	public static final class CommentHolder extends ViewHolder {
		
		public ImageView ivUserPhoto;
		public ImageView ivMenu;
		public TextView tvNickname;
		public TextView tvCreated;
		public TextView tvCommentContent;
		
		public CommentHolder(View view) {
			super(view);
			
			ivUserPhoto = (ImageView) view.findViewById(R.id.iv_comment_user_photo);
			ivMenu = (ImageView) view.findViewById(R.id.iv_menu);
			tvNickname = (TextView) view.findViewById(R.id.tv_comment_user_nickname);
			tvCreated = (TextView) view.findViewById(R.id.tv_comment_created);
			tvCommentContent = (TextView) view.findViewById(R.id.tv_comment_content);
		}
		
	}
	
}
