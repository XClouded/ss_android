package com.myandb.singsong.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.Session.OpenRequest;
import com.facebook.model.GraphUser;
import com.google.gson.Gson;
import com.myandb.singsong.adapter.FriendsAdapter;
import com.myandb.singsong.model.FacebookUser;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JSONArrayRequest;
import com.myandb.singsong.net.JSONArraySuccessListener;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.Utility;

import android.app.Activity;
import android.content.Intent;

public class FacebookFriendsFragment extends ListFragment {
	
	private FriendsAdapter adapter;
	private Map<String, FacebookUser> facebookUserMap;

	@Override
	protected void initialize(Activity activity) {
		super.initialize(activity);
		
		if (getAdapter() == null) {
			adapter = new FriendsAdapter();
			setAdapter(adapter);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session session = Session.getActiveSession();
		if (session != null) {
			session.onActivityResult(getActivity(), requestCode, resultCode, data);
		}
	}

	@Override
	protected void onDataChanged() {
		super.onDataChanged();
		
		if (!isDataAlive()) {
			openFacebookSession();
		}
	}
	
	private boolean isDataAlive() {
		return adapter != null && adapter.getCount() > 0;
	}

	private void openFacebookSession() {
		OpenRequest request = new OpenRequest(this);
		request.setPermissions(Arrays.asList("email", "user_friends"));
		request.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
		
		Session session = Session.getActiveSession();
		if (session != null) {
			session.close();
		}
		session = new Session(getActivity().getApplicationContext());
		session.addCallback(statusCallback);
		session.openForRead(request);
		Session.setActiveSession(session);
	}
	
	private Session.StatusCallback statusCallback = new Session.StatusCallback() {
		
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			if (state.isOpened()) {
				Request.newMyFriendsRequest(session, friendsListCallback).executeAsync();
			}
		}
	};
	
	private GraphUserListCallback friendsListCallback = new GraphUserListCallback() {
		
		@Override
		public void onCompleted(List<GraphUser> users, Response response) {
			facebookUserMap = new HashMap<String, FacebookUser>();
			for (GraphUser user : users) {
				facebookUserMap.put(user.getId(), new FacebookUser(user));
			}
			requestUsersWithFacebookIds();
		}
	};
	
	private void requestUsersWithFacebookIds() {
		if (facebookUserMap == null || facebookUserMap.isEmpty()) {
			setListShown(true);
			return;
		}
		
		UrlBuilder builder = new UrlBuilder();
		builder.s("users").p("facebook_id", getFacebookIdChainString(facebookUserMap)).p("req", "profile");
		JSONArrayRequest request = new JSONArrayRequest(
				builder.build(),
				new JSONArraySuccessListener(this, "onGetUserSuccess"),
				null);
		addRequest(request);
	}
	
	private String getFacebookIdChainString(Map<String, FacebookUser> userMap) {
		String idChain = "";
		int index = 0;
		int size = userMap.size();
		for (Entry<String, FacebookUser> entry : userMap.entrySet()) {
			idChain += entry.getKey();
			if (index < size - 1) {
				idChain += "-";
			}
			index++;
		}
		return idChain;
	}
	
	public void onGetUserSuccess(JSONArray response) {
		List<User> users = makeUsersFromResponse(response);
		bindFacebookUsers(users);
		adapter.addAll(users);
		setListShown(true);
	}
	
	private List<User> makeUsersFromResponse(JSONArray response) {
		List<User> users = new ArrayList<User>();
		Gson gson = Utility.getGsonInstance();
		try {
			for (int i = 0, l = response.length(); i < l; i++) {
				JSONObject userJSON = response.getJSONObject(i);
				User user = gson.fromJson(userJSON.toString(), User.class);
				users.add(user);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return users;
	}
	
	private void bindFacebookUsers(List<User> users) {
		for (User user : users) {
			FacebookUser facebookUser = facebookUserMap.get(user.getFacebookId());
			user.setFacebookUser(facebookUser);
		}
	}

}
