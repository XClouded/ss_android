package com.myandb.singsong.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.adapter.MenuAdapter;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.GlobalMenu;
import com.myandb.singsong.model.User;
import com.myandb.singsong.secure.Authenticator;

public class DrawerFragment extends BaseFragment {
	
	private ImageView ivDrawerUserPhoto;
	private TextView tvDrawerUserNickname;
	private ListView lvMenu;
	private List<GlobalMenu> menus;

	@Override
	protected int getResourceId() {
		return R.layout.fragment_drawer;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		ivDrawerUserPhoto = (ImageView) view.findViewById(R.id.iv_drawer_user_photo);
		tvDrawerUserNickname = (TextView) view.findViewById(R.id.tv_drawer_user_nickname);
		lvMenu = (ListView) view.findViewById(R.id.lv_menu);
	}

	@Override
	protected void initialize(Bundle bundle) {
		makeMenus();
	}
	
	private void makeMenus() {
		menus = new ArrayList<GlobalMenu>();
		menus.add(makeHomeMenu());
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
		BaseAdapter adapter = new MenuAdapter(getActivity(), menus);
		lvMenu.setAdapter(adapter);
	}
	
	@Override
	protected void onDataChanged() {
		User currentUser = Authenticator.getUser();
		if (currentUser != null) {
			ImageHelper.displayPhoto(currentUser, ivDrawerUserPhoto);
			tvDrawerUserNickname.setText(currentUser.getNickname());
		} else {
			// Change user section
		}
	}

}
