package com.myandb.singsong.fragment;

import java.util.Calendar;

import org.json.JSONArray;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.UpActivity;
import com.myandb.singsong.adapter.MusicAdapter;
import com.myandb.singsong.adapter.NotificationAdapter;
import com.myandb.singsong.adapter.MusicAdapter.LayoutType;
import com.myandb.singsong.net.GradualLoader;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.net.GradualLoader.OnLoadCompleteListener;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.util.StringFormatter;
import com.myandb.singsong.widget.HorizontalListView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class HomeFragment extends BaseFragment {
	
	private TextView tvNotificationCount;
	private HorizontalListView hlvPopularMusic;
	private HorizontalListView hlvRecentMusic;
	private TextView tvRecentMusicMore;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	protected int getResourceId() {
		return R.layout.fragment_home;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		hlvPopularMusic = (HorizontalListView) view.findViewById(R.id.hlv_popular_music);
		hlvRecentMusic = (HorizontalListView) view.findViewById(R.id.hlv_recent_music);
		tvRecentMusicMore = (TextView) view.findViewById(R.id.tv_recent_music_more);
	}

	@Override
	protected void initialize(Activity activity) {
		
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		hlvPopularMusic.setDividerWidth(getResources().getDimensionPixelSize(R.dimen.margin));
		hlvRecentMusic.setDividerWidth(getResources().getDimensionPixelSize(R.dimen.margin));
		
		// load collabo artist
		// load genre songs
		// onresume logo on
		
		loadRecentMusic();
		
		loadPopularMusic();
	}
	
	private void loadPopularMusic() {
		final MusicAdapter adapter = new MusicAdapter(LayoutType.POPULAR_HOME); 
		hlvPopularMusic.setAdapter(adapter);
		final UrlBuilder urlBuilder = new UrlBuilder().s("musics").p("order", "sing_num_this_week").p("req", "songs").take(5);
		final GradualLoader loader = new GradualLoader(getActivity());
		loader.setUrlBuilder(urlBuilder);
		loader.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			
			@Override
			public void onComplete(JSONArray response) {
				adapter.addAll(response);
			}
		});
		loader.load();
	}
	
	private void loadRecentMusic() {
		final MusicAdapter adapter = new MusicAdapter(LayoutType.RECENT); 
		hlvRecentMusic.setAdapter(adapter);
		final String startDate = StringFormatter.getDateString(Calendar.DATE, -7);
		final UrlBuilder urlBuilder = new UrlBuilder().s("musics").start(startDate).p("order", "created_at").take(10);
		GradualLoader loader = new GradualLoader(getActivity());
		loader.setUrlBuilder(urlBuilder);
		loader.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			
			@Override
			public void onComplete(JSONArray response) {
				adapter.addAll(response);
			}
		});
		loader.load();
	}

	@Override
	protected void onDataChanged() {}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.home, menu);
		
		View notificationView = getActionViewCompat(menu, R.id.action_notification);
		if (notificationView != null) {
			notificationView.setOnClickListener(notificationClickListener);
			tvNotificationCount = (TextView) notificationView.findViewById(R.id.tv_action_notification_count);
			updateNotificationCount();
			registerNotificationCountListener();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_sing:
			return true;
		
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private OnClickListener notificationClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Bundle params = new Bundle();
			params.putString("order", "updated_at");
			String userId = String.valueOf(Authenticator.getUser().getId());
			Bundle bundle = new Bundle();
			bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, "새로운 소식");
			bundle.putString(ListFragment.EXTRA_URL_SEGMENT, "users/" + userId + "/notifications");
			bundle.putBundle(ListFragment.EXTRA_QUERY_PARAMS, params);
			bundle.putString(ListFragment.EXTRA_ADAPTER_NAME, NotificationAdapter.class.getName());
			Intent intent = new Intent(getActivity(), UpActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, ListFragment.class.getName());
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			startFragment(intent);
		}
	};
	
	private View getActionViewCompat(Menu menu, int id) {
		return MenuItemCompat.getActionView(menu.findItem(id));
	}
	
	private void updateNotificationCount() {
		int count = getCurrentNotificationCount();
		setNotificationNum(count, tvNotificationCount);
	}
	
	private int getCurrentNotificationCount() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String key = getString(R.string.key_notification_count);
		return preferences.getInt(key, 0);
	}
	
	private void setNotificationNum(int count, TextView textView) {
		if (textView != null) {
			if (count > 0) {
				count = Math.min(count, 99);
				textView.setVisibility(View.VISIBLE);
				textView.setText(String.valueOf(count));
			} else {
				textView.setVisibility(View.GONE);
			}
		}
	}
	
	private void registerNotificationCountListener() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		preferences.registerOnSharedPreferenceChangeListener(notificationCountChangeListener);
	}
	
	private OnSharedPreferenceChangeListener notificationCountChangeListener = new OnSharedPreferenceChangeListener() {
		
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			if (isAdded()) {
				String countkey = getString(R.string.key_notification_count);
				if (key.equals(countkey)) {
					updateNotificationCount();
				}
			}
		}
	};

}
