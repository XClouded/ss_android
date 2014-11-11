package com.myandb.singsong.fragment;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.MainActivity;
import com.myandb.singsong.activity.PhotoSlideActivity;
import com.myandb.singsong.activity.ProfileEditActivity;
import com.myandb.singsong.activity.SimpleListActivity;
import com.myandb.singsong.activity.SimpleListActivity.SimpleListType;
import com.myandb.singsong.adapter.MySongAdapter;
import com.myandb.singsong.dialog.UpdateFriendshipDialog;
import com.myandb.singsong.event.MemberOnlyClickListener;
import com.myandb.singsong.event.OnCompleteListener;
import com.myandb.singsong.event.OnVolleyWeakError;
import com.myandb.singsong.event.OnVolleyWeakResponse;
import com.myandb.singsong.file.FileManager;
import com.myandb.singsong.image.BitmapBuilder;
import com.myandb.singsong.model.Friendship;
import com.myandb.singsong.model.Model;
import com.myandb.singsong.model.Profile;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.OAuthJsonObjectRequest;
import com.myandb.singsong.net.OAuthJustRequest;
import com.myandb.singsong.net.UploadManager;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.util.ImageHelper.BlurAsyncTask;
import com.myandb.singsong.util.Utility;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.DiscCacheUtil;

public class ProfileRootFragment extends Fragment {
	
	public static final String INTENT_IS_EDIT_PHOTO = "_is_edit_photo_";
	public static final String INTENT_NICKNAME = "_nickname_";
	public static final String INTENT_PROFILE = "_profile_";
	public static final int R_CODE_EDIT_PROFILE = 101; 
	
	private User thisUser;
	private User currentUser;
	private Profile profile;
	private Friendship friendship;
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
	private ListView listView;
	private View vResendEmail;
	private Button btnResendEmail;
	private MySongAdapter adapter;
	private RequestQueue requestQueue;
	private UpdateFriendshipDialog dialog;
	private boolean isFollowingCurrentState;
	private boolean isCurrentUser;
	
	public void setUser(User thisUser) {
		this.thisUser = thisUser;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.common_listview, container, false);
		
		View header = inflater.inflate(R.layout.fragment_user_page_header, null);
		listView = (ListView) root.findViewById(R.id.lv_full_width);
		listView.addHeaderView(header);
		
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (thisUser != null) {
			requestQueue = ((App) getActivity().getApplicationContext()).getQueueInstance();
			currentUser = Authenticator.getUser();
			isCurrentUser = thisUser.getId() == currentUser.getId();
			isFollowingCurrentState = false;
			
			initializeView();
			
			loadUserPhoto();
			
			setupView();
			
			loadProfileData();
			
			loadUserSong();
			
			if (!currentUser.isActivated() && isCurrentUser) {
				checkUserActivation();
			}
		} else {
			getActivity().finish();
		}
	}
	
	private void initializeView() {
		View view = getView();
		
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
	
	private void setupView() {
		if (isCurrentUser) {
			tvUserComments.setVisibility(View.VISIBLE);
			ivUserTrashes.setVisibility(View.VISIBLE);
			
			tvUserComments.setOnClickListener(simpleListClickListener);
			ivUserTrashes.setOnClickListener(simpleListClickListener);
		} else {
			tvUserComments.setVisibility(View.INVISIBLE);
			ivUserTrashes.setVisibility(View.INVISIBLE);
		}
		
		tvUserFollowings.setOnClickListener(simpleListClickListener);
		tvUserFollowers.setOnClickListener(simpleListClickListener);
		tvUserLikings.setOnClickListener(simpleListClickListener);
	}
	
	private void loadProfileData() {
		UrlBuilder urlBuilder = new UrlBuilder();
		String url = urlBuilder.s("users").s(thisUser.getId()).s("profile").toString();
		
		OAuthJsonObjectRequest request = new OAuthJsonObjectRequest(
				Method.GET, url, null,
				new OnVolleyWeakResponse<ProfileRootFragment, JSONObject>(this, "onGetProfileResponse", Profile.class),
				new OnVolleyWeakError<ProfileRootFragment>(this, "onGetProfileError")
		);
		
		requestQueue.add(request);
	}
	
	public void onGetProfileResponse(Profile profile) {
		this.profile = profile;
		
		if (isCurrentUser) {
			currentUser.setProfile(profile);
			Authenticator auth = new Authenticator();
			auth.update(currentUser);
			
			onPreparationCompleted();
		} else {
			UrlBuilder urlBuilder = new UrlBuilder();
			String url = urlBuilder.s("friendships").s(thisUser.getId()).toString();
			
			OAuthJsonObjectRequest request = new OAuthJsonObjectRequest(
					Method.GET, url, null,
					new OnVolleyWeakResponse<ProfileRootFragment, JSONObject>(this, "onFriendshipFound", Friendship.class),
					new OnVolleyWeakError<ProfileRootFragment>(this, "onFriendshipNotFound")
			);
			
			requestQueue.add(request);
		}
	}
	
	public void onGetProfileError() {
		Toast.makeText(getActivity(), getString(R.string.t_poor_network_connection), Toast.LENGTH_SHORT).show();
		
		getActivity().finish();
	}
	
	public void onFriendshipFound(Friendship friendship) {
		this.friendship = friendship;
		this.isFollowingCurrentState = true;
		
		onPreparationCompleted();
	}
	
	public void onFriendshipNotFound() {
		this.isFollowingCurrentState = false;
		
		onPreparationCompleted();
	}
	
	private void onPreparationCompleted() {
		if (isCurrentUser) {
			btnEditProfile.setVisibility(View.VISIBLE);
			btnFollow.setVisibility(View.INVISIBLE);
			btnEditProfile.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), ProfileEditActivity.class);
					startActivityForResult(intent, R_CODE_EDIT_PROFILE);
				}
				
			});
		} else {
			btnEditProfile.setVisibility(View.INVISIBLE);
			btnFollow.setVisibility(View.VISIBLE);
			
			toggleFollowing(isFollowingCurrentState);
		}
		
		if (isCurrentUser) {
			tvUsername.setText(thisUser.getUsername());
		} else {
			tvUsername.setText(thisUser.getCroppedUsername());
		}
		
		tvNickname.setText(thisUser.getNickname());
		
		setTextOnMainTab(tvUserSongs, profile.getWorkedSingNum(), "부른 노래");
		
		setTextOnMainTab(tvUserFollowings, profile.getWorkedFollowingsNum(), "팔로잉");
		
		setTextOnMainTab(tvUserFollowers, profile.getWorkedFollowersNum(), "팔로워");
		
		setStatusMessage(tvUserStatus, profile.getStatusMessage());
	}
	
	private void setTextOnMainTab(TextView textView, String num, String name) {
		SpannableString spannable = new SpannableString(name);
		spannable.setSpan(new RelativeSizeSpan(0.85f), 0, spannable.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		textView.setText(num);
		textView.append("\n");
		textView.append(spannable);
	}
	
	private void setStatusMessage(TextView textView, String statusMessage) {
		if (statusMessage.isEmpty()) {
			if (isCurrentUser) {
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
	
	private void loadUserSong() {
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.s("users").s(thisUser.getId()).s("songs").s("all").p("order", "created_at");
		adapter = new MySongAdapter(getActivity(), urlBuilder, isCurrentUser, false);
		listView.setAdapter(adapter);
	}
	
	private void checkUserActivation() {
		UrlBuilder urlBuilder = new UrlBuilder();
		String url = urlBuilder.s("users").s(currentUser.getId()).toString();
		
		OAuthJsonObjectRequest request = new OAuthJsonObjectRequest(
				Method.GET, url, null,
				new OnVolleyWeakResponse<ProfileRootFragment, JSONObject>(this, "onCheckActivationResponse", User.class),
				new OnVolleyWeakError<ProfileRootFragment>(this, "onCheckActivationError")
		);
		
		requestQueue.add(request);
	}
	
	public void onCheckActivationResponse(User user) {
		if (user.isActivated()) {
			Authenticator auth = new Authenticator();
			auth.update(user);
			currentUser = user;
			
			vResendEmail.setVisibility(View.GONE);
		} else {
			vResendEmail.setVisibility(View.VISIBLE);
			
			btnResendEmail.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					UrlBuilder urlBuilder = new UrlBuilder();
					String url = urlBuilder.s("activations").toString();
					
					OAuthJustRequest request = new OAuthJustRequest(Method.POST, url, null);
					requestQueue.add(request);
					
					Toast.makeText(getActivity(), getString(R.string.t_activation_email_send), Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
	
	public void onCheckActivationError() {
		
	}
	
	public void toggleFollowing(boolean isFollowing) {
		isFollowingCurrentState = isFollowing;
		
		if (isFollowingCurrentState) {
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
	
	private OnClickListener followClickListener = new MemberOnlyClickListener() {
		
		@Override
		public void onActivated(View v) {
			UrlBuilder urlBuilder = new UrlBuilder();
			String url = urlBuilder.s("friendships").s(thisUser.getId()).toString();
			
			OAuthJustRequest request = new OAuthJustRequest(Method.POST, url, null);
			requestQueue.add(request);
			
			toggleFollowing(true);
		}
	};
	
	private OnClickListener updateFriendshipClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (dialog == null) {
				dialog = new UpdateFriendshipDialog(ProfileRootFragment.this);
			}
			
			dialog.setFriendship(friendship);
			dialog.show();
		}
	};
	
	private OnClickListener simpleListClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			Intent intent = new Intent(getActivity(), SimpleListActivity.class);
			intent.putExtra(SimpleListActivity.INTENT_USER, Utility.getGsonInstance().toJson(thisUser, User.class));
			
			switch (view.getId()) {
			case R.id.tv_user_followings:
				intent.putExtra(SimpleListActivity.INTENT_LIST_TYPE, SimpleListType.FOLLOWINGS);
				break;

			case R.id.tv_user_followers:
				intent.putExtra(SimpleListActivity.INTENT_LIST_TYPE, SimpleListType.FOLLOWERS);
				break;
				
			case R.id.tv_user_likings:
				intent.putExtra(SimpleListActivity.INTENT_LIST_TYPE, SimpleListType.LIKINGS);
				break;
				
			case R.id.tv_user_comments:
				intent.putExtra(SimpleListActivity.INTENT_LIST_TYPE, SimpleListType.COMMENTS);
				break;
			
			case R.id.iv_user_trashes:
				intent.putExtra(SimpleListActivity.INTENT_LIST_TYPE, SimpleListType.TRASHED);
				break;
				
			default:
				break;
				
			}
			
			startActivity(intent);
		}
	};
	
	private void loadUserPhoto() {
		if (isCurrentUser) {
			if (FileManager.isExist(FileManager.USER_PHOTO)) {
				setImageViewBitmap(FileManager.get(FileManager.USER_PHOTO));
			}
		} else if (thisUser.hasPhoto()) {
			ImageLoader imageLoader = ImageLoader.getInstance();
			File cache = DiscCacheUtil.findInCache(thisUser.getPhotoUrl(), imageLoader.getDiscCache());
			
			setImageViewBitmap(cache);
		}
	}
	
	private void setImageViewBitmap(File file) {
		if (file != null && file.exists()) {
			BitmapBuilder bitmapBuilder = new BitmapBuilder();
			
			if (ivUserPhoto != null) {
				Bitmap bitmap = bitmapBuilder
						.setSource(file)
						.setOutputSize(100)
						.enableCrop(false)
						.build();
				ivUserPhoto.setImageBitmap(bitmap);
				ivUserPhoto.setOnClickListener(zoomPhotoClickListener);
			}
			
			BlurAsyncTask blurTask = new BlurAsyncTask();
			blurTask.setImageView(ivProfileBg);
			blurTask.execute(file);
		}
	}
	
	private OnClickListener zoomPhotoClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getActivity(), PhotoSlideActivity.class);
			intent.putExtra(PhotoSlideActivity.INTENT_PHOTO_URL, thisUser.getPhotoUrl());
			startActivity(intent);
		}
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch (requestCode) {
		case R_CODE_EDIT_PROFILE:
			if (resultCode == Activity.RESULT_OK) {
				try {
					boolean hasPhotoChanged = data.getBooleanExtra(INTENT_IS_EDIT_PHOTO, false);
					String nickname = data.getStringExtra(INTENT_NICKNAME);
					String profileInJson = data.getStringExtra(INTENT_PROFILE);
					MainActivity home = (MainActivity) getActivity();
					currentUser = Authenticator.getUser();
					
					if (hasPhotoChanged) {
						UploadManager manager = new UploadManager();
						manager.start(
								getActivity(), FileManager.get(FileManager.TEMP),
								"user_photo", currentUser.getUsername() + Model.SUFFIX_JPG, "image/jpeg",
								photoUploadCompleteListener
						);
					}
					
					if (nickname != null) {
						currentUser.setNickname(nickname);
						
						tvNickname.setText(nickname);
						
						if (home != null) {
							home.updateNickname(nickname);
						}
						
						requestUpdateNickname(nickname);
					}
					
					if (profileInJson != null) {
						Gson gson = Utility.getGsonInstance();
						Profile profile = gson.fromJson(profileInJson, Profile.class);
						
						currentUser.setProfile(profile);
						
						setStatusMessage(tvUserStatus, profile.getStatusMessage());
						requestUpdateProfile(profileInJson);
					}
					
					Authenticator auth = new Authenticator();
					auth.update(currentUser);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			break;
		}
	}
	
	private OnCompleteListener photoUploadCompleteListener = new OnCompleteListener() {
		
		@Override
		public void done(Exception e) {
			if (getActivity() != null) {
				if (e == null) {
					try {
						String photoUrl = Model.STORAGE_HOST + Model.STORAGE_USER + currentUser.getUsername() + Model.SUFFIX_JPG; 
						JSONObject message = new JSONObject();
						message.put("main_photo_url", photoUrl);
						
						UrlBuilder urlBuilder = new UrlBuilder();
						String url = urlBuilder.s("users").toString();
						OAuthJsonObjectRequest request = new OAuthJsonObjectRequest(
								Method.PUT, url, message,
								new OnVolleyWeakResponse<ProfileRootFragment, JSONObject>(ProfileRootFragment.this, "onDataUploadSuccess", User.class),
								new OnVolleyWeakError<ProfileRootFragment>(ProfileRootFragment.this, "onUploadError")
								);
						
						RequestQueue queue = ((App) getActivity().getApplicationContext()).getQueueInstance();
						queue.add(request);
					} catch (JSONException e1) {
						onUploadError();
					}
				} else {
					onUploadError();
				}
			}
		}
	};
	
	public void onDataUploadSuccess(User user) {
		try {
			FileUtils.copyFile(FileManager.get(FileManager.TEMP), FileManager.get(FileManager.USER_PHOTO));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		currentUser.setPhotoUrl(user.getPhotoUrl(), user.getPhotoUpdatedAt());
		Authenticator auth = new Authenticator();
		auth.update(currentUser);
	}
	
	private void onUploadError() {
		Toast.makeText(getActivity(), getString(R.string.t_upload_failed), Toast.LENGTH_SHORT).show();
	}
	
	private void requestUpdateNickname(String nickname) {
		try {
			UrlBuilder urlBuilder = new UrlBuilder();
			String url = urlBuilder.s("users").toString();
			
			JSONObject message = new JSONObject();
			message.put("nickname", nickname);
			
			OAuthJustRequest request = new OAuthJustRequest(Method.PUT, url, message);
			requestQueue.add(request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void requestUpdateProfile(String profileInJson) {
		try {
			UrlBuilder urlBuilder = new UrlBuilder();
			String url = urlBuilder.s("profile").toString();
			JSONObject message = new JSONObject(profileInJson);
			
			OAuthJustRequest request = new OAuthJustRequest(Method.PUT, url, message);
			requestQueue.add(request);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (adapter != null) {
			adapter.onDestroy();
		}
		
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}
	
}
