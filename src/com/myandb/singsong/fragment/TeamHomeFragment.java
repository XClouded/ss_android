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
import com.myandb.singsong.event.MemberOnlyClickListener;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Friendship;
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

public class TeamHomeFragment extends ListFragment {
	
	private User currentUser;
	private Friendship friendship;
	
	private ImageView ivTeamBackgroundPhoto;
	private ImageView ivTeamEmblem;
	private ImageView ivEditProfile;
	private TextView tvTeamCollabos;
	private TextView tvTeamMembers;
	private TextView tvTeamFollowers;
	private TextView tvTeamTitle;
	private TextView tvTeamDescription;
	private Button btnFollow;

	@Override
	protected int getResourceId() {
		return R.layout.fragment_team_home;
	}

	@Override
	protected int getListHeaderViewResId() {
		return R.layout.fragment_team_home_header;
	}

	@Override
	protected ListAdapter instantiateAdapter(Activity activity) {
		return new CommentAdapter();
	}

	@Override
	protected UrlBuilder instantiateUrlBuilder(Activity activity) {
		return new UrlBuilder().s("songs").s(1189491).s("comments");
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		super.onViewInflated(view, inflater);
		
		view = getListHeaderView();
		ivTeamBackgroundPhoto = (ImageView) view.findViewById(R.id.iv_team_background_photo);
		ivTeamEmblem = (ImageView) view.findViewById(R.id.iv_team_emblem);
		ivEditProfile = (ImageView) view.findViewById(R.id.iv_edit_profile);
		tvTeamCollabos = (TextView) view.findViewById(R.id.tv_team_collabos);
		tvTeamMembers = (TextView) view.findViewById(R.id.tv_team_members);
		tvTeamFollowers = (TextView) view.findViewById(R.id.tv_team_followers);
		tvTeamTitle = (TextView) view.findViewById(R.id.tv_team_title);
		tvTeamDescription = (TextView) view.findViewById(R.id.tv_team_description);
		btnFollow = (Button) view.findViewById(R.id.btn_follow);
	}

	@Override
	protected void initialize(Activity activity) {
		super.initialize(activity);
		currentUser = Authenticator.getUser();
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		super.setupViews(savedInstanceState);
		
		displayTeamSpecificViews();

		displayTextOnMainTab(tvTeamCollabos, "5", "¶¼Ã¢");
		
		displayTextOnMainTab(tvTeamMembers, "4/7", "¸â¹ö");
		
		displayTextOnMainTab(tvTeamFollowers, "23", getString(R.string.follower));
		
		setOnToListClickListener();
		
		setTeamPhoto();
	}
	
	private boolean isTeamMember() {
		return true;
	}
	
	private boolean isTeamLeader() {
		return true;
	}
	
	private void displayTeamSpecificViews() {
		tvTeamTitle.setText("team title");
		
		if (isTeamMember()) {
			btnFollow.setVisibility(View.GONE);
		} else {
			btnFollow.setVisibility(View.VISIBLE);
		}
	}
	
	private void displayTextOnMainTab(TextView textView, String num, String name) {
		SpannableString spannable = new SpannableString(name);
		spannable.setSpan(new RelativeSizeSpan(0.85f), 0, spannable.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		textView.setText(num);
		textView.append("\n");
		textView.append(spannable);
	}
	
	private void setOnToListClickListener() {
		tvTeamCollabos.setOnClickListener(toUserItemListClickListener);
		tvTeamMembers.setOnClickListener(toUserItemListClickListener);
		tvTeamFollowers.setOnClickListener(toUserItemListClickListener);
	}
	
	private OnClickListener toUserItemListClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			onToUserItemListClick(view.getId());
		}
	};
	
	private void onToUserItemListClick(int id) {
		String userId = String.valueOf(0);
		String segment = "users/" + userId + "/";
		String adapterName = "";
		Bundle bundle = new Bundle();
		Bundle params = new Bundle();
		
		switch (id) {
		case R.id.action_user_followings:
		case R.id.tv_user_followings:
			segment += "followings";
			params.putString("req[]", "profile");
			params.putString("order", "friendships.created_at");
			adapterName = FriendsAdapter.class.getName();
			break;
			
		case R.id.action_user_followers:	
		case R.id.tv_user_followers:
			segment += "followers";
			params.putString("req[]", "profile");
			params.putString("order", "friendships.created_at");
			adapterName = FriendsAdapter.class.getName();
			break;
			
		case R.id.action_user_likings:
			segment += "songs/likings";
			params.putString("order", "created_at");
			adapterName = MyLikeSongAdapter.class.getName();
			bundle.putBoolean(ListFragment.EXTRA_HORIZONTAL_PADDING, true);
			bundle.putBoolean(ListFragment.EXTRA_VERTICAL_PADDING, true);
			break;
			
		case R.id.action_user_comments:
			segment += "songs/comments";
			params.putString("order", "created_at");
			adapterName = MyCommentAdapter.class.getName();
			bundle.putBoolean(ListFragment.EXTRA_HORIZONTAL_PADDING, true);
			bundle.putBoolean(ListFragment.EXTRA_VERTICAL_PADDING, true);
			break;
			
		case R.id.action_user_trash:
			segment += "songs/trash";
			params.putString("order", "deleted_at");
			adapterName = MySongAdapter.class.getName();
			bundle.putBoolean(ListFragment.EXTRA_VERTICAL_PADDING, true);
			break;
			
		default:
			return;
		}
		
		bundle.putString(ListFragment.EXTRA_URL_SEGMENT, segment);
		bundle.putString(ListFragment.EXTRA_ADAPTER_NAME, adapterName);
		bundle.putBundle(ListFragment.EXTRA_QUERY_PARAMS, params);
		Intent intent = new Intent(getActivity(), RootActivity.class);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, ListFragment.class.getName());
		startFragment(intent);
	}
	
	private void setTeamPhoto() {
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
	
	private OnClickListener editProfileClickListener = new MemberOnlyClickListener() {
		
		@Override
		public void onLoggedIn(View v, User user) {
			Bundle bundle = new Bundle();
			bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, getString(R.string.fragment_setting_action_title));
			Intent intent = new Intent(getActivity(), UpActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, SettingFragment.class.getName());
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.hold);
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		String title = "team fragment";
		setFadingActionBarTitle(title);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.user_home, menu);
		setFadingActionBarIcon(menu, R.id.action_overflow);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		onToUserItemListClick(item.getItemId());
		return super.onOptionsItemSelected(item);
	}

}
