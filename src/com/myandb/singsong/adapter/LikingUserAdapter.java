package com.myandb.singsong.adapter;

import com.myandb.singsong.R;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.SongLiking;
import com.myandb.singsong.model.User;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class LikingUserAdapter extends HolderAdapter<SongLiking, LikingUserAdapter.UserHolder> {
	
	public LikingUserAdapter() {
		super(SongLiking.class);
	}

	@Override
	public UserHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.row_song_liking, parent, false);
		return new UserHolder(view);
	}

	@Override
	public void onBindViewHolder(Context context, UserHolder viewHolder, int position) {
		final SongLiking liking = getItem(position);
		final User user = liking.getUser();
		
		viewHolder.tvUserNickname.setText(user.getNickname());
		viewHolder.view.setOnClickListener(user.getProfileClickListener(context));
		ImageHelper.displayPhoto(user, viewHolder.ivUserPhoto);
	}
	
	public static final class UserHolder extends ViewHolder {
		
		public TextView tvUserNickname;
		public ImageView ivUserPhoto;
		
		public UserHolder(View view) {
			super(view);
			
			tvUserNickname = (TextView) view.findViewById(R.id.tv_user_nickname);
			ivUserPhoto = (ImageView) view.findViewById(R.id.iv_user_photo);
		}
		
	}

}
