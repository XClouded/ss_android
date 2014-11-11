package com.myandb.singsong.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.dialog.ChangeSongStateDialog;
import com.myandb.singsong.event.Listeners;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.UrlBuilder;

public class MySongAdapter extends AutoLoadAdapter<Song> {
	
	private static final LinearLayout.LayoutParams LL_FULL_SIZE_PARAMS = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

	private final boolean isCurrentUser;
	private final boolean isDeleted;
	private final ChangeSongStateDialog dialog;
	
	public MySongAdapter(Context context, boolean isCurrentUser, boolean isDeleted) {
		super(context, Song.class, false);
		
		this.isCurrentUser = isCurrentUser;
		this.isDeleted = isDeleted;
		this.dialog = new ChangeSongStateDialog(context, this, isDeleted);
	}
	
	public MySongAdapter(Context context, UrlBuilder urlBuilder, boolean isCurrentUser, boolean isDeleted) {
		this(context, isCurrentUser, isDeleted);
		
		resetRequest(urlBuilder);
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final SongHolder songHolder;
		final Song song = (Song) getItem(position);
		final Music music = song.getMusic();
		final User creator = song.getCreator();
		final List<Song> children = song.getChildren();
		
		if (view == null) {
			view = View.inflate(getContext(), R.layout.row_my_song, null);
			
			songHolder = new SongHolder();
			songHolder.tvCreatorNickname = (TextView) view.findViewById(R.id.tv_parent_user_nickname);
			songHolder.tvCreatorMessage = (TextView) view.findViewById(R.id.tv_parent_song_message);
			songHolder.tvPartnerNickname = (TextView) view.findViewById(R.id.tv_this_user_nickname);
			songHolder.tvPartnerMessage = (TextView) view.findViewById(R.id.tv_this_song_message);
			songHolder.tvMusicInfo = (TextView) view.findViewById(R.id.tv_music_info);
			songHolder.tvLikeNum = (TextView) view.findViewById(R.id.tv_song_like_num);
			songHolder.tvCommentNum = (TextView) view.findViewById(R.id.tv_song_comment_num);
			songHolder.tvCollaboNum = (TextView) view.findViewById(R.id.tv_song_collabo_num);
			songHolder.tvCreatedTime = (TextView) view.findViewById(R.id.tv_song_created_time);
			
			songHolder.ivCreatorPhoto = (ImageView) view.findViewById(R.id.iv_parent_user_photo);
			songHolder.ivCreatorImage = (ImageView) view.findViewById(R.id.iv_parent_song_image);
			songHolder.ivPartnerPhoto = (ImageView) view.findViewById(R.id.iv_this_user_photo);
			songHolder.ivPartnerImage = (ImageView) view.findViewById(R.id.iv_this_song_image);
			songHolder.ivFirstChildrenImage = (ImageView) view.findViewById(R.id.iv_first_children_upload_image);
			songHolder.ivSecondChildrenImage = (ImageView) view.findViewById(R.id.iv_second_children_upload_image);
			songHolder.ivChangeStateSong = (ImageView) view.findViewById(R.id.iv_change_state_song);
			
			songHolder.vCollaboNumWrapper = view.findViewById(R.id.ll_collabo_num_wrapper);
			songHolder.vPartnerWrapper = view.findViewById(R.id.ll_partner_wrapper);
			songHolder.vChildrenWrapper = view.findViewById(R.id.ll_children_wrapper);
			songHolder.vPartnerPhotoWrapper = view.findViewById(R.id.ll_partner_photo_wrapper);
			
			view.setTag(songHolder);
		} else {
			songHolder = (SongHolder) view.getTag();
		}
		
		if (music != null && creator != null) {
			songHolder.tvLikeNum.setText(song.getWorkedLikeNum());
			songHolder.tvCommentNum.setText(song.getWorkedCommentNum());
			songHolder.tvCreatedTime.setText(song.getWorkedCreatedTime(getCurrentDate()));
			
			songHolder.tvMusicInfo.setText(music.getSingerName());
			songHolder.tvMusicInfo.append("\n");
			songHolder.tvMusicInfo.append(music.getWorkedTitle());
			songHolder.tvMusicInfo.append("\t");
			songHolder.tvMusicInfo.append("(" + song.getWorkedDuration() + ")");
			
			if (!song.isRoot()) {
				final Song parentSong = song.getParentSong();
				final User parentUser = song.getParentUser();
				
				if (parentUser != null) {
					songHolder.vCollaboNumWrapper.setVisibility(View.GONE);
					songHolder.vPartnerPhotoWrapper.setVisibility(View.VISIBLE);
					songHolder.vPartnerWrapper.setVisibility(View.VISIBLE);
					songHolder.vChildrenWrapper.setVisibility(View.GONE);
					songHolder.ivPartnerImage.setLayoutParams(LL_FULL_SIZE_PARAMS);
					
					songHolder.tvCreatorNickname.setText(parentUser.getNickname());
					songHolder.tvCreatorMessage.setText(parentSong.getCroppedMessage());
					songHolder.tvPartnerNickname.setText(creator.getNickname());
					songHolder.tvPartnerMessage.setText(song.getCroppedMessage());
					
					ImageHelper.displayPhoto(parentUser.getPhotoUrl(), songHolder.ivCreatorPhoto);
					ImageHelper.displayPhoto(parentSong.getPhotoUrl(), songHolder.ivCreatorImage);
					ImageHelper.displayPhoto(creator, songHolder.ivPartnerPhoto);
					ImageHelper.displayPhoto(song.getPhotoUrl(), songHolder.ivPartnerImage);
					
					view.setOnClickListener(Listeners.getPlayClickListener(getContext(), song));
				}
			} else if (children != null) {
				songHolder.vCollaboNumWrapper.setVisibility(View.VISIBLE);
				songHolder.vPartnerPhotoWrapper.setVisibility(View.GONE);
				
				if (children.size() == 0) {
					songHolder.vPartnerWrapper.setVisibility(View.GONE);
				} else if (children.size() == 1) {
					songHolder.vPartnerWrapper.setVisibility(View.VISIBLE);
					songHolder.vChildrenWrapper.setVisibility(View.GONE);
					songHolder.ivPartnerImage.setLayoutParams(LL_FULL_SIZE_PARAMS);
				} else if (children.size() == 2) {
					songHolder.vPartnerWrapper.setVisibility(View.VISIBLE);
					songHolder.vChildrenWrapper.setVisibility(View.VISIBLE);
					songHolder.ivSecondChildrenImage.setVisibility(View.GONE);
				} else {
					songHolder.vPartnerWrapper.setVisibility(View.VISIBLE);
					songHolder.vChildrenWrapper.setVisibility(View.VISIBLE);
					songHolder.ivSecondChildrenImage.setVisibility(View.VISIBLE);
				}
				
				if (children.size() == 0) {
					view.setOnClickListener(Listeners.getPlayClickListener(getContext(), song));
				} else {
					view.setOnClickListener(Listeners.getChildrenClickListener(getContext(), song));
				}
				
				songHolder.tvCollaboNum.setText(song.getWorkedCollaboNum());
				songHolder.tvCreatorNickname.setText(creator.getNickname());
				songHolder.tvCreatorMessage.setText(song.getCroppedMessage());
				
				ImageHelper.displayPhoto(creator.getPhotoUrl(), songHolder.ivCreatorPhoto);
				ImageHelper.displayPhoto(song.getPhotoUrl(), songHolder.ivCreatorImage);
				
				setChildrenImage(children, 0, songHolder.ivPartnerImage);
				setChildrenImage(children, 1, songHolder.ivFirstChildrenImage);
				setChildrenImage(children, 2, songHolder.ivSecondChildrenImage);
			}
			
			if (isCurrentUser && dialog != null) {
				songHolder.ivChangeStateSong.setVisibility(View.VISIBLE);
				
				if (isDeleted) {
					songHolder.ivChangeStateSong.setImageResource(R.drawable.ic_restore);
				} else {
					songHolder.ivChangeStateSong.setImageResource(R.drawable.ic_trash_inverse);
				}
				
				songHolder.ivChangeStateSong.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (song != null) {
							dialog.setTargetSong(song);
							dialog.show();
						}
					}
				});
			} else {
				songHolder.ivChangeStateSong.setVisibility(View.GONE);
			}
		}
		
		return view;
	}
	
	private void setChildrenImage(List<Song> children, int index, ImageView imageView) {
		if (children.size() > index) {
			final Song childSong = children.get(index);
			
			if (childSong != null) {
				ImageHelper.displayPhoto(childSong.getPhotoUrl(), imageView);
			}
		}
	}
	
	public void onDestroy() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}
	
	private static class SongHolder {
		
		public TextView tvCreatorNickname,
						tvCreatorMessage,
						tvPartnerNickname,
						tvPartnerMessage,
						tvLikeNum,
						tvCommentNum,
						tvCollaboNum,
						tvCreatedTime,
						tvMusicInfo;
		
		public ImageView ivCreatorPhoto,
						 ivCreatorImage,
						 ivPartnerPhoto,
						 ivPartnerImage,
						 ivFirstChildrenImage,
						 ivSecondChildrenImage,
						 ivChangeStateSong;
		
		public View vCollaboNumWrapper,
					vPartnerWrapper,
					vChildrenWrapper,
					vPartnerPhotoWrapper;
		
	}

}
