package com.myandb.singsong.fragment;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.fragment.SearchFragment.SearchType;
import com.myandb.singsong.model.User;
import com.myandb.singsong.pager.InviteFriendsPagerAdapter;
import com.myandb.singsong.secure.Authenticator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FindFriendsFragment extends ListFragment {
	
	private Button btnSearchUser;
	private Button btnConnectFacebook;
	private View vFacebookHeaderContainer;

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
		btnConnectFacebook = (Button) view.findViewById(R.id.btn_connect_facebook);
		vFacebookHeaderContainer = view.findViewById(R.id.v_facebook_header_container);
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		super.setupViews(savedInstanceState);
		btnSearchUser.setOnClickListener(searchUserClickListener);
		btnConnectFacebook.setOnClickListener(connectFacebookClickListener);
	}
	
	private OnClickListener searchUserClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Bundle bundle = new Bundle();
			bundle.putSerializable(SearchFragment.EXTRA_SEARCH_TYPE, SearchType.USER);
			bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, "���� �˻�");
			Intent intent = new Intent(getActivity(), RootActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, SearchFragment.class.getName());
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			startFragment(intent);
		}
	};
	
	private OnClickListener connectFacebookClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
		}
	};

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.find_friends, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_invite_friends:
			Bundle bundle = new Bundle();
			bundle.putInt(FragmentPagerFragment.EXTRA_ITEM_NUM, 0);
			bundle.putString(FragmentPagerFragment.EXTRA_PAGER_ADAPTER, InviteFriendsPagerAdapter.class.getName());
			bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, getString(R.string.fragment_invite_friends_title));
			Intent intent = new Intent(getActivity(), RootActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, FragmentPagerFragment.class.getName());
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			startFragment(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onDataChanged() {
		User user = Authenticator.getUser();
		if (user.isFacebookActivated()) {
			btnConnectFacebook.setVisibility(View.GONE);
			vFacebookHeaderContainer.setVisibility(View.VISIBLE);
			// get facebook friend
		} else {
			btnConnectFacebook.setVisibility(View.VISIBLE);
			vFacebookHeaderContainer.setVisibility(View.GONE);
			setListShown(true);
			// set onclick listener
		}
	}

}
