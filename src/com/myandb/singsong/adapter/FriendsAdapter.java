package com.myandb.singsong.adapter;

import com.myandb.singsong.R;
import com.myandb.singsong.event.Listeners;
import com.myandb.singsong.model.Profile;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.ImageHelper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
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
			
			view.setOnClickListener(Listeners.getProfileClickListener(getContext(), user));
		}
		
		return view;
	}
	
	private static class UserHolder {
		
		public TextView tvUserNickname;
		public TextView tvUserStatus;
		public ImageView ivUserPhoto;
		
	}

}
