package com.myandb.singsong.fragment;

import org.json.JSONArray;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.activity.UpActivity;
import com.myandb.singsong.adapter.FriendsAdapter;
import com.myandb.singsong.fragment.SearchFragment.SearchType;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.GradualLoader;
import com.myandb.singsong.net.GradualLoader.OnLoadCompleteListener;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.pager.InviteFriendsPagerAdapter;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.widget.LinearGridView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListAdapter;

public class FindFriendsFragment extends BaseFragment {
	
	private View vSearchUser;
	private View vFacebookFriends;
	private LinearGridView linearGridView;
	private FriendsAdapter adapter;
	private UrlBuilder builder;
	private GradualLoader loader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	protected int getResourceId() {
		return R.layout.fragment_find_friends;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		vSearchUser = view.findViewById(R.id.ll_search_user);
		vFacebookFriends = view.findViewById(R.id.ll_facebook_friends);
		linearGridView = (LinearGridView) view.findViewById(R.id.linear_grid_view);
	}

	@Override
	protected void initialize(Activity activity) {
		if (adapter == null) {
			adapter = new FriendsAdapter();
			builder = new UrlBuilder().s("users").s("recommendations");
			loader = new GradualLoader(activity);
			loader.setUrlBuilder(builder);
			loader.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				
				@Override
				public void onComplete(JSONArray response) {
					adapter.addAll(response);
					setAdapter(adapter);
				}
			});
		} else {
			setAdapter(adapter);
		}
	}
	
	private void setAdapter(ListAdapter adapter) {
		linearGridView.setShowDivider(true);
		linearGridView.setColumnCount(1);
		linearGridView.setAdapter(adapter);
	}
	
	@Override
	protected void onDataChanged() {}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		vSearchUser.setOnClickListener(searchUserClickListener);
		vFacebookFriends.setOnClickListener(facebookFriendsClickListener);
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
		bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, getString(R.string.fragment_community_title));
		Intent intent = new Intent(getActivity(), UpActivity.class);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, SettingFragment.class.getName());
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
		startFragment(intent);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (adapter.getCount() == 0 && !loader.isNothingToLoad()) {
			loader.load();
		}
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
