package com.myandb.singsong.fragment;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.model.User;
import com.myandb.singsong.secure.Authenticator;

import android.annotation.SuppressLint;
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
	protected View inflateEmptyView(LayoutInflater inflater) {
		return null;
	}

	@Override
	protected View inflateListHeaderView(LayoutInflater inflater) {
		return null;
	}

	@SuppressLint("InflateParams")
	@Override
	protected View inflateFixedHeaderView(LayoutInflater inflater) {
		return inflater.inflate(R.layout.fragment_find_friends_fixed_header, null);
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		// Nothing to run
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		super.onViewInflated(view, inflater);
		btnSearchUser = (Button) view.findViewById(R.id.btn_search_user);
		btnConnectFacebook = (Button) view.findViewById(R.id.btn_connect_facebook);
		vFacebookHeaderContainer = view.findViewById(R.id.v_facebook_header_container);
	}

	@Override
	protected void setupViews() {
		super.setupViews();
		btnSearchUser.setOnClickListener(searchUserClickListener);
	}
	
	private OnClickListener searchUserClickListener = new OnClickListener() {
		
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

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.find_friends, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.invite_friends:
			Bundle bundle = new Bundle();
			bundle.putInt(ViewPagerFragment.EXTRA_ITEM_NUM, 0);
			Intent intent = new Intent(getActivity(), RootActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, InviteFriendsFragment.class.getName());
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			if (getActivity() instanceof BaseActivity) {
				((BaseActivity) getActivity()).changePage(intent);
			}
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		setActionBarTitle(R.string.fragment_find_friends_title);
	}

}
