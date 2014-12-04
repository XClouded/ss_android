package com.myandb.singsong.event;

import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Request.Method;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.UrlBuilder;
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
			App app = ((App) v.getContext().getApplicationContext());
			checkUserActivation(app.getQueueInstance(), user);
		}
	}
	
	private void checkUserActivation(RequestQueue queue, User user) {
		UrlBuilder urlBuilder = new UrlBuilder();
		String url = urlBuilder.s("users").s(user.getId()).toString();
		
		JSONObjectRequest request = new JSONObjectRequest(
				Method.GET, url, null,
				new OnVolleyWeakResponse<ActivateOnlyClickListener, JSONObject>(this, "onCheckActivationResponse", User.class),
				new OnVolleyWeakError<ActivateOnlyClickListener>(this, "onCheckActivationError")
		);
		queue.add(request);
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
		Toast.makeText(view.getContext(), view.getContext().getString(R.string.t_guest), Toast.LENGTH_SHORT).show();
	}
	
	public abstract void onActivated(View v, User user);

}
