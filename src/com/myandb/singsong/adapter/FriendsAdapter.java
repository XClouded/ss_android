package com.myandb.singsong.adapter;

import com.android.volley.RequestQueue;
import com.android.volley.Request.Method;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.event.Listeners;
import com.myandb.singsong.event.MemberOnlyClickListener;
import com.myandb.singsong.model.Profile;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.OAuthJustRequest;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.ImageHelper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendsAdapter extends AutoLoadAdapter<User> {
	
	public FriendsAdapter(Context context) {
		super(context, User.class, true);
	}
	
	public FriendsAdapter(Context context, UrlBuilder urlBuilder) {
		this(context);
		
		resetRequest(urlBuilder);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final UserHolder userHolder;
		final User user = (User) getItem(position);
		final Profile profile = user.getProfile();
		
		if (view == null) {
			view = View.inflate(getContext(), R.layout.row_friend, null);
			
			userHolder = new UserHolder();
			userHolder.tvUserNickname = (TextView) view.findViewById(R.id.tv_user_nickname);
			userHolder.tvUserStatus = (TextView) view.findViewById(R.id.tv_user_status);
			userHolder.ivUserPhoto = (ImageView) view.findViewById(R.id.iv_user_photo);
			userHolder.btnFollow = (Button) view.findViewById(R.id.btn_follow);
			
			view.setTag(userHolder);
		} else {
			userHolder = (UserHolder) view.getTag();
		}
		
		if (profile != null) {
			if (profile.getStatusMessage().length() > 0) {
				userHolder.tvUserStatus.setVisibility(View.VISIBLE);
				userHolder.tvUserStatus.setText(profile.getStatusMessage());
			} else {
				userHolder.tvUserStatus.setVisibility(View.INVISIBLE);
			}
			
			userHolder.tvUserNickname.setText(user.getNickname());
			
			ImageHelper.displayPhoto(user, userHolder.ivUserPhoto);
			
			userHolder.btnFollow.setTag(user);
			toggleFollowing(userHolder.btnFollow, user.isFollowing());
			
			view.setOnClickListener(Listeners.getProfileClickListener(getContext(), user));
		}
		
		return view;
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
	
	private OnClickListener followClickListener = new MemberOnlyClickListener() {
		
		@Override
		public void onActivated(View v) {
			User user = (User) v.getTag();
			
			UrlBuilder urlBuilder = new UrlBuilder();
			String url = urlBuilder.s("friendships").s(user.getId()).toString();
			
			RequestQueue queue = ((App) v.getContext().getApplicationContext()).getQueueInstance();
			OAuthJustRequest request = new OAuthJustRequest(Method.POST, url, null);
			queue.add(request);
			
			toggleFollowing(v, true);
		}
	};
	
	private OnClickListener unfollowClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			User user = (User) v.getTag();
			
			UrlBuilder urlBuilder = new UrlBuilder();
			String url = urlBuilder.s("friendships").s(user.getId()).toString();
			
			RequestQueue queue = ((App) v.getContext().getApplicationContext()).getQueueInstance();
			OAuthJustRequest request = new OAuthJustRequest(Method.DELETE, url, null);
			queue.add(request);
			
			toggleFollowing(v, false);
		}
	};
	
	private static class UserHolder {
		
		public TextView tvUserNickname;
		public TextView tvUserStatus;
		public ImageView ivUserPhoto;
		public Button btnFollow;
		
	}

}
