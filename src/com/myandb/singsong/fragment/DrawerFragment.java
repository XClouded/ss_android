package com.myandb.singsong.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.activity.UpActivity;
import com.myandb.singsong.adapter.MenuAdapter;
import com.myandb.singsong.model.GlobalMenu;
import com.myandb.singsong.model.User;
import com.myandb.singsong.secure.Auth;
import com.myandb.singsong.util.ImageHelper;

public class DrawerFragment extends BaseFragment {
	
	private ImageView ivDrawerUserPhoto;
	private TextView tvDrawerUserNickname;
	private ListView lvMenu;

	@Override
	public boolean addToBackStack() {
		return false;
	}

	@Override
	protected int getResourceId() {
		return R.layout.fragment_drawer;
	}

	@Override
	protected void initiateChildViews(View parent) {
		ivDrawerUserPhoto = (ImageView) parent.findViewById(R.id.iv_drawer_user_photo);
		tvDrawerUserNickname = (TextView) parent.findViewById(R.id.tv_drawer_user_nickname);
		lvMenu = (ListView) parent.findViewById(R.id.lv_menu);
	}
	
	@Override
	protected void setupViews() {
		BaseAdapter adapter = new MenuAdapter(getActivity(), getMenuItems());
		lvMenu.setAdapter(adapter);
	}
	
	private List<GlobalMenu> getMenuItems() {
		List<GlobalMenu> menuItems = new ArrayList<GlobalMenu>();
		
		Intent collabo = new Intent(getActivity(), UpActivity.class);
		collabo.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, CollaboratedFragment.class.getName());
		collabo.putExtra(UpActivity.EXTRA_FULL_SCREEN, true);
		menuItems.add(new GlobalMenu(R.string.follower, collabo, R.drawable.ic_artist_menu));
		
		Intent waiting = new Intent(getActivity(), RootActivity.class);
		waiting.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, JoinFragment.class.getName());
		menuItems.add(new GlobalMenu(R.string.following, waiting, R.drawable.ic_collabo_menu));
		
		return menuItems;
	}
	
	@Override
	protected void onDataChanged() {
		User currentUser = Auth.getUser();
		if (currentUser != null) {
			ImageHelper.displayPhoto(currentUser, ivDrawerUserPhoto);
			tvDrawerUserNickname.setText(currentUser.getNickname());
		} else {
			// hide
		}
	}

}
