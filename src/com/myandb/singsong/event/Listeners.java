package com.myandb.singsong.event;

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.ArtistActivity;
import com.myandb.singsong.activity.ChildSongActivity;
import com.myandb.singsong.activity.OldBaseActivity;
import com.myandb.singsong.activity.PlayerActivity;
import com.myandb.singsong.activity.ProfileRootActivity;
import com.myandb.singsong.activity.RecordMainActivity;
import com.myandb.singsong.model.Activity;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Notification;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.OAuthJsonObjectRequest;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.service.PlayerService;
import com.myandb.singsong.util.Utility;

public class Listeners {

	private Listeners() {}
	
	public static OnClickListener getSourceClickListener(final Context context, final Notification notification) {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Activity activity = notification.getActivity();
				
				if (activity.getSourceType() == Activity.TYPE_RECOMMEND_ARTIST) {
					Intent intent = new Intent(context, ArtistActivity.class);
					context.startActivity(intent);
				} else {
					String url = getUrl(activity);
					
					OAuthJsonObjectRequest request = new OAuthJsonObjectRequest(
							Method.GET, url, null,
							new OnFetchResponse(context, activity.getSourceType()),
							new OnFetchError(context, activity.getSourceType())
							);
					
					RequestQueue queue = ((App) context.getApplicationContext()).getQueueInstance();
					queue.add(request); 
				}
			}
			
			private String getUrl(Activity activity) {
				UrlBuilder urlBuilder = new UrlBuilder();
				
				switch(activity.getSourceType()) {
				case Activity.TYPE_CREATE_FRIENDSHIP:
					return urlBuilder.s("users").s(activity.getUserId()).p("req[]", "profile").toString();
					
				case Activity.TYPE_CREATE_COMMENT:
				case Activity.TYPE_CREATE_LIKING:
					return urlBuilder.s("songs").s(activity.getParentId()).p("req[]", "full").toString();
				case Activity.TYPE_CREATE_ROOT_SONG:
				case Activity.TYPE_CREATE_LEAF_SONG:
					return urlBuilder.s("songs").s(activity.getSourceId()).p("req[]", "full").toString();
					
				default:
					return "";
				}
			}
		};
	}
	
	private static class OnFetchResponse extends OnVolleyWeakResponse<Context, JSONObject> {
		
		private int sourceType;
		
		public OnFetchResponse(Context context, int sourceType) {
			super(context);
			
			this.sourceType = sourceType;
		}

		@Override
		protected void onFilteredResponse(Context context, JSONObject response) {
			Intent intent = new Intent();
			
			switch(sourceType) {
			case Activity.TYPE_CREATE_FRIENDSHIP:
				intent.setClass(context, ProfileRootActivity.class);
				intent.putExtra(ProfileRootActivity.INTENT_USER, response.toString());
				break;
				
			case Activity.TYPE_CREATE_COMMENT:
			case Activity.TYPE_CREATE_LIKING:
			case Activity.TYPE_CREATE_ROOT_SONG:
			case Activity.TYPE_CREATE_LEAF_SONG:
				Gson gson = Utility.getGsonInstance();
				Song song = gson.fromJson(response.toString(), Song.class);
				OldBaseActivity activity = (OldBaseActivity) context;
				PlayerService service = activity.getService();
				
				if (service != null) {
					intent.setClass(context, PlayerActivity.class);
					service.setSong(song);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				}
				break;
				
			default:
				return;
			}
			
			context.startActivity(intent);
		}
		
	}
	
	private static class OnFetchError extends OnVolleyWeakError<Context> {
		
		private int sourceType;

		public OnFetchError(Context context, int sourceType) {
			super(context);
			
			this.sourceType = sourceType;
		}

		@Override
		public void onFilteredResponse(Context reference, VolleyError error) {
			switch(sourceType) {
			case Activity.TYPE_CREATE_FRIENDSHIP:
				Toast.makeText(reference, reference.getString(R.string.t_unknown_user), Toast.LENGTH_SHORT).show();
				break;
				
			case Activity.TYPE_CREATE_COMMENT:
			case Activity.TYPE_CREATE_LIKING:
			case Activity.TYPE_CREATE_ROOT_SONG:
			case Activity.TYPE_CREATE_LEAF_SONG:
				Toast.makeText(reference, reference.getString(R.string.t_deleted_song), Toast.LENGTH_SHORT).show();
				break;
				
			default:
				break;
			}
		}
		
	}
	
	public static OnClickListener getChildrenClickListener(final Context context, final Song song) {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Gson gson = Utility.getGsonInstance();
				Song parentSong = song.getParentSong() == null ? song : song.getParentSong();
				parentSong.setMusic(song.getMusic());
				
				Intent intent = new Intent(context, ChildSongActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				intent.putExtra(ChildSongActivity.INTENT_PARENT_SONG, gson.toJson(parentSong, Song.class));
				context.startActivity(intent);
			}
		};
	}
	
	public static OnClickListener getProfileClickListener(final Context context, final User user) {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (user != null) {
					User currentUser = Authenticator.getUser();
					
					if (user.getId() != currentUser.getId()) {
						Gson gson = Utility.getGsonInstance();
						String userInJson = gson.toJson(user, User.class);
						
						Intent intent = new Intent(context, ProfileRootActivity.class);
						intent.putExtra(ProfileRootActivity.INTENT_USER, userInJson);
						context.startActivity(intent);
					}
				}
			}
		};
	}
	
	public static OnClickListener getPlayClickListener(final Context context, final Song song) {
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				OldBaseActivity activity = (OldBaseActivity) context;
				PlayerService service = activity.getService();
				
				if (service != null) {
					service.setSong(song);
					
					Intent intent = new Intent(context, PlayerActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					context.startActivity(intent);
				}
			}
		};
	}
	
	public static OnClickListener getRecordClickListener(final Context context, final Music music) {
		return new MemberOnlyClickListener() {
			
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@Override
			public void onActivated(View v) {
				Gson gson = Utility.getGsonInstance();
				String musicInJson = gson.toJson(music, Music.class);
				
				Intent intent = new Intent(context, RecordMainActivity.class);
				intent.putExtra(RecordMainActivity.INTENT_MUSIC, musicInJson);
				
				if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
				}
				
				context.startActivity(intent);
			}
		};
	}
	
	public static OnClickListener getCollaboClickListener(final Context context, final Song song) {
		return new MemberOnlyClickListener() {
			
			@Override
			public void onActivated(View v) {
				UrlBuilder urlBuilder = new UrlBuilder();
				Song parentSong = song.getParentSong() == null ? song : song.getParentSong();
				parentSong.setMusic(song.getMusic());
				
				String url = urlBuilder.s("songs").s(parentSong.getId()).toString();
				OAuthJsonObjectRequest request = new OAuthJsonObjectRequest(
						Method.GET, url, null,
						new OnCheckResponse(context, parentSong),
						new OnCheckError(context)
				);
				
				RequestQueue queue = ((App) context.getApplicationContext()).getQueueInstance();
				queue.add(request);
			}
		};
	}
	
	private static class OnCheckResponse extends OnVolleyWeakResponse<Context, JSONObject> {
		
		private Song parentSong;

		public OnCheckResponse(Context context, Song parentSong) {
			super(context);
			
			this.parentSong = parentSong;
		}

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		protected void onFilteredResponse(Context context, JSONObject response) {
			Gson gson = Utility.getGsonInstance();
			
			Intent intent = new Intent(context, RecordMainActivity.class);
			intent.putExtra(RecordMainActivity.INTENT_PARENT_SONG, gson.toJson(parentSong, Song.class));
			
			if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			
			context.startActivity(intent);
		}
		
	}
	
	private static class OnCheckError extends OnVolleyWeakError<Context> {

		public OnCheckError(Context reference) {
			super(reference);
		}

		@Override
		public void onFilteredResponse(Context reference, VolleyError error) {
			Toast.makeText(reference, reference.getString(R.string.t_deleted_song), Toast.LENGTH_SHORT).show();
		}
		
	}
	
}
