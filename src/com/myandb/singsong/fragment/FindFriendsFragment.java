package com.myandb.singsong.fragment;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.model.User;
import com.myandb.singsong.pager.InviteFriendsPagerAdapter;
import com.myandb.singsong.secure.Authenticator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FindFriendsFragment extends ListFragment {
	
	private Button btnSearchUser;
	private Button btnInviteFriends;
	private Button btnConnectFacebook;
	private View vFacebookHeaderContainer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@SuppressLint("InflateParams")
	@Override
	protected View inflateFixedHeaderView(LayoutInflater inflater) {
		return inflater.inflate(R.layout.fragment_find_friends_fixed_header, null);
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		super.onViewInflated(view, inflater);
		btnSearchUser = (Button) view.findViewById(R.id.btn_search_user);
		btnInviteFriends = (Button) view.findViewById(R.id.btn_invite_friends);
		btnConnectFacebook = (Button) view.findViewById(R.id.btn_connect_facebook);
		vFacebookHeaderContainer = view.findViewById(R.id.v_facebook_header_container);
	}

	@Override
	protected void setupViews() {
		super.setupViews();
		btnSearchUser.setOnClickListener(searchUserClickListener);
		btnInviteFriends.setOnClickListener(inviteFriendsClickListener);
		btnConnectFacebook.setOnClickListener(connectFacebookClickListener);
	}
	
	private OnClickListener searchUserClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
		}
	};
	
	private OnClickListener inviteFriendsClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Bundle bundle = new Bundle();
			bundle.putInt(FragmentPagerFragment.EXTRA_ITEM_NUM, 0);
			bundle.putString(FragmentPagerFragment.EXTRA_PAGER_ADAPTER, InviteFriendsPagerAdapter.class.getName());
			bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, getString(R.string.fragment_invite_friends_title));
			Intent intent = new Intent(getActivity(), RootActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, FragmentPagerFragment.class.getName());
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
