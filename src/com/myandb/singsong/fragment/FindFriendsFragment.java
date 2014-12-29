package com.myandb.singsong.fragment;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.activity.UpActivity;
import com.myandb.singsong.adapter.FriendsAdapter;
import com.myandb.singsong.fragment.SearchFragment.SearchType;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.pager.InviteFriendsPagerAdapter;
import com.myandb.singsong.secure.Authenticator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListAdapter;

public class FindFriendsFragment extends ListFragment {
	
	private Button btnSearchUser;
	private Button btnFacebookFriends;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	protected int getFixedHeaderViewResId() {
		return R.layout.fragment_find_friends_fixed_header;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		super.onViewInflated(view, inflater);
		
		view = getFixedHeaderView();
		btnSearchUser = (Button) view.findViewById(R.id.btn_search_user);
		btnFacebookFriends = (Button) view.findViewById(R.id.btn_facebook_friends);
	}

	@Override
	protected ListAdapter instantiateAdapter(Activity activity) {
		return new FriendsAdapter();
	}

	@Override
	protected UrlBuilder instantiateUrlBuilder(Activity activity) {
		return new UrlBuilder().s("users").s("recommendations");
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		super.setupViews(savedInstanceState);
		btnSearchUser.setOnClickListener(searchUserClickListener);
		btnFacebookFriends.setOnClickListener(facebookFriendsClickListener);
	}
	
	private OnClickListener searchUserClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			startUserSearchFragment();
		}
	};
	
	private void startUserSearchFragment() {
		Bundle bundle = new Bundle();
		bundle.putSerializable(SearchFragment.EXTRA_SEARCH_TYPE, SearchType.USER);
		bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, "유저 검색");
		Intent intent = new Intent(getActivity(), RootActivity.class);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, SearchFragment.class.getName());
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
		startFragment(intent);
	}
	
	private OnClickListener facebookFriendsClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			User user = Authenticator.getUser();
			if (user.isFacebookActivated()) {
				startFacebookFriendsFragment();
			} else {
				startUserProfileFragment();
			}
		}
	};
	
	private void startFacebookFriendsFragment() {
		Bundle bundle = new Bundle();
		bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, "페이스북 친구 추가");
		Intent intent = new Intent(getActivity(), RootActivity.class);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, FacebookFriendsFragment.class.getName());
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
		startFragment(intent);
	}
	
	private void startUserProfileFragment() {
		Bundle bundle = new Bundle();
		bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, getString(R.string.fragment_setting_title));
		Intent intent = new Intent(getActivity(), UpActivity.class);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, SettingFragment.class.getName());
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
		startFragment(intent);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.find_friends, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_invite_friends:
			startInviteFriendsFragment();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void startInviteFriendsFragment() {
		Bundle bundle = new Bundle();
		bundle.putInt(FragmentPagerFragment.EXTRA_ITEM_NUM, 0);
		bundle.putString(FragmentPagerFragment.EXTRA_PAGER_ADAPTER, InviteFriendsPagerAdapter.class.getName());
		bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, getString(R.string.fragment_invite_friends_title));
		Intent intent = new Intent(getActivity(), RootActivity.class);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, FragmentPagerFragment.class.getName());
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
		startFragment(intent);
	}

}
