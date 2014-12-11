package com.myandb.singsong.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.dialog.BaseDialog;
import com.myandb.singsong.dialog.DeleteCommentDialog;
import com.myandb.singsong.dialog.ReportCommentDialog;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.SongComment;
import com.myandb.singsong.model.User;
import com.myandb.singsong.secure.Authenticator;

public class CommentAdapter extends HolderAdapter<SongComment, CommentAdapter.CommentHolder> {
	
	private SongComment selectedComment;
	private FragmentManager fragmentManager;

	public CommentAdapter(Context context) {
		super(SongComment.class);
		this.fragmentManager = ((ActionBarActivity) context).getSupportFragmentManager();
	}

	@Override
	public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = View.inflate(parent.getContext(), R.layout.row_comment, null);
		return new CommentHolder(view);
	}

	@Override
	public void onBindViewHolder(Context context, CommentHolder viewHolder, int position) {
		final SongComment comment = getItem(position);
		final User writer = comment.getWriter();
		
		viewHolder.tvNickname.setText(writer.getNickname());
		viewHolder.tvCreated.setText(comment.getWorkedCreatedTime(getCurrentDate()));
		viewHolder.tvCommentContent.setText(comment.getContent());
		viewHolder.ivUserPhoto.setOnClickListener(writer.getProfileClickListener(context));
		viewHolder.ivMenu.setTag(comment);
		viewHolder.ivMenu.setOnClickListener(menuClickListener);
		
		ImageHelper.displayPhoto(writer, viewHolder.ivUserPhoto);
	}
	
	private OnClickListener menuClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			selectedComment = (SongComment) v.getTag();
			PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
			if (isUserWriter(selectedComment)) {
				popupMenu.inflate(R.menu.delete_comment);
			} else {
				popupMenu.inflate(R.menu.report_comment);
			}
			popupMenu.setOnMenuItemClickListener(menuItemClickListener);
			popupMenu.show();
		}
		
		private boolean isUserWriter(SongComment comment) {
			return Authenticator.getUser().getId() == comment.getWriter().getId();
		}
	};
	
	private OnMenuItemClickListener menuItemClickListener = new OnMenuItemClickListener() {
		
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			BaseDialog dialog = null;
			Bundle bundle = new Bundle();
			
			switch (item.getItemId()) {
			case R.id.action_report_comment:
				dialog = new ReportCommentDialog();
				bundle.putString("", selectedComment.getContent());
				break;
				
			case R.id.action_delete_comment:
				dialog = new DeleteCommentDialog();
				break;

			default:
				return false;
			}
			
			bundle.putInt("", selectedComment.getId());
			dialog.setArguments(bundle);
			dialog.show(fragmentManager, "");
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
