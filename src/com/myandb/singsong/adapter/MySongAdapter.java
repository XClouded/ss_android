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

public class MySongAdapter extends HolderAdapter<Song, MySongAdapter.SongHolder> {
	
	private static final LinearLayout.LayoutParams LL_FULL_SIZE_PARAMS = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

	private final boolean isCurrentUser;
	private final boolean isDeleted;
	private final ChangeSongStateDialog dialog;
	
	public MySongAdapter(Context context, boolean isCurrentUser, boolean isDeleted) {
		super(Song.class);
		
		this.isCurrentUser = isCurrentUser;
		this.isDeleted = isDeleted;
		this.dialog = new ChangeSongStateDialog(context, this, isDeleted);
	}

	@Override
	public SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = View.inflate(parent.getContext(), R.layout.row_my_song, null);
		return new SongHolder(view);
	}

	@Override
	public void onBindViewHolder(SongHolder viewHolder, int position) {
		final Song song = (Song) getItem(position);
		final Music music = song.getMusic();
		final User creator = song.getCreator();
		final List<Song> children = song.getChildren();
		final Context context = viewHolder.view.getContext();

		viewHolder.tvLikeNum.setText(song.getWorkedLikeNum());
		viewHolder.tvCommentNum.setText(song.getWorkedCommentNum());
		viewHolder.tvCreatedTime.setText(song.getWorkedCreatedTime(getCurrentDate()));
		
		viewHolder.tvMusicInfo.setText(music.getSingerName());
		viewHolder.tvMusicInfo.append("\n");
		viewHolder.tvMusicInfo.append(music.getWorkedTitle());
		viewHolder.tvMusicInfo.append("\t");
		viewHolder.tvMusicInfo.append("(" + song.getWorkedDuration() + ")");
		
		if (!song.isRoot()) {
			final Song parentSong = song.getParentSong();
			final User parentUser = song.getParentUser();
			
			if (parentUser != null) {
				viewHolder.vCollaboNumWrapper.setVisibility(View.GONE);
				viewHolder.vPartnerPhotoWrapper.setVisibility(View.VISIBLE);
				viewHolder.vPartnerWrapper.setVisibility(View.VISIBLE);
				viewHolder.vChildrenWrapper.setVisibility(View.GONE);
				viewHolder.ivPartnerImage.setLayoutParams(LL_FULL_SIZE_PARAMS);
				
				viewHolder.tvCreatorNickname.setText(parentUser.getNickname());
				viewHolder.tvCreatorMessage.setText(parentSong.getCroppedMessage());
				viewHolder.tvPartnerNickname.setText(creator.getNickname());
				viewHolder.tvPartnerMessage.setText(song.getCroppedMessage());
				
				ImageHelper.displayPhoto(parentUser.getPhotoUrl(), viewHolder.ivCreatorPhoto);
				ImageHelper.displayPhoto(parentSong.getPhotoUrl(), viewHolder.ivCreatorImage);
				ImageHelper.displayPhoto(creator, viewHolder.ivPartnerPhoto);
				ImageHelper.displayPhoto(song.getPhotoUrl(), viewHolder.ivPartnerImage);
				
				viewHolder.view.setOnClickListener(Listeners.getPlayClickListener(context, song));
			}
		} else if (children != null) {
			viewHolder.vCollaboNumWrapper.setVisibility(View.VISIBLE);
			viewHolder.vPartnerPhotoWrapper.setVisibility(View.GONE);
			
			if (children.size() == 0) {
				viewHolder.vPartnerWrapper.setVisibility(View.GONE);
			} else if (children.size() == 1) {
				viewHolder.vPartnerWrapper.setVisibility(View.VISIBLE);
				viewHolder.vChildrenWrapper.setVisibility(View.GONE);
				viewHolder.ivPartnerImage.setLayoutParams(LL_FULL_SIZE_PARAMS);
			} else if (children.size() == 2) {
				viewHolder.vPartnerWrapper.setVisibility(View.VISIBLE);
				viewHolder.vChildrenWrapper.setVisibility(View.VISIBLE);
				viewHolder.ivSecondChildrenImage.setVisibility(View.GONE);
			} else {
				viewHolder.vPartnerWrapper.setVisibility(View.VISIBLE);
				viewHolder.vChildrenWrapper.setVisibility(View.VISIBLE);
				viewHolder.ivSecondChildrenImage.setVisibility(View.VISIBLE);
			}
			
			if (children.size() == 0) {
				viewHolder.view.setOnClickListener(Listeners.getPlayClickListener(context, song));
			} else {
				viewHolder.view.setOnClickListener(Listeners.getChildrenClickListener(context, song));
			}
			
			viewHolder.tvCollaboNum.setText(song.getWorkedCollaboNum());
			viewHolder.tvCreatorNickname.setText(creator.getNickname());
			viewHolder.tvCreatorMessage.setText(song.getCroppedMessage());
			
			ImageHelper.displayPhoto(creator.getPhotoUrl(), viewHolder.ivCreatorPhoto);
			ImageHelper.displayPhoto(song.getPhotoUrl(), viewHolder.ivCreatorImage);
			
			setChildrenImage(children, 0, viewHolder.ivPartnerImage);
			setChildrenImage(children, 1, viewHolder.ivFirstChildrenImage);
			setChildrenImage(children, 2, viewHolder.ivSecondChildrenImage);
		}
		
		if (isCurrentUser && dialog != null) {
			viewHolder.ivChangeStateSong.setVisibility(View.VISIBLE);
			
			if (isDeleted) {
				viewHolder.ivChangeStateSong.setImageResource(R.drawable.ic_restore);
			} else {
				viewHolder.ivChangeStateSong.setImageResource(R.drawable.ic_trash_inverse);
			}
			
			viewHolder.ivChangeStateSong.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (song != null) {
						dialog.setTargetSong(song);
						dialog.show();
					}
				}
			});
		} else {
			viewHolder.ivChangeStateSong.setVisibility(View.GONE);
		}
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
	
	public static final class SongHolder extends ViewHolder {
		
		public TextView tvCreatorNickname;
		public TextView tvCreatorMessage;
		public TextView tvPartnerNickname;
		public TextView tvPartnerMessage;
		public TextView tvLikeNum;
		public TextView tvCommentNum;
		public TextView tvCollaboNum;
		public TextView tvCreatedTime;
		public TextView tvMusicInfo;
		public ImageView ivCreatorPhoto;
		public ImageView ivCreatorImage;
		public ImageView ivPartnerPhoto;
		public ImageView ivPartnerImage;
		public ImageView ivFirstChildrenImage;
		public ImageView ivSecondChildrenImage;
		public ImageView ivChangeStateSong;
		public View vCollaboNumWrapper;
		public View vPartnerWrapper;
		public View vChildrenWrapper;
		public View vPartnerPhotoWrapper;
		
		public SongHolder(View view) {
			super(view);
			
			tvCreatorNickname = (TextView) view.findViewById(R.id.tv_parent_user_nickname);
			tvCreatorMessage = (TextView) view.findViewById(R.id.tv_parent_song_message);
			tvPartnerNickname = (TextView) view.findViewById(R.id.tv_this_user_nickname);
			tvPartnerMessage = (TextView) view.findViewById(R.id.tv_this_song_message);
			tvMusicInfo = (TextView) view.findViewById(R.id.tv_music_info);
			tvLikeNum = (TextView) view.findViewById(R.id.tv_song_like_num);
			tvCommentNum = (TextView) view.findViewById(R.id.tv_song_comment_num);
			tvCollaboNum = (TextView) view.findViewById(R.id.tv_song_collabo_num);
			tvCreatedTime = (TextView) view.findViewById(R.id.tv_song_created_time);
			ivCreatorPhoto = (ImageView) view.findViewById(R.id.iv_parent_user_photo);
			ivCreatorImage = (ImageView) view.findViewById(R.id.iv_parent_song_image);
			ivPartnerPhoto = (ImageView) view.findViewById(R.id.iv_this_user_photo);
			ivPartnerImage = (ImageView) view.findViewById(R.id.iv_this_song_image);
			ivFirstChildrenImage = (ImageView) view.findViewById(R.id.iv_first_children_upload_image);
			ivSecondChildrenImage = (ImageView) view.findViewById(R.id.iv_second_children_upload_image);
			ivChangeStateSong = (ImageView) view.findViewById(R.id.iv_change_state_song);
			vCollaboNumWrapper = view.findViewById(R.id.ll_collabo_num_wrapper);
			vPartnerWrapper = view.findViewById(R.id.ll_partner_wrapper);
			vChildrenWrapper = view.findViewById(R.id.ll_children_wrapper);
			vPartnerPhotoWrapper = view.findViewById(R.id.ll_partner_photo_wrapper);
		}
		
	}

}
