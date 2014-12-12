package com.myandb.singsong.dialog;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request.Method;
import com.google.gson.Gson;
import com.myandb.singsong.R;
import com.myandb.singsong.fragment.UserHomeFragment;
import com.myandb.singsong.model.Friendship;
import com.myandb.singsong.net.JustRequest;
import com.myandb.singsong.util.Utility;

public class UpdateFriendshipDialog extends BaseDialog {
	
	public static final String EXTRA_FRIENDSHIP = "friendship";
	
	private UserHomeFragment fragment;
	private Button btnAllowPush;
	private Button btnUnfollow;
	private Button btnRecommendArtist;
	private Friendship friendship;
	
	@Override
	protected int getResourceId() {
		return R.layout.dialog_update_friendship;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		Gson gson = Utility.getGsonInstance();
		String friendshipInJson = bundle.getString(EXTRA_FRIENDSHIP);
		friendship = gson.fromJson(friendshipInJson, Friendship.class);
	}

	@Override
	protected void initialize(Activity activity) {
		fragment = (UserHomeFragment) getParentFragment();
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		btnAllowPush = (Button) view.findViewById(R.id.btn_allow_push);
		btnUnfollow = (Button) view.findViewById(R.id.btn_unfollow);
		btnRecommendArtist = (Button) view.findViewById(R.id.btn_recommend_artist);
	}

	@Override
	protected void setupViews() {
		btnAllowPush.setOnClickListener(updateAllowPushClickListener);
		btnUnfollow.setOnClickListener(unfollowClickListener);
		btnRecommendArtist.setOnClickListener(recommendClickListener);
		updateTextOnAllowPush();
	}
	
	private void updateTextOnAllowPush() {
		if (friendship.isAllowNotify()) {
			btnAllowPush.setText("✓ ");
			btnAllowPush.append("알림 받기");
		} else {
			btnAllowPush.setText("알림 받기");
		}
	}
	
	private View.OnClickListener updateAllowPushClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			boolean isAllowNotify = !friendship.isAllowNotify();
			
			try {
				JSONObject message = new JSONObject();
				message.put("allow_notify", isAllowNotify);
				
				int followingId = friendship.getFollowingUserId();
				JustRequest request = new JustRequest(Method.PUT, "friendships/" + followingId, message);
				addRequest(request);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			fragment.toggleAllowNotify();
			friendship.setAllowNotify(isAllowNotify);
			updateTextOnAllowPush();
		}
	};
	
	private View.OnClickListener unfollowClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int followingId = friendship.getFollowingUserId();
			JustRequest request = new JustRequest(Method.DELETE, "friendships/" + followingId, null);
			addRequest(request);
			fragment.toggleFollowing(false);
			dismiss();
		}
	};
	
	private View.OnClickListener recommendClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int followingId = friendship.getFollowingUserId();
			JustRequest request = new JustRequest(Method.POST, "candidates/" + followingId, null);
			addRequest(request);
			makeToast(R.string.t_recommend_has_accepted);
			dismiss();
		}
	};

}
