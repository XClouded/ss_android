package com.myandb.singsong.fragment;

import com.android.volley.Request.Method;
import com.google.gson.Gson;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.activity.UpActivity;
import com.myandb.singsong.adapter.CommentAdapter;
import com.myandb.singsong.adapter.FriendsAdapter;
import com.myandb.singsong.adapter.MyCommentAdapter;
import com.myandb.singsong.adapter.MyLikeSongAdapter;
import com.myandb.singsong.adapter.MySongAdapter;
import com.myandb.singsong.dialog.BaseDialog;
import com.myandb.singsong.dialog.GalleryDialog;
import com.myandb.singsong.dialog.UpdateFriendshipDialog;
import com.myandb.singsong.event.ActivateOnlyClickListener;
import com.myandb.singsong.fragment.TabListFragment.Tab;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Friendship;
import com.myandb.singsong.model.Team;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JSONObjectSuccessListener;
import com.myandb.singsong.net.JustRequest;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.util.Utility;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class TeamHomeFragment extends TabListFragment {
	
	private User currentUser;
	private Team team;
	private Friendship friendship;
	
	private ImageView ivTeamBackgroundPhoto;
	private ImageView ivTeamEmblem;
	private TextView tvTeamTitle;
	private TextView tvTeamDescription;
	private TextView tvTeamStatus;
	private TextView tvFollowers;
	private TextView tvTeamMembers;
	private TextView tvTeamCollabos;
	private Button btnApply;
	private Button btnFollow;
	private Button btnEditTeam;
	private Button btnEditRole;
	private View vMemberMenus;
	private View vTeamCollaboWorkspace;
	private View vTeamCommunity;
	private View vTeamCandidates;

	@Override
	protected int getListHeaderViewResId() {
		return R.layout.fragment_team_home_header;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		Gson gson = Utility.getGsonInstance();
		String teamInJson = bundle.getString(Team.class.getName());
		team = gson.fromJson(teamInJson, Team.class);
		currentUser = Authenticator.getUser();
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		super.onViewInflated(view, inflater);
		
		view = getListHeaderView();
		ivTeamBackgroundPhoto = (ImageView) view.findViewById(R.id.iv_team_background_photo);
		ivTeamEmblem = (ImageView) view.findViewById(R.id.iv_team_emblem);
		tvTeamTitle = (TextView) view.findViewById(R.id.tv_team_title);
		tvTeamDescription = (TextView) view.findViewById(R.id.tv_team_description);
		tvTeamStatus = (TextView) view.findViewById(R.id.tv_team_status);
		tvFollowers = (TextView) view.findViewById(R.id.tv_followers);
		tvTeamMembers = (TextView) view.findViewById(R.id.tv_team_members);
		tvTeamCollabos = (TextView) view.findViewById(R.id.tv_team_collabos);
		btnApply = (Button) view.findViewById(R.id.btn_apply);
		btnFollow = (Button) view.findViewById(R.id.btn_follow);
		btnEditTeam = (Button) view.findViewById(R.id.btn_edit_team);
		btnEditRole = (Button) view.findViewById(R.id.btn_edit_role);
	}
	
	private boolean isTeamMember() {
		return true;
	}
	
	private boolean isTeamLeader() {
		return true;
	}

	@Override
	protected void initialize(Activity activity) {
		super.initialize(activity);
		Gson gson = Utility.getGsonInstance();
		String teamInJson = "{\"id\":\"5\",\"name\":\"asdfegege\",\"leader_id\":\"24\",\"status_message\":\"\",\"max_member_num\":\"0\",\"member_num\":\"1\",\"genre_id\":\"0\",\"gender_type\":\"0\",\"published_song_num\":\"0\",\"follower_num\":\"0\",\"song_in_progress_num\":\"0\",\"main_photo_url\":\"\",\"main_photo_updated_at\":\"0000-00-00 00:00:00\",\"emblem_url\":\"\",\"emblem_updated_at\":\"0000-00-00 00:00:00\",\"created_at\":\"2015-03-25 11:39:04\",\"updated_at\":\"2015-03-25 11:39:04\",\"deleted_at\":null\"}";
		team = gson.fromJson(teamInJson, Team.class);
		currentUser = Authenticator.getUser();
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		super.setupViews(savedInstanceState);
		
		if (team == null) {
			return;
		}
		
		dispatchAuthorizationRelatedViews();
		
		displayTeamInformations();
		
		displayTextOnMainTab(tvTeamMembers, team.getCurrentMemberNumState(), "¸â¹ö");
		
		displayTextOnMainTab(tvTeamCollabos, team.getWorkedPublishedSongNum(), "¶¼Ã¢");
		
		displayPhotos();
	}
	
	private void dispatchAuthorizationRelatedViews() {
		if (isTeamMember()) {
			onTeamMember();
			if (isTeamLeader()) {
				onTeamLeader();
			}
		} else {
			onGuest();
		}
	}
	
	private void onTeamMember() {
		setViewsGone(btnApply, btnFollow, btnEditTeam);
		setViewsVisible(vMemberMenus, btnEditRole);
		btnEditRole.setOnClickListener(editRoleClickListener);
	}
	
	private void onTeamLeader() {
		setViewsVisible(btnEditTeam);
		btnEditTeam.setOnClickListener(editTeamClickListener);
	}
	
	private void onGuest() {
		setViewsGone(vMemberMenus, btnEditRole, btnEditTeam);
		setViewsVisible(btnApply, btnFollow);
		btnApply.setOnClickListener(applyClickListener);
		btnFollow.setOnClickListener(followClickListener);
	}
	
	private void displayTeamInformations() {
		tvTeamTitle.setText(team.getName());
		tvTeamStatus.setText(team.getStatusMessage());
		tvTeamDescription.setText(team.getDescription());
		tvFollowers.setText("ÆÈ·Î¿ö " + team.getFollowersNum());
	}
	
	private void displayTextOnMainTab(TextView textView, String num, String name) {
		SpannableString spannable = new SpannableString(name);
		spannable.setSpan(new RelativeSizeSpan(0.85f), 0, spannable.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		textView.setText(num);
		textView.append("\n");
		textView.append(spannable);
	}
	
	private void displayPhotos() {
		boolean teamHasPhoto = true;
		if (teamHasPhoto) {
			ImageHelper.displayPhoto(null, ivTeamEmblem, new SimpleImageLoadingListener() {

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					super.onLoadingComplete(imageUri, view, loadedImage);
					ivTeamEmblem.setOnClickListener(photoZoomClickListener);
				}
				
			});
		}
		
		boolean teamHasBackgroundPhoto = true;
		if (teamHasBackgroundPhoto) {
			ImageHelper.displayPhoto(null, ivTeamBackgroundPhoto, new SimpleImageLoadingListener() {

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					super.onLoadingComplete(imageUri, view, loadedImage);
					ivTeamBackgroundPhoto.setOnClickListener(photoZoomClickListener);
				}
				
			});
		}
	}
	
	private OnClickListener photoZoomClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Bundle bundle = new Bundle();
			bundle.putString(GalleryDialog.EXTRA_PHOTO_URL, "");
			GalleryDialog dialog = new GalleryDialog();
			dialog.setArguments(bundle);
			dialog.show(getChildFragmentManager(), "");
		}
	};
	
	private void checkUserFollowThisTeam() {
		JSONObjectRequest request = new JSONObjectRequest(
				"friendships/" + "team_id", null, null,
				new JSONObjectSuccessListener(this, "onFriendshipFound", Friendship.class),
				new JSONErrorListener(this, "onFriendshipNotFound")
		);
		addRequest(request);
	}
	
	public void onFriendshipFound(Friendship friendship) {
		toggleFollowing(true);
	}
	
	public void onFriendshipNotFound() {
		toggleFollowing(false);
	}
	
	public void toggleFollowing(boolean following) {
		if (following) {
			if (friendship == null) {
				friendship = new Friendship();
				friendship.setFollowingUserId(0 /*team id*/);
				friendship.setAllowNotify(true);
			}
			
			btnFollow.setBackgroundResource(R.drawable.button_primary_selector);
			btnFollow.setText(getString(R.string.button_following));
			btnFollow.setOnClickListener(updateFriendshipClickListener);
		} else {
			if (friendship != null) {
				friendship = null;
			}
			
			btnFollow.setBackgroundResource(R.drawable.button_transparent_selector);
			btnFollow.setText(getString(R.string.button_follow));
			btnFollow.setOnClickListener(followClickListener);
		}
	}
	
	public void toggleAllowNotify() {
		if (friendship != null) {
			boolean current = friendship.isAllowNotify();
			friendship.setAllowNotify(!current);
		}
	}
	
	private OnClickListener followClickListener = new ActivateOnlyClickListener() {
		
		@Override
		public void onActivated(View v, User user) {
			JustRequest request = new JustRequest(Method.POST, "friendships/" + "team_id", null, null);
			addRequest(request);
			toggleFollowing(true);
		}
	};
	
	private OnClickListener applyClickListener = new ActivateOnlyClickListener() {
		
		@Override
		public void onActivated(View v, User user) {
			
		}
	};
	
	private OnClickListener updateFriendshipClickListener = new ActivateOnlyClickListener() {
		
		@Override
		public void onActivated(View v, User user) {
			if (friendship != null) {
				Gson gson = Utility.getGsonInstance();
				Bundle bundle = new Bundle();
				bundle.putString(UpdateFriendshipDialog.EXTRA_FRIENDSHIP, gson.toJson(friendship));
				BaseDialog dialog = new UpdateFriendshipDialog();
				dialog.setArguments(bundle);
				dialog.show(getChildFragmentManager(), "");
			}
		}
	};
	
	private OnClickListener editTeamClickListener = new ActivateOnlyClickListener() {
		
		@Override
		public void onActivated(View v, User user) {
			Bundle bundle = new Bundle();
			bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, team.getName());
			
			Intent intent = new Intent(getActivity(), UpActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, TeamSettingFragment.class.getName());
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.hold);
		}
	};
	
	private OnClickListener editRoleClickListener = new ActivateOnlyClickListener() {
		
		@Override
		public void onActivated(View v, User user) {
			
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		setFadingActionBarTitle(team.getName());
	}

	@Override
	protected void defineTabs() {
		addTab(tvTeamMembers, new Tab(
				new UrlBuilder().s("songs").s(1257871).s("comments"), 
				new CommentAdapter()));
		
		addTab(tvTeamCollabos, new Tab(
				new UrlBuilder().s("songs").s(1257871).s("comments"), 
				new CommentAdapter()));
	}

}
