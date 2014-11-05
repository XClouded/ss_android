package com.myandb.singsong.event;

import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Request.Method;
import com.myandb.singsong.App;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.OAuthJsonObjectRequest;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Auth;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public abstract class MemberOnlyClickListener implements OnClickListener {
	
	private View view;

	@Override
	public void onClick(View v) {
		this.view = v;
		User currentUser = Auth.getUser();
		
		if (currentUser.isActivated()) {
			onActivated(v);
		} else {
			UrlBuilder urlBuilder = new UrlBuilder();
			String url = urlBuilder.s("users").s(currentUser.getId()).toString();
			
			OAuthJsonObjectRequest request = new OAuthJsonObjectRequest(
					Method.GET, url, null,
					new OnVolleyWeakResponse<MemberOnlyClickListener, JSONObject>(this, "onCheckActivationResponse", User.class),
					new OnVolleyWeakError<MemberOnlyClickListener>(this, "onCheckActivationError")
			);
			
			RequestQueue queue = ((App) v.getContext().getApplicationContext()).getQueueInstance();
			queue.add(request);
		}
	}
	
	public void onCheckActivationResponse(User user) {
		if (user.isActivated()) {
			Auth auth = new Auth();
			auth.update(user);
			
			onActivated(view);
		} else {
			onCheckActivationError();
		}
	}
	
	public void onCheckActivationError() {
		Toast.makeText(view.getContext(), "�̸��� ������ ���ּ���. ������ �Ͻø� �ݶ� �뷡���� ��� ����� �̿��Ͻ� �� �ֽ��ϴ�.", Toast.LENGTH_SHORT).show();
	}
	
	public abstract void onActivated(View v);

}
