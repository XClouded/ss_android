package com.myandb.singsong.fragment;

import android.annotation.SuppressLint;
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
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.google.gson.Gson;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.activity.UpActivity;
import com.myandb.singsong.adapter.FriendsAdapter;
import com.myandb.singsong.adapter.MyCommentAdapter;
import com.myandb.singsong.adapter.MyLikeSongAdapter;
import com.myandb.singsong.adapter.MySongAdapter;
import com.myandb.singsong.dialog.BaseDialog;
import com.myandb.singsong.dialog.GalleryDialog;
import com.myandb.singsong.dialog.UpdateFriendshipDialog;
import com.myandb.singsong.event.ActivateOnlyClickListener;
import com.myandb.singsong.event.MemberOnlyClickListener;
import com.myandb.singsong.image.BlurAsyncTask;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Friendship;
import com.myandb.singsong.model.Profile;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JustRequest;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.net.JSONObjectSuccessListener;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.util.Utility;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class UserHomeFragment extends ListFragment {
	
	public static final String EXTRA_THIS_USER = "this_user";
	public static final int REQUEST_CODE_EDIT_PROFILE = 101; 
	
	private User thisUser;
	private User currentUser;
	private Friendship friendship;
	
	private ImageView ivUserPhoto;
	private ImageView ivUserPhotoBackground;
	private TextView tvNickname;
	private TextView tvUserStatus;
	private TextView tvUserSongs;
	private TextView tvUserFollowings;
	private TextView tvUserFollowers;
	private View tvResendEmail;
	private Button btnFollow;
	private Button btnEditProfile;

	@SuppressLint("InflateParams")
	@Override
	protected View inflateListHeaderView(LayoutInflater inflater) {
		return inflater.inflate(R.layout.fragment_user_home_header, null);
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		super.onArgumentsReceived(bundle);
		Gson gson = Utility.getGsonInstance();
		String userInJson = bundle.getString(EXTRA_THIS_USER);
		thisUser = gson.fromJson(userInJson, User.class);
		currentUser = Authenticator.getUser();
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		super.onViewInflated(view, inflater);
		
		ivUserPhoto = (ImageView) view.findViewById(R.id.iv_user_photo);
		ivUserPhotoBackground = (ImageView) view.findViewById(R.id.iv_user_photo_background);
		tvNickname = (TextView) view.findViewById(R.id.tv_user_nickname);
		tvUserStatus = (TextView) view.findViewById(R.id.tv_user_status);
		tvUserSongs = (TextView) view.findViewById(R.id.tv_user_songs);
		tvUserFollowings = (TextView) view.findViewById(R.id.tv_user_followings);
		tvUserFollowers = (TextView) view.findViewById(R.id.tv_user_followers);
		tvResendEmail = (TextView) view.findViewById(R.id.tv_resend_email);
		btnFollow = (Button) view.findViewById(R.id.btn_follow);
		btnEditProfile = (Button) view.findViewById(R.id.btn_edit_profile);
	}

	@Override
	protected void setupViews() {
		super.setupViews();
		
		setHasOptionsMenu(true);
		
		displayUserSpecificViews();

		setOnToListClickListener();
		
		setUserPhoto();
		
		loadProfileData();
		
		loadUserSong();
		
		if (!thisUser.isActivated()) {
			checkUserActivation();
		}
	}
	
	private void displayUserSpecificViews() {
		tvNickname.setText(thisUser.getNickname());
		
		if (isCurrentUser()) {
			btnEditProfile.setVisibility(View.VISIBLE);
			btnFollow.setVisibility(View.GONE);
		} else {
			btnEditProfile.setVisibility(View.GONE);
			btnFollow.setVisibility(View.VISIBLE);
		}
	}
	
	private void setOnToListClickListener() {
		tvUserFollowings.setOnClickListener(toUserItemListClickListener);
		tvUserFollowers.setOnClickListener(toUserItemListClickListener);
	}
	
	private OnClickListener toUserItemListClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			onToUserItemListClick(view.getId());
		}
	};
	
	private void onToUserItemListClick(int id) {
		String userId = String.valueOf(thisUser.getId());
		String segment = "users/" + userId + "/";
		String adapterName = "";
		String title = getCroppedNickname(thisUser.getNickname());
		title += "´ÔÀÇ ";
		Bundle params = new Bundle();
		
		switch (id) {
		case R.id.action_user_followings:
		case R.id.tv_user_followings:
			title += getString(R.string.following);
			segment += "followings";
			params.putString("req[]", "profile");
			params.putString("order", "friendships.created_at");
			adapterName = FriendsAdapter.class.getName();
			break;
			
		case R.id.action_user_followers:	
		case R.id.tv_user_followers:
			title += getString(R.string.follower);
			segment += "followers";
			params.putString("req[]", "profile");
			params.putString("order", "friendships.created_at");
			adapterName = FriendsAdapter.class.getName();
			break;
			
		case R.id.action_user_likings:
			title += getString(R.string.like);
			segment += "songs/likings";
			params.putString("order", "created_at");
			adapterName = MyLikeSongAdapter.class.getName();
			break;
			
		case R.id.action_user_comments:
			title += getString(R.string.comment);
			segment += "songs/comments";
			params.putString("order", "created_at");
			adapterName = MyCommentAdapter.class.getName();
			break;
			
		case R.id.action_user_trash:
			title += getString(R.string.trash);
			segment += "songs/trash";
			params.putString("order", "deleted_at");
			adapterName = MySongAdapter.class.getName();
			break;
			
		default:
			return;
		}
		
		Bundle bundle = new Bundle();
		bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, title);
		bundle.putString(ListFragment.EXTRA_URL_SEGMENT, segment);
		bundle.putString(ListFragment.EXTRA_ADAPTER_NAME, adapterName);
		bundle.putBundle(ListFragment.EXTRA_QUERY_PARAMS, params);
		Intent intent = new Intent(getActivity(), RootActivity.class);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, ListFragment.class.getName());
		startFragment(intent);
	}
	
	private String getCroppedNickname(String nickname) {
		String cropped = "";
		if (nickname.length() > 12) {
			cropped = nickname.substring(0, 6);
			cropped += "..";
			return cropped;
		} else {
			return nickname;
		}
	}
	
	private void setUserPhoto() {
		if (thisUser.hasPhoto()) {
			ImageHelper.displayPhoto(thisUser, ivUserPhoto, imageLoadingListener);
		}
	}
	
	private ImageLoadingListener imageLoadingListener = new ImageLoadingListener() {

		@Override
		public void onLoadingCancelled(String arg0, View arg1) {}

		@Override
		public void onLoadingComplete(String url, View imageView, Bitmap bitmap) {
			ivUserPhoto.setOnClickListener(photoZoomClickListener);
			setBackgroundBlurImage(bitmap);
		}

		@Override
		public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {}

		@Override
		public void onLoadingStarted(String arg0, View arg1) {}
		
	};
	
	private void setBackgroundBlurImage(Bitmap bitmap) {
		BlurAsyncTask blurTask = new BlurAsyncTask();
		blurTask.setImageView(ivUserPhotoBackground);
		blurTask.execute(bitmap);
	}
	
	private OnClickListener photoZoomClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Bundle bundle = new Bundle();
			bundle.putString(GalleryDialog.EXTRA_PHOTO_URL, thisUser.getPhotoUrl());
			GalleryDialog dialog = new GalleryDialog();
			dialog.setArguments(bundle);
			dialog.show(getChildFragmentManager(), "");
		}
	};
	
	private boolean isCurrentUser() {
		return thisUser.getId() == currentUser.getId();
	}
	
	private void loadProfileData() {
		String segment = "users/" + thisUser.getId() + "/profile";
		JSONObjectRequest request = new JSONObjectRequest(
				segment, null,
				new JSONObjectSuccessListener(this, "onGetProfileResponse", Profile.class),
				new JSONErrorListener(this, "onGetProfileError")
		);
		addRequest(request);
	}
	
	public void onGetProfileResponse(Profile profile) {
		updateProfile(profile);
		setupProfileRelatedViews(profile);
		
		if (isCurrentUser()) {
			btnEditProfile.setOnClickListener(editProfileClickListener);
		} else {
			checkIsThisUserFriend();
		}
	}
	
	public void onGetProfileError() {
		makeToast(R.string.t_poor_network_connection);
		getActivity().finish();
	}
	
	private void updateProfile(Profile profile) {
		if (isCurrentUser()) {
			new Authenticator().update(profile);
		}
	}
	
	private void setupProfileRelatedViews(Profile profile) {
		displayTextOnMainTab(tvUserSongs, profile.getWorkedSingNum(), "ºÎ¸¥ ³ë·¡");
		
		displayTextOnMainTab(tvUserFollowings, profile.getWorkedFollowingsNum(), "ÆÈ·ÎÀ×");
		
		displayTextOnMainTab(tvUserFollowers, profile.getWorkedFollowersNum(), "ÆÈ·Î¿ö");
		
		displayStatusMessage(tvUserStatus, profile.getStatusMessage());
	}
	
	private void displayTextOnMainTab(TextView textView, String num, String name) {
		SpannableString spannable = new SpannableString(name);
		spannable.setSpan(new RelativeSizeSpan(0.85f), 0, spannable.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		textView.setText(num);
		textView.append("\n");
		textView.append(spannable);
	}
	
	private void displayStatusMessage(TextView textView, String statusMessage) {
		if (statusMessage.isEmpty()) {
			if (isCurrentUser()) {
				textView.setText("\"");
				textView.append("»óÅÂ±ÛÀ» ÀÔ·ÂÇØÁÖ¼¼¿ä. :)");
				textView.append("\"");
			}
		} else {
			textView.setText("\"");
			textView.append(statusMessage);
			textView.append("\"");
		}
	}
	
	private void checkIsThisUserFriend() {
		JSONObjectRequest request = new JSONObjectRequest(
				"friendships/" + thisUser.getId(), null,
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
				friendship.setFollowingUserId(thisUser.getId());
				friendship.setAllowNotify(true);
			}
			
			btnFollow.setBackgroundResource(R.drawable.button_primary_selector);
			btnFollow.setText("ÆÈ·ÎÀ×");
			btnFollow.setOnClickListener(updateFriendshipClickListener);
		} else {
			if (friendship != null) {
				friendship = null;
			}
			
			btnFollow.setBackgroundResource(R.drawable.button_transparent_selector);
			btnFollow.setText("+ÆÈ·Î¿ì");
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
			JustRequest request = new JustRequest(Method.POST, "friendships/" + thisUser.getId(), null);
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
			bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, getString(R.string.fragment_setting_title));
			Intent intent = new Intent(getActivity(), UpActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, SettingFragment.class.getName());
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE);
			getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.hold);
		}
	};
	
	private void loadUserSong() {
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.s("users").s(thisUser.getId()).s("songs").s("all").p("order", "created_at");
		setUrlBuilder(urlBuilder);
		
		MySongAdapter adapter = new MySongAdapter(getActivity(), isCurrentUser(), false);
		setAdapter(adapter);
		load();
	}
	
	private void checkUserActivation() {
		JSONObjectRequest request = new JSONObjectRequest(
				"users/" + currentUser.getId(), null,
				new JSONObjectSuccessListener(this, "onCheckActivationResponse", User.class),
				new JSONErrorListener()
		);
		addRequest(request);
	}
	
	public void onCheckActivationResponse(User user) {
		if (user.isActivated()) {
			tvResendEmail.setVisibility(View.GONE);
			updateUser(user);
		} else {
			tvResendEmail.setVisibility(View.VISIBLE);
		}
	}
	
	private void updateUser(User user) {
		Authenticator auth = new Authenticator();
		auth.update(user);
		currentUser = user;
	}

	@Override
	protected void onDataChanged() {}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case REQUEST_CODE_EDIT_PROFILE:
			if (resultCode == Activity.RESULT_OK) {
				currentUser = thisUser = Authenticator.getUser();
				tvNickname.setText(currentUser.getNickname());
				setActionBarTitle(currentUser.getNickname());
				displayStatusMessage(tvUserStatus, currentUser.getProfile().getStatusMessage());
				setUserPhoto();
				
				if (getActivity() instanceof RootActivity) {
					((RootActivity) getActivity()).updateDrawer();
				}
			}
			
			break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		setFadingActionBarTitle(thisUser.getNickname());
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (isCurrentUser()) {
			inflater.inflate(R.menu.this_user_home, menu);
		} else {
			inflater.inflate(R.menu.user_home, menu);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		onToUserItemListClick(item.getItemId());
		return super.onOptionsItemSelected(item);
	}
	
}
