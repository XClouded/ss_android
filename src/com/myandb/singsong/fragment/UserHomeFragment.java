package com.myandb.singsong.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
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
import com.myandb.singsong.adapter.MySongAdapter;
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
	private UpdateFriendshipDialog dialog;
	
	private ImageView ivUserPhoto;
	private ImageView ivProfileBg;
	private TextView tvUsername;
	private TextView tvNickname;
	private TextView tvUserStatus;
	private TextView tvUserSongs;
	private TextView tvUserFollowings;
	private TextView tvUserFollowers;
	private TextView tvUserLikings;
	private TextView tvUserComments;
	private ImageView ivUserTrashes;
	private Button btnFollow;
	private Button btnEditProfile;
	private View vResendEmail;
	private Button btnResendEmail;

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
		ivProfileBg = (ImageView) view.findViewById(R.id.iv_profile_bg);
		tvUsername = (TextView) view.findViewById(R.id.tv_user_username);
		tvNickname = (TextView) view.findViewById(R.id.tv_user_nickname);
		tvUserStatus = (TextView) view.findViewById(R.id.tv_user_status);
		tvUserSongs = (TextView) view.findViewById(R.id.tv_user_songs);
		tvUserFollowings = (TextView) view.findViewById(R.id.tv_user_followings);
		tvUserFollowers = (TextView) view.findViewById(R.id.tv_user_followers);
		tvUserLikings = (TextView) view.findViewById(R.id.tv_user_likings);
		tvUserComments = (TextView) view.findViewById(R.id.tv_user_comments);
		ivUserTrashes = (ImageView) view.findViewById(R.id.iv_user_trashes);
		btnFollow = (Button) view.findViewById(R.id.btn_follow);
		btnEditProfile = (Button) view.findViewById(R.id.btn_edit_profile);
		vResendEmail = view.findViewById(R.id.rl_resend_email);
		btnResendEmail = (Button) view.findViewById(R.id.btn_resend_email);
	}

	@Override
	protected void setupViews() {
		super.setupViews();
		
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
			tvUserComments.setVisibility(View.VISIBLE);
			ivUserTrashes.setVisibility(View.VISIBLE);
			btnEditProfile.setVisibility(View.VISIBLE);
			btnFollow.setVisibility(View.GONE);
			tvUsername.setText(thisUser.getUsername());
		} else {
			tvUserComments.setVisibility(View.GONE);
			ivUserTrashes.setVisibility(View.GONE);
			btnEditProfile.setVisibility(View.GONE);
			btnFollow.setVisibility(View.VISIBLE);
			tvUsername.setText(thisUser.getCroppedUsername());
		}
	}
	
	private void setOnToListClickListener() {
		if (isCurrentUser()) {
			tvUserComments.setOnClickListener(toListClickListener);
			ivUserTrashes.setOnClickListener(toListClickListener);
		}
		
		tvUserFollowings.setOnClickListener(toListClickListener);
		tvUserFollowers.setOnClickListener(toListClickListener);
		tvUserLikings.setOnClickListener(toListClickListener);
	}
	
	private OnClickListener toListClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			Gson gson = Utility.getGsonInstance();
			Bundle bundle = new Bundle();
			bundle.putString(EXTRA_THIS_USER, gson.toJson(thisUser, User.class));
			Intent intent = new Intent(getActivity(), RootActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			
			switch (view.getId()) {
			case R.id.tv_user_followings:
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, "");
				break;

			case R.id.tv_user_followers:
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, "");
				break;
				
			case R.id.tv_user_likings:
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, "");
				break;
				
			case R.id.tv_user_comments:
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, "");
				break;
			
			case R.id.iv_user_trashes:
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, "");
				break;
				
			default:
				break;
				
			}
			
			startFragment(intent);
		}
	};
	
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
		blurTask.setImageView(ivProfileBg);
		blurTask.execute(bitmap);
	}
	
	private OnClickListener photoZoomClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Bundle bundle = new Bundle();
			bundle.putString("", thisUser.getPhotoUrl());
			Intent intent = new Intent(getActivity(), UpActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, "");
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			startActivity(intent);
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
		displayTextOnMainTab(tvUserSongs, profile.getWorkedSingNum(), "부른 노래");
		
		displayTextOnMainTab(tvUserFollowings, profile.getWorkedFollowingsNum(), "팔로잉");
		
		displayTextOnMainTab(tvUserFollowers, profile.getWorkedFollowersNum(), "팔로워");
		
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
				textView.append("상태글을 입력해주세요. :)");
				textView.append("\"");
			} else {
				textView.setVisibility(View.GONE);
			}
		} else {
			if (statusMessage.length() > 20) {
				try {
					statusMessage = new StringBuilder(statusMessage).insert(20, "\n").toString();
				} catch (StringIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
			
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
			
			btnFollow.setBackgroundResource(R.drawable.img_following_arrow);
			btnFollow.setOnClickListener(updateFriendshipClickListener);
		} else {
			if (friendship != null) {
				friendship = null;
			}
			
			btnFollow.setBackgroundResource(R.drawable.img_follow);
			btnFollow.setOnClickListener(followClickListener);
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
			if (dialog == null) {
				dialog = new UpdateFriendshipDialog();
			}
//			dialog.setFriendship(friendship);
			dialog.show(getChildFragmentManager(), "");
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
			vResendEmail.setVisibility(View.GONE);
			updateUser(user);
		} else {
			vResendEmail.setVisibility(View.VISIBLE);
			btnResendEmail.setOnClickListener(sendEmailClickListener);
		}
	}
	
	private void updateUser(User user) {
		Authenticator auth = new Authenticator();
		auth.update(user);
		currentUser = user;
	}
	
	private OnClickListener sendEmailClickListener = new MemberOnlyClickListener() {
		
		@Override
		public void onLoggedIn(View v, User user) {
			JustRequest request = new JustRequest(Method.POST, "activations", null);
			addRequest(request);
			makeToast(R.string.t_activation_email_send);
			vResendEmail.setVisibility(View.GONE);
		}
	};

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
		setActionBarTitle(thisUser.getNickname());
	}
	
}
