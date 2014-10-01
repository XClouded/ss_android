package com.myandb.singsong.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.event.Listeners;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.SongComment;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.ImageHelper;

public class MyCommentAdapter extends AutoLoadAdapter<SongComment> {
	
	public MyCommentAdapter(Context context) {
		super(context, SongComment.class, true);
	}
	
	public MyCommentAdapter(Context context, UrlBuilder urlBuilder) {
		this(context);
		
		resetRequest(urlBuilder);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final CommentHolder songHolder;
		final SongComment comment = (SongComment) getItem(position);
		final Song song = comment.getCommentable();
		final User user = comment.getWriter();
		
		if (view == null) {
			view = View.inflate(getContext(), R.layout.row_my_comment, null);
			
			songHolder = new CommentHolder();
			
			songHolder.tvCommentUserNickname = (TextView) view.findViewById(R.id.tv_comment_user_nickname);
			songHolder.tvCommentContent = (TextView) view.findViewById(R.id.tv_comment_content);
			songHolder.tvCommentCreatedTime = (TextView) view.findViewById(R.id.tv_comment_created);
			songHolder.tvMusicInfo = (TextView) view.findViewById(R.id.tv_music_info);
			
			songHolder.ivAlbumPhoto = (ImageView) view.findViewById(R.id.iv_album_photo);
			songHolder.ivCommentUserPhoto = (ImageView) view.findViewById(R.id.iv_comment_user_photo);
			
			view.setTag(songHolder);
		} else {
			songHolder = (CommentHolder) view.getTag();
		}
		
		if (song != null) {
			Music music = song.getMusic();
			
			songHolder.tvCommentUserNickname.setText(user.getNickname());
			songHolder.tvCommentContent.setText(comment.getContent());
			songHolder.tvCommentCreatedTime.setText(comment.getWorkedCreatedTime(getCurrentDate()));
			
			songHolder.tvMusicInfo.setText(music.getSingerName());
			songHolder.tvMusicInfo.append(" - ");
			songHolder.tvMusicInfo.append(music.getTitle());
			
			ImageHelper.displayPhoto(user, songHolder.ivCommentUserPhoto);
			ImageHelper.displayPhoto(music.getAlbumPhotoUrl(), songHolder.ivAlbumPhoto);
			
			view.setOnClickListener(Listeners.getPlayClickListener(getContext(), song));
		}
		
		return view;
	}
	
	private static class CommentHolder {
		
		public TextView tvCommentUserNickname,
			tvCommentCreatedTime,
			tvCommentContent,
			tvMusicInfo;
		
		public ImageView ivAlbumPhoto,
			ivCommentUserPhoto;
		
	}

}
