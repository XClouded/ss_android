package com.myandb.singsong.fragment;

import java.util.List;

import org.json.JSONObject;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.google.gson.Gson;
import com.myandb.singsong.adapter.TaggableFriendAdapter;
import com.myandb.singsong.model.TaggableFriend;
import com.myandb.singsong.model.TaggableFriendsWrapper;
import com.myandb.singsong.util.Utility;

public class InviteFacebookFragment extends ListFragment {

	@Override
	protected void onDataChanged() {
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			Request request = new Request(session, "/me/taggable_friends", null, HttpMethod.GET, getTaggableFriendsCallback);
			request.executeAsync();
		}
	}
	
	private Request.Callback getTaggableFriendsCallback = new Request.Callback() {
		
		@Override
		public void onCompleted(Response response) {
			TaggableFriendsWrapper wrapper = convertResponseToWrapper(response);
			if (wrapper != null) {
				List<TaggableFriend> friends = wrapper.getData();
				TaggableFriendAdapter adapter = new TaggableFriendAdapter();
				adapter.addAll(friends);
				getListView().setAdapter(adapter);
				setListShown(true);
			}
		}
	};
	
	private TaggableFriendsWrapper convertResponseToWrapper(Response response) {
		GraphObject graphObject = response.getGraphObject();
		if (graphObject != null) {
			JSONObject jsonObject = graphObject.getInnerJSONObject();
			String taggableFriendsJson = jsonObject.toString();
			Gson gson = Utility.getGsonInstance();
			return gson.fromJson(taggableFriendsJson, TaggableFriendsWrapper.class);
		} else {
			return null;
		}
	}

}
