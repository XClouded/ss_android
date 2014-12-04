package com.myandb.singsong.adapter;

import com.android.volley.RequestQueue;
import com.android.volley.Request.Method;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.event.ActivateOnlyClickListener;
import com.myandb.singsong.event.Listeners;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Profile;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JustRequest;

import android.content.Context;
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
	public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = View.inflate(parent.getContext(), R.layout.row_friend, null);
		return new UserHolder(view);
	}

	@Override
	public void onBindViewHolder(UserHolder viewHolder, int position) {
		final User user = getItem(position);
		final Profile profile = user.getProfile();
		final Context context = viewHolder.view.getContext();
		
		viewHolder.tvUserStatus.setText(profile.getStatusMessage());
		viewHolder.tvUserNickname.setText(user.getNickname());
		
		ImageHelper.displayPhoto(user, viewHolder.ivUserPhoto);
		
		viewHolder.btnFollow.setTag(user);
		toggleFollowing(viewHolder.btnFollow, user.isFollowing());
		
		viewHolder.view.setOnClickListener(Listeners.getProfileClickListener(context, user));
	}
	
	private void toggleFollowing(View v, boolean isFollowing) {
		if (isFollowing) {
			v.setBackgroundResource(R.drawable.img_following);
			v.setOnClickListener(unfollowClickListener);
		} else {
			v.setBackgroundResource(R.drawable.img_follow);
			v.setOnClickListener(followClickListener);
		} 
	}
	
	private OnClickListener followClickListener = new ActivateOnlyClickListener() {
		
		@Override
		public void onActivated(View v, User user) {
			User friend = (User) v.getTag();
			RequestQueue queue = ((App) v.getContext().getApplicationContext()).getQueueInstance();
			JustRequest request = new JustRequest(Method.POST, "friendships/" + friend.getId(), null);
			queue.add(request);
			
			toggleFollowing(v, true);
		}
	};
	
	private OnClickListener unfollowClickListener = new ActivateOnlyClickListener() {
		
		@Override
		public void onActivated(View v, User user) {
			User friend = (User) v.getTag();
			RequestQueue queue = ((App) v.getContext().getApplicationContext()).getQueueInstance();
			JustRequest request = new JustRequest(Method.DELETE, "friendships/" + friend.getId(), null);
			queue.add(request);
			
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
