package com.myandb.singsong.event;

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.gson.Gson;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.fragment.UserHomeFragment;
import com.myandb.singsong.model.Activity;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Notification;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.JSONObjectRequest;
import com.myandb.singsong.net.JSONErrorListener;
import com.myandb.singsong.net.JSONObjectSuccessListener;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.service.PlayerService;
import com.myandb.singsong.util.Utility;

public class Listeners {

	private Listeners() {}
	
	public static OnClickListener getSourceClickListener(final Context context, final Notification notification) {
		return new OnClickListener() {
			
			private Activity activity;
			
			@Override
			public void onClick(View v) {
				activity = notification.getActivity();
				
				if (activity.getSourceType() == Activity.TYPE_RECOMMEND_ARTIST) {
//					Intent intent = new Intent(context, ArtistActivity.class);
//					context.startActivity(intent);
				} else {
					JSONObjectRequest request = new JSONObjectRequest(
							getUrl(activity), null,
							new JSONObjectSuccessListener(this, "onSuccess"),
							new JSONErrorListener(this, "onFail")
					);
					
					((App) context.getApplicationContext()).addShortLivedRequest(context, request);
				}
			}
			
			public void onSuccess(JSONObject response) {
				Intent intent = new Intent();
				
				switch(activity.getSourceType()) {
				case Activity.TYPE_CREATE_FRIENDSHIP:
//				intent.setClass(context, ProfileRootActivity.class);
//				intent.putExtra(ProfileRootActivity.INTENT_USER, response.toString());
					break;
					
				case Activity.TYPE_CREATE_COMMENT:
				case Activity.TYPE_CREATE_LIKING:
				case Activity.TYPE_CREATE_ROOT_SONG:
				case Activity.TYPE_CREATE_LEAF_SONG:
					Gson gson = Utility.getGsonInstance();
					Song song = gson.fromJson(response.toString(), Song.class);
//				OldBaseActivity activity = (OldBaseActivity) context;
					PlayerService service = null;/*activity.getService();*/
					
					if (service != null) {
//					intent.setClass(context, PlayerActivity.class);
//					service.setSong(song);
						intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					}
					break;
					
				default:
					return;
				}
				
				context.startActivity(intent);
			}
			
			public void onFail() {
				switch(activity.getSourceType()) {
				case Activity.TYPE_CREATE_FRIENDSHIP:
					Toast.makeText(context, context.getString(R.string.t_unknown_user), Toast.LENGTH_SHORT).show();
					break;
					
				case Activity.TYPE_CREATE_COMMENT:
				case Activity.TYPE_CREATE_LIKING:
				case Activity.TYPE_CREATE_ROOT_SONG:
				case Activity.TYPE_CREATE_LEAF_SONG:
					Toast.makeText(context, context.getString(R.string.t_deleted_song), Toast.LENGTH_SHORT).show();
					break;
					
				default:
					break;
				}
			}
			
			private String getUrl(Activity activity) {
				switch(activity.getSourceType()) {
				case Activity.TYPE_CREATE_FRIENDSHIP:
					return "users/" + activity.getUserId() + "?req[]=profile";
					
				case Activity.TYPE_CREATE_COMMENT:
				case Activity.TYPE_CREATE_LIKING:
					return "songs" + activity.getParentId() + "?req[]=full";
				case Activity.TYPE_CREATE_ROOT_SONG:
				case Activity.TYPE_CREATE_LEAF_SONG:
					return "songs" + activity.getSourceId() + "?req[]=full";
					
				default:
					return "";
				}
			}
		};
	}
	
	public static OnClickListener getRecordClickListener(final Context context, final Music music) {
		return new ActivateOnlyClickListener() {
			
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@Override
			public void onActivated(View v, User user) {
				Gson gson = Utility.getGsonInstance();
				String musicInJson = gson.toJson(music, Music.class);
				
				Intent intent = null;/*new Intent(context, RecordMainActivity.class);*/
//				intent.putExtra(RecordMainActivity.INTENT_MUSIC, musicInJson);
				
				if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
				}
				
				context.startActivity(intent);
				
			}
		};
	}
	
}
