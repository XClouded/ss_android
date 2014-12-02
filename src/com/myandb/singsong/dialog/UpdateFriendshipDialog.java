package com.myandb.singsong.dialog;

import org.json.JSONException;
import org.json.JSONObject;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Request.Method;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.fragment.UserHomeFragment;
import com.myandb.singsong.model.Friendship;
import com.myandb.singsong.net.OAuthJustRequest;
import com.myandb.singsong.net.UrlBuilder;

public class UpdateFriendshipDialog extends BaseDialog {
	
	private UserHomeFragment fragment;
	private Button btnAllowPush;
	private Button btnUnfollow;
	private Button btnRecommendArtist;
	private Friendship friendship;

	public UpdateFriendshipDialog(UserHomeFragment fragment) {
		super(fragment.getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
		this.fragment = fragment;
	}

	@Override
	protected void initialize() {
		// Nothing to run
	}

	@Override
	protected int getResourceId() {
		return R.layout.dialog_update_friendship;
	}

	@Override
	protected void onViewInflated() {
		btnAllowPush = (Button) findViewById(R.id.btn_allow_push);
		btnUnfollow = (Button) findViewById(R.id.btn_unfollow);
		btnRecommendArtist = (Button) findViewById(R.id.btn_recommend_artist);
	}

	@Override
	protected void setupViews() {
		btnAllowPush.setOnClickListener(updateAllowPushClickListener);
		btnUnfollow.setOnClickListener(unfollowClickListener);
		btnRecommendArtist.setOnClickListener(recommendClickListener);
	}

	@Override
	public void show() {
		super.show();
		
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
				UrlBuilder urlBuilder = new UrlBuilder();
				String url = urlBuilder.s("friendships").s(friendship.getFollowingUserId()).toString();
				JSONObject message = new JSONObject();
				message.put("allow_notify", isAllowNotify);
				
				OAuthJustRequest request = new OAuthJustRequest(Method.PUT, url, message);
				RequestQueue queue = ((App) fragment.getActivity().getApplicationContext()).getQueueInstance();
				queue.add(request);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			friendship.setAllowNotify(isAllowNotify);
			
			updateTextOnAllowPush();
		}
	};
	
	private View.OnClickListener unfollowClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			UrlBuilder urlBuilder = new UrlBuilder();
			String url = urlBuilder.s("friendships").s(friendship.getFollowingUserId()).toString();
			
			OAuthJustRequest request = new OAuthJustRequest(Method.DELETE, url, null);
			RequestQueue queue = ((App) fragment.getActivity().getApplicationContext()).getQueueInstance();
			queue.add(request);
			
			fragment.toggleFollowing(false);
			
			UpdateFriendshipDialog.this.dismiss();
		}
	};
	
	private View.OnClickListener recommendClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			UrlBuilder urlBuilder = new UrlBuilder();
			String url = urlBuilder.s("candidates").s(friendship.getFollowingUserId()).toString();
			
			OAuthJustRequest request = new OAuthJustRequest(Method.POST, url, null);
			RequestQueue queue = ((App) fragment.getActivity().getApplicationContext()).getQueueInstance();
			queue.add(request);
			
			Toast.makeText(getContext(), getContext().getString(R.string.t_recommend_has_accepted), Toast.LENGTH_SHORT).show();
			
			UpdateFriendshipDialog.this.dismiss();
		}
	};
	
	public void setFriendship(Friendship friendship) {
		this.friendship = friendship;
	}

	@Override
	public void dismiss() {
		super.dismiss();
		this.friendship = null;
	}

}
