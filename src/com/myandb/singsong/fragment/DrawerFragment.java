package com.myandb.singsong.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.adapter.MenuAdapter;
import com.myandb.singsong.event.MemberOnlyClickListener;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.GlobalMenu;
import com.myandb.singsong.model.User;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.widget.LinearGridView;

public class DrawerFragment extends BaseFragment {
	
	private ImageView ivDrawerUserPhoto;
	private TextView tvDrawerUserNickname;
	private TextView tvDrawerUserUsername;
	private LinearGridView lgvMenu;
	private Button btnDrawerLogin;
	private View llDrawerUserWrapper;
	private View llDrawerLoginWrapper;
	private List<GlobalMenu> menus;

	@Override
	protected int getResourceId() {
		return R.layout.fragment_drawer;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		ivDrawerUserPhoto = (ImageView) view.findViewById(R.id.iv_drawer_user_photo);
		tvDrawerUserNickname = (TextView) view.findViewById(R.id.tv_drawer_user_nickname);
		tvDrawerUserUsername = (TextView) view.findViewById(R.id.tv_drawer_user_username);
		lgvMenu = (LinearGridView) view.findViewById(R.id.lgv_menu);
		btnDrawerLogin = (Button) view.findViewById(R.id.btn_drawer_login);
		llDrawerUserWrapper = view.findViewById(R.id.ll_drawer_user_wrapper);
		llDrawerLoginWrapper = view.findViewById(R.id.ll_drawer_login_wrapper);
	}

	@Override
	protected void initialize(Activity activity) {
		makeMenus();
	}
	
	private void makeMenus() {
		menus = new ArrayList<GlobalMenu>();
		menus.add(makeHomeMenu());
		menus.add(makeArtistMenu());
		menus.add(makeSingMenu());
		menus.add(makeListenMenu());
		menus.add(makeFindFriendMenu());
		menus.add(makeSettingMenu());
	}
	
	private GlobalMenu makeHomeMenu() {
		Intent intent = new Intent(getActivity(), RootActivity.class);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, HomeFragment.class.getName());
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_ROOT, true);
		return new GlobalMenu(R.string.fragment_home_title, intent, R.drawable.ic_menu_home);
	}
	
	private GlobalMenu makeArtistMenu() {
		Bundle bundle = new Bundle();
		bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, getString(R.string.fragment_artist_list_title));
		Intent intent = new Intent(getActivity(), RootActivity.class);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, ArtistListFragment.class.getName());
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_ROOT, true);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
		return new GlobalMenu(R.string.fragment_artist_list_title, intent, R.drawable.ic_menu_artist);
	}
	
	private GlobalMenu makeSingMenu() {
		Bundle bundle = new Bundle();
		bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, getString(R.string.fragment_sing_title));
		Intent intent = new Intent(getActivity(), RootActivity.class);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, MusicHomeFragment.class.getName());
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_ROOT, true);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
		return new GlobalMenu(R.string.fragment_sing_title, intent, R.drawable.ic_menu_sing, true);
	}
	
	private GlobalMenu makeListenMenu() {
		Bundle bundle = new Bundle();
		bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, getString(R.string.fragment_listen_title));
		Intent intent = new Intent(getActivity(), RootActivity.class);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, ListenHomeFragment.class.getName());
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_ROOT, true);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
		return new GlobalMenu(R.string.fragment_listen_title, intent, R.drawable.ic_menu_listen);
	}
	
	private GlobalMenu makeFindFriendMenu() {
		Bundle bundle = new Bundle();
		bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, getString(R.string.fragment_find_friends_title));
		Intent intent = new Intent(getActivity(), RootActivity.class);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, FindFriendsFragment.class.getName());
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_ROOT, true);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
		return new GlobalMenu(R.string.fragment_find_friends_title, intent, R.drawable.ic_menu_friend, true);
	}
	
	private GlobalMenu makeSettingMenu() {
		Intent intent = new Intent(getActivity(), RootActivity.class);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, SettingFragment.class.getName());
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_ROOT, true);
		return new GlobalMenu(R.string.fragment_setting_title, intent, R.drawable.ic_menu_setting);
	}
	
	@Override
	protected void setupViews(Bundle savedInstanceState) {
		MenuAdapter adapter = new MenuAdapter();
		adapter.addAll(menus);
		lgvMenu.setColumnCount(2);
		lgvMenu.setAdapter(adapter);
	}
	
	@Override
	protected void onDataChanged() {
		User currentUser = Authenticator.getUser();
		if (currentUser != null) {
			llDrawerUserWrapper.setVisibility(View.VISIBLE);
			llDrawerLoginWrapper.setVisibility(View.GONE);
			ImageHelper.displayPhoto(currentUser, ivDrawerUserPhoto);
			tvDrawerUserNickname.setText(currentUser.getNickname());
			tvDrawerUserUsername.setText(currentUser.getUsername());
			llDrawerUserWrapper.setOnClickListener(myProfileClickListener);
		} else {
			llDrawerUserWrapper.setVisibility(View.GONE);
			llDrawerLoginWrapper.setVisibility(View.VISIBLE);
			btnDrawerLogin.setOnClickListener(myProfileClickListener);
		}
	}
	
	private OnClickListener myProfileClickListener = new MemberOnlyClickListener() {
		
		@Override
		public void onLoggedIn(View v, User user) {
			String userInJson = Authenticator.getUserInJson();
			Bundle bundle = new Bundle();
			bundle.putString(UserHomeFragment.EXTRA_THIS_USER, userInJson);
			
			Intent intent = new Intent(getActivity(), RootActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, UserHomeFragment.class.getName());
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			startFragment(intent);
		}
	};

}
