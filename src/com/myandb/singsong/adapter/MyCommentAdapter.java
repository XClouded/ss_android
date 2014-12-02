package com.myandb.singsong.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.event.Listeners;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.SongComment;
import com.myandb.singsong.model.User;

public class MyCommentAdapter extends HolderAdapter<SongComment, MyCommentAdapter.CommentHolder> {
	
	public MyCommentAdapter() {
		super(SongComment.class);
	}

	@Override
	public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = View.inflate(parent.getContext(), R.layout.row_my_comment, null);
		return new CommentHolder(view);
	}

	@Override
	public void onBindViewHolder(CommentHolder viewHolder, int position) {
		final SongComment comment = getItem(position);
		final Song song = comment.getCommentable();
		final User user = comment.getWriter();
		final Music music = song.getMusic();
		final Context context = viewHolder.view.getContext();
		
		viewHolder.tvCommentUserNickname.setText(user.getNickname());
		viewHolder.tvCommentContent.setText(comment.getContent());
		viewHolder.tvCommentCreatedTime.setText(comment.getWorkedCreatedTime(getCurrentDate()));
		
		viewHolder.tvMusicInfo.setText(music.getSingerName());
		viewHolder.tvMusicInfo.append(" - ");
		viewHolder.tvMusicInfo.append(music.getTitle());
		
		ImageHelper.displayPhoto(user, viewHolder.ivCommentUserPhoto);
		ImageHelper.displayPhoto(music.getAlbumPhotoUrl(), viewHolder.ivAlbumPhoto);
		
		viewHolder.view.setOnClickListener(Listeners.getPlayClickListener(context, song));
	}
	
	public static final class CommentHolder extends ViewHolder {
		
		public TextView tvCommentUserNickname;
		public TextView tvCommentCreatedTime;
		public TextView tvCommentContent;
		public TextView tvMusicInfo;
		public ImageView ivAlbumPhoto;
		public ImageView ivCommentUserPhoto;
		
		public CommentHolder(View view) {
			super(view);

			tvCommentUserNickname = (TextView) view.findViewById(R.id.tv_comment_user_nickname);
			tvCommentContent = (TextView) view.findViewById(R.id.tv_comment_content);
			tvCommentCreatedTime = (TextView) view.findViewById(R.id.tv_comment_created);
			tvMusicInfo = (TextView) view.findViewById(R.id.tv_music_info);
			ivAlbumPhoto = (ImageView) view.findViewById(R.id.iv_album_photo);
			ivCommentUserPhoto = (ImageView) view.findViewById(R.id.iv_comment_user_photo);
		}
		
	}

}
