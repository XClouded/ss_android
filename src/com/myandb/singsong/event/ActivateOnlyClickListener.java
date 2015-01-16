package com.myandb.singsong.event;

import com.android.volley.Request.Method;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.net.JSONObjectSuccessListener;
import com.myandb.singsong.secure.Authenticator;

import android.view.View;
import android.widget.Toast;

public abstract class ActivateOnlyClickListener extends MemberOnlyClickListener {

	private View view;

	@Override
	public void onLoggedIn(View v, User user) {
		this.view = v;
		
		if (user.isActivated()) {
			onActivated(v, user);
		} else {
			checkUserActivation(v, user);
		}
	}
	
	private void checkUserActivation(View view, User user) {
		int userId = user.getId();
		JSONObjectRequest request = new JSONObjectRequest(
				Method.GET, "users/" + userId, null,
				new JSONObjectSuccessListener(this, "onCheckActivationResponse", User.class),
				new JSONErrorListener(this, "onCheckActivationError")
		);
		App app = ((App) view.getContext().getApplicationContext());
		app.addShortLivedRequest(view.getContext(), request);
	}
	
	public void onCheckActivationResponse(User user) {
		if (user.isActivated()) {
			Authenticator auth = new Authenticator();
			auth.update(user);
			onActivated(view, user);
		} else {
			onCheckActivationError();
		}
	}
	
	public void onCheckActivationError() {
		Toast.makeText(view.getContext(), view.getContext().getString(R.string.t_notify_guest), Toast.LENGTH_SHORT).show();
	}
	
	public abstract void onActivated(View v, User user);

}
