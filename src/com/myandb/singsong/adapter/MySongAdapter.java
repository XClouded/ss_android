package com.myandb.singsong.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JustRequest;
import com.myandb.singsong.widget.RoundedImageView;

public class MySongAdapter extends HolderAdapter<Song, MySongAdapter.SongHolder> {
	
	private static final LinearLayout.LayoutParams LL_FULL_SIZE_PARAMS = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

	private final boolean isCurrentUser;
	private final boolean isDeleted;
	private Context context;
	private Song selectedSong;
	private List<Boolean> cornerRadius;
	
	public MySongAdapter() {
		this(true, true);
	}
	
	public MySongAdapter(boolean isCurrentUser, boolean isDeleted) {
		super(Song.class);
		
		this.isCurrentUser = isCurrentUser;
		this.isDeleted = isDeleted;
		
		cornerRadius = new ArrayList<Boolean>();
		cornerRadius.add(true);
		cornerRadius.add(true);
		cornerRadius.add(true);
		cornerRadius.add(true);
	}

	@Override
	public SongHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.row_song_user_page, parent, false);
		context = parent.getContext();
		return new SongHolder(view);
	}

	@Override
	public void onBindViewHolder(Context context, SongHolder viewHolder, final Song song, int position) {
		final Music music = song.getMusic();
		final User creator = song.getCreator();
		final List<Song> children = song.getChildren();
		if (music == null || creator == null) {
			return;
		}

		viewHolder.tvLikeNum.setText(song.getWorkedLikeNum());
		viewHolder.tvCommentNum.setText(song.getWorkedCommentNum());
		viewHolder.tvCreatedTime.setText(song.getWorkedCreatedTime(getCurrentDate()));
		
		viewHolder.tvMusicInfo.setText(music.getWorkedTitle());
		viewHolder.tvMusicInfo.append("\t");
		viewHolder.tvMusicInfo.append("(" + song.getWorkedDuration() + ")");
		viewHolder.tvMusicInfo.append("\n");
		viewHolder.tvMusicInfo.append(music.getSingerName());
		
		viewHolder.vPrelistenControl.setOnClickListener(song.getSampleClickListener());
		viewHolder.view.setOnClickListener(song.getPlayClickListener());
		
		if (!song.isRoot()) {
			final Song parentSong = song.getParentSong();
			final User parentUser = song.getParentUser();
			
			if (parentSong == null || parentUser == null) {
				return;
			}
			
			viewHolder.vCollaboNumWrapper.setVisibility(View.GONE);
			viewHolder.vThisPhotoWrapper.setVisibility(View.VISIBLE);
			viewHolder.vThisWrapper.setVisibility(View.VISIBLE);
			viewHolder.vChildrenWrapper.setVisibility(View.GONE);
			viewHolder.ivThisSongImage.setLayoutParams(LL_FULL_SIZE_PARAMS);
			setCornerRadius(viewHolder.ivParentSongImage, false);
			
			viewHolder.tvParentUserNickname.setText(parentUser.getNickname());
			viewHolder.tvParentSongMessage.setText(parentSong.getCroppedMessage());
			viewHolder.tvThisUserNickname.setText(creator.getNickname());
			viewHolder.tvThisSongMessage.setText(song.getCroppedMessage());
			
			ImageHelper.displayPhoto(parentUser.getPhotoUrl(), viewHolder.ivParentUserPhoto);
			ImageHelper.displayPhoto(parentSong.getPhotoUrl(), viewHolder.ivParentSongImage);
			ImageHelper.displayPhoto(creator, viewHolder.ivThisUserPhoto);
			ImageHelper.displayPhoto(song.getPhotoUrl(), viewHolder.ivThisSongImage);
		} else if (children != null) {
			viewHolder.vCollaboNumWrapper.setVisibility(View.VISIBLE);
			viewHolder.vThisPhotoWrapper.setVisibility(View.GONE);
			setCornerRadius(viewHolder.ivParentSongImage, true);
			
			if (children.size() == 0) {
				viewHolder.vThisWrapper.setVisibility(View.GONE);
			} else if (children.size() == 1) {
				viewHolder.vThisWrapper.setVisibility(View.VISIBLE);
				viewHolder.vChildrenWrapper.setVisibility(View.GONE);
				viewHolder.ivThisSongImage.setLayoutParams(LL_FULL_SIZE_PARAMS);
			} else if (children.size() == 2) {
				viewHolder.vThisWrapper.setVisibility(View.VISIBLE);
				viewHolder.vChildrenWrapper.setVisibility(View.VISIBLE);
				viewHolder.ivSecondChildrenImage.setVisibility(View.GONE);
			} else {
				viewHolder.vThisWrapper.setVisibility(View.VISIBLE);
				viewHolder.vChildrenWrapper.setVisibility(View.VISIBLE);
				viewHolder.ivSecondChildrenImage.setVisibility(View.VISIBLE);
			}
			
			viewHolder.tvCollaboNum.setText(song.getWorkedCollaboNum());
			viewHolder.tvParentUserNickname.setText(creator.getNickname());
			viewHolder.tvParentSongMessage.setText(song.getCroppedMessage());
			
			ImageHelper.displayPhoto(creator.getPhotoUrl(), viewHolder.ivParentUserPhoto);
			ImageHelper.displayPhoto(song.getPhotoUrl(), viewHolder.ivParentSongImage);
			
			viewHolder.vCollaboNumWrapper.setOnClickListener(song.getChildrenClickListener());
			
			setChildrenImage(children, 0, viewHolder.ivThisSongImage);
			setChildrenImage(children, 1, viewHolder.ivFirstChildrenImage);
			setChildrenImage(children, 2, viewHolder.ivSecondChildrenImage);
		}
		
		if (isCurrentUser) {
			viewHolder.ivChangeStateSong.setVisibility(View.VISIBLE);
			
			if (isDeleted) {
				viewHolder.ivChangeStateSong.setImageResource(R.drawable.ic_restore);
			} else {
				viewHolder.ivChangeStateSong.setImageResource(R.drawable.ic_trash_inverse);
			}
			
			viewHolder.ivChangeStateSong.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					selectedSong = song;
					PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
					if (isDeleted) {
						popupMenu.inflate(R.menu.restore_song);
					} else {
						popupMenu.inflate(R.menu.delete_song);
					}
					popupMenu.setOnMenuItemClickListener(menuItemClickListener);
					popupMenu.show();
				}
			});
		} else {
			viewHolder.ivChangeStateSong.setVisibility(View.GONE);
		}
	}
	
	private void setCornerRadius(ImageView imageView, boolean topRight) {
		if (imageView instanceof RoundedImageView) {
			if (cornerRadius != null && cornerRadius.size() == 4) {
				cornerRadius.set(1, topRight);
				((RoundedImageView) imageView).setCornerDirection(cornerRadius);
			}
		}
	}
	
	private OnMenuItemClickListener menuItemClickListener = new OnMenuItemClickListener() {

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			if (selectedSong == null) {
				return false;
			}
			
			switch (item.getItemId()) {
			case R.id.action_delete_song:
				deleteSong();
				return true;
				
			case R.id.action_restore_song:
				restoreSong();
				return true;

			default:
				return false;
			}
		}
		
	};
	
	private void deleteSong() {
		String segment = "songs/" + String.valueOf(selectedSong.getId());
		JustRequest request = new JustRequest(Method.DELETE, segment, null, null);
		((App) context.getApplicationContext()).addShortLivedRequest(context, request);
		removeItem(selectedSong);
	}
	
	private void restoreSong() {
		String segment = "songs/" + String.valueOf(selectedSong.getId());
		JustRequest request = new JustRequest(Method.PUT, segment, null, null);
		((App) context.getApplicationContext()).addShortLivedRequest(context, request);
		removeItem(selectedSong);
	}
	
	private void setChildrenImage(List<Song> children, int index, ImageView imageView) {
		if (children.size() > index) {
			final Song childSong = children.get(index);
			
			if (childSong != null) {
				ImageHelper.displayPhoto(childSong.getPhotoUrl(), imageView);
			}
		}
	}
	
	public static final class SongHolder extends ViewHolder {
		
		public TextView tvParentUserNickname;
		public TextView tvParentSongMessage;
		public TextView tvThisUserNickname;
		public TextView tvThisSongMessage;
		public TextView tvLikeNum;
		public TextView tvCommentNum;
		public TextView tvCollaboNum;
		public TextView tvCreatedTime;
		public TextView tvMusicInfo;
		public ImageView ivParentUserPhoto;
		public ImageView ivParentSongImage;
		public ImageView ivThisUserPhoto;
		public ImageView ivThisSongImage;
		public ImageView ivFirstChildrenImage;
		public ImageView ivSecondChildrenImage;
		public ImageView ivChangeStateSong;
		public View vCollaboNumWrapper;
		public View vThisWrapper;
		public View vChildrenWrapper;
		public View vThisPhotoWrapper;
		public View vPrelistenControl;
		
		public SongHolder(View view) {
			super(view);
			
			tvParentUserNickname = (TextView) view.findViewById(R.id.tv_parent_user_nickname);
			tvParentSongMessage = (TextView) view.findViewById(R.id.tv_parent_song_message);
			tvThisUserNickname = (TextView) view.findViewById(R.id.tv_this_user_nickname);
			tvThisSongMessage = (TextView) view.findViewById(R.id.tv_this_song_message);
			tvMusicInfo = (TextView) view.findViewById(R.id.tv_music_info);
			tvLikeNum = (TextView) view.findViewById(R.id.tv_song_like_num);
			tvCommentNum = (TextView) view.findViewById(R.id.tv_song_comment_num);
			tvCollaboNum = (TextView) view.findViewById(R.id.tv_song_collabo_num);
			tvCreatedTime = (TextView) view.findViewById(R.id.tv_song_created_time);
			ivParentUserPhoto = (ImageView) view.findViewById(R.id.iv_parent_user_photo);
			ivParentSongImage = (ImageView) view.findViewById(R.id.iv_parent_song_image);
			ivThisUserPhoto = (ImageView) view.findViewById(R.id.iv_this_user_photo);
			ivThisSongImage = (ImageView) view.findViewById(R.id.iv_this_song_image);
			ivFirstChildrenImage = (ImageView) view.findViewById(R.id.iv_first_children_upload_image);
			ivSecondChildrenImage = (ImageView) view.findViewById(R.id.iv_second_children_upload_image);
			ivChangeStateSong = (ImageView) view.findViewById(R.id.iv_change_state_song);
			vCollaboNumWrapper = view.findViewById(R.id.ll_collabo_num_wrapper);
			vThisWrapper = view.findViewById(R.id.ll_this_wrapper);
			vChildrenWrapper = view.findViewById(R.id.ll_children_wrapper);
			vThisPhotoWrapper = view.findViewById(R.id.ll_this_photo_wrapper);
			vPrelistenControl = view.findViewById(R.id.ll_prelisten_control);
		}
		
	}

}
