package com.myandb.singsong.event;

import com.myandb.singsong.model.User;
import com.myandb.singsong.secure.Authenticator;

import android.view.View;
import android.view.View.OnClickListener;

public abstract class MemberOnlyClickListener implements OnClickListener {
	
	@Override
	public void onClick(View v) {
		if (Authenticator.isLoggedIn()) {
			onLoggedIn(v, Authenticator.getUser());
		} else {
			onLoggedOut(v);
		}
	}
	
	public void onLoggedOut(View v) {
		// show login dialog
	}
	
	public abstract void onLoggedIn(View v, User user);

}
