package com.myandb.singsong.event;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.fragment.ListFragment;
import com.myandb.singsong.fragment.UserHomeFragment;
import com.myandb.singsong.model.UserActivity;
import com.myandb.singsong.model.Notification;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.util.Utility;

public class Listeners {

	private Listeners() {}
	
	public static OnClickListener getSourceClickListener(final Context context, final Notification notification) {
		return new OnClickListener() {
			
			private UserActivity activity;
			private View view;
			
			@Override
			public void onClick(View v) {
				view = v;
				activity = notification.getActivity();
				
				if (activity.getSourceType() == UserActivity.TYPE_RECOMMEND_ARTIST) {
					((Activity) view.getContext()).finish();
				} else {
					JSONObjectRequest request = new JSONObjectRequest(
							getUrl(activity), null, null,
							successListener, errorListener
					);
					((App) context.getApplicationContext()).addShortLivedRequest(context, request);
				}
			}
			
			private Listener<JSONObject> successListener = new Listener<JSONObject>() {

				@Override
				public void onResponse(JSONObject response) {
					Intent intent = new Intent(context, RootActivity.class);
					
					switch(activity.getSourceType()) {
					case UserActivity.TYPE_CREATE_FRIENDSHIP:
						Bundle bundle = new Bundle();
						bundle.putString(UserHomeFragment.EXTRA_THIS_USER, response.toString());
						bundle.putBoolean(ListFragment.EXTRA_VERTICAL_PADDING, true);
						intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, UserHomeFragment.class.getName());
						intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
						((BaseActivity) view.getContext()).changePage(intent);
						break;
						
					case UserActivity.TYPE_CREATE_COMMENT:
					case UserActivity.TYPE_CREATE_LIKING:
					case UserActivity.TYPE_CREATE_ROOT_SONG:
					case UserActivity.TYPE_CREATE_LEAF_SONG:
						if (view != null) {
							Gson gson = Utility.getGsonInstance();
							Song song = gson.fromJson(response.toString(), Song.class);
							song.getPlayClickListener().onClick(view);
							intent.putExtra(RootActivity.EXTRA_SHOW_PLAYER, true);
							((BaseActivity) view.getContext()).changePage(intent);
						}
						break;
						
					default:
						return;
					}
					
				}
			};
			
			private ErrorListener errorListener = new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					switch(activity.getSourceType()) {
					case UserActivity.TYPE_CREATE_FRIENDSHIP:
						Toast.makeText(context, context.getString(R.string.t_alert_unknown_user), Toast.LENGTH_SHORT).show();
						break;
						
					case UserActivity.TYPE_CREATE_COMMENT:
					case UserActivity.TYPE_CREATE_LIKING:
					case UserActivity.TYPE_CREATE_ROOT_SONG:
					case UserActivity.TYPE_CREATE_LEAF_SONG:
						Toast.makeText(context, context.getString(R.string.t_alert_deleted_song), Toast.LENGTH_SHORT).show();
						break;
						
					default:
						break;
					}
				}
			};
			
			private String getUrl(UserActivity activity) {
				switch(activity.getSourceType()) {
				case UserActivity.TYPE_CREATE_FRIENDSHIP:
					return "users/" + activity.getUserId() + "?req[]=profile";
					
				case UserActivity.TYPE_CREATE_COMMENT:
				case UserActivity.TYPE_CREATE_LIKING:
					return "songs/" + activity.getParentId() + "?req[]=full";
				case UserActivity.TYPE_CREATE_ROOT_SONG:
				case UserActivity.TYPE_CREATE_LEAF_SONG:
					return "songs/" + activity.getSourceId() + "?req[]=full";
					
				default:
					return "";
				}
			}
		};
	}
	
}
