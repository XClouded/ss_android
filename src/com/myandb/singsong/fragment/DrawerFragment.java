package com.myandb.singsong.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.adapter.MenuAdapter;
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
	private List<GlobalMenu> menus;

	@Override
	protected int getResourceId() {
		return R.layout.fragment_drawer;
	}
	
	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		// Nothing to run
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		ivDrawerUserPhoto = (ImageView) view.findViewById(R.id.iv_drawer_user_photo);
		tvDrawerUserNickname = (TextView) view.findViewById(R.id.tv_drawer_user_nickname);
		tvDrawerUserUsername = (TextView) view.findViewById(R.id.tv_drawer_user_username);
		lgvMenu = (LinearGridView) view.findViewById(R.id.lgv_menu);
	}

	@Override
	protected void initialize(Activity activity) {
		makeMenus();
	}
	
	private void makeMenus() {
		menus = new ArrayList<GlobalMenu>();
		menus.add(makeHomeMenu());
		menus.add(makeArtistMenu());
		menus.add(makeArtistMenu());
		menus.add(makeArtistMenu());
		menus.add(makeArtistMenu());
		menus.add(makeArtistMenu());
	}
	
	private GlobalMenu makeHomeMenu() {
		Intent intent = new Intent(getActivity(), RootActivity.class);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, WaitingFragment.class.getName());
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_ROOT, true);
		return new GlobalMenu(R.string.follower, intent, R.drawable.ic_artist_menu);
	}
	
	private GlobalMenu makeArtistMenu() {
		Intent intent = new Intent(getActivity(), RootActivity.class);
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, LegendFragment.class.getName());
		intent.putExtra(BaseActivity.EXTRA_FRAGMENT_ROOT, true);
		return new GlobalMenu(R.string.following, intent, R.drawable.ic_collabo_menu);
	}
	
	@Override
	protected void setupViews() {
		MenuAdapter adapter = new MenuAdapter();
		adapter.addAll(menus);
		lgvMenu.setColumnCount(2);
		lgvMenu.setAdapter(adapter);
	}
	
	@Override
	protected void onDataChanged() {
		User currentUser = Authenticator.getUser();
		if (currentUser != null) {
			ImageHelper.displayPhoto(currentUser, ivDrawerUserPhoto);
			tvDrawerUserNickname.setText(currentUser.getNickname());
			tvDrawerUserUsername.setText(currentUser.getUsername());
		} else {
			// Change user section
		}
	}

}
