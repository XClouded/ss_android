package com.myandb.singsong.adapter;

import com.android.volley.Request.Method;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.event.ActivateOnlyClickListener;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.FacebookUser;
import com.myandb.singsong.model.Profile;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JustRequest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendsAdapter extends HolderAdapter<User, FriendsAdapter.UserHolder> {
	
	public FriendsAdapter() {
		super(User.class);
	}

	@Override
	public UserHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.row_friend, parent, false);
		return new UserHolder(view);
	}

	@Override
	public void onBindViewHolder(Context context, UserHolder viewHolder, User user, int position) {
		final FacebookUser facebookUser = user.getFacebookUser();
		final Profile profile = user.getProfile();
		
		if (profile != null) {
			viewHolder.tvUserStatus.setText(profile.getStatusMessage());
		}
		
		if (facebookUser != null) {
			viewHolder.tvUserNickname.setText(facebookUser.getName());
			ImageHelper.displayPhoto(facebookUser.getPhotoUrl(), viewHolder.ivUserPhoto);
		} else {
			viewHolder.tvUserNickname.setText(user.getNickname());
			ImageHelper.displayPhoto(user, viewHolder.ivUserPhoto);
		}
		
		viewHolder.btnFollow.setTag(user);
		viewHolder.view.setOnClickListener(user.getProfileClickListener());
		toggleFollowing(viewHolder.btnFollow, user.isFollowing());
	}
	
	private void toggleFollowing(View v, boolean isFollowing) {
		if (isFollowing) {
			v.setBackgroundResource(R.drawable.button_primary_selector);
			v.setOnClickListener(unfollowClickListener);
			((Button) v).setText(v.getContext().getString(R.string.button_following));
		} else {
			v.setBackgroundResource(R.drawable.button_grey_selector);
			v.setOnClickListener(followClickListener);
			((Button) v).setText(v.getContext().getString(R.string.button_follow));
		} 
	}
	
	private OnClickListener followClickListener = new ActivateOnlyClickListener() {
		
		@Override
		public void onActivated(View v, User user) {
			User friend = (User) v.getTag();
			JustRequest request = new JustRequest(Method.POST, "friendships/" + friend.getId(), null);
			((App) v.getContext().getApplicationContext()).addShortLivedRequest(v.getContext(), request);
			toggleFollowing(v, true);
		}
	};
	
	private OnClickListener unfollowClickListener = new ActivateOnlyClickListener() {
		
		@Override
		public void onActivated(View v, User user) {
			User friend = (User) v.getTag();
			JustRequest request = new JustRequest(Method.DELETE, "friendships/" + friend.getId(), null);
			((App) v.getContext().getApplicationContext()).addShortLivedRequest(v.getContext(), request);
			toggleFollowing(v, false);
		}
	};
	
	public static final class UserHolder extends ViewHolder {
		
		public TextView tvUserNickname;
		public TextView tvUserStatus;
		public ImageView ivUserPhoto;
		public Button btnFollow;
		
		public UserHolder(View view) {
			super(view);
			
			tvUserNickname = (TextView) view.findViewById(R.id.tv_user_nickname);
			tvUserStatus = (TextView) view.findViewById(R.id.tv_user_status);
			ivUserPhoto = (ImageView) view.findViewById(R.id.iv_user_photo);
			btnFollow = (Button) view.findViewById(R.id.btn_follow);
		}
		
	}

}
