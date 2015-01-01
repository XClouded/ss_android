package com.myandb.singsong.fragment;

import org.json.JSONArray;

import com.google.gson.Gson;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.activity.UpActivity;
import com.myandb.singsong.adapter.ArtistAdapter;
import com.myandb.singsong.adapter.MusicAdapter;
import com.myandb.singsong.adapter.NotificationAdapter;
import com.myandb.singsong.adapter.MusicAdapter.LayoutType;
import com.myandb.singsong.dialog.BaseDialog;
import com.myandb.singsong.dialog.SelectRecordModeDialog;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.net.GradualLoader;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.net.GradualLoader.OnLoadCompleteListener;
import com.myandb.singsong.pager.PagerWrappingAdapter;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.util.Utility;
import com.myandb.singsong.widget.HorizontalListView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class HomeFragment extends BaseFragment {
	
	private TextView tvNotificationCount;
	private TextView tvRecentMusicMore;
	private TextView tvCollaboArtistMore;
	private ViewPager vpPopularMusic;
	private PagerWrappingAdapter popularMusicAdapter;
	private HorizontalListView hlvRecentMusic;
	private ViewGroup vgTodayCollaboArtistContainer;
	private MusicAdapter recentMusicAdapter;
	private ArtistAdapter todayArtistAdapter;
	
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
		vpPopularMusic = (ViewPager) view.findViewById(R.id.vp_popular_music);
		hlvRecentMusic = (HorizontalListView) view.findViewById(R.id.hlv_recent_music);
		tvRecentMusicMore = (TextView) view.findViewById(R.id.tv_recent_music_more);
		tvCollaboArtistMore = (TextView) view.findViewById(R.id.tv_collabo_artist_more);
		vgTodayCollaboArtistContainer = (ViewGroup) view.findViewById(R.id.fl_today_collabo_artist_container);
	}

	@Override
	protected void initialize(Activity activity) {
		int padding = getResources().getDimensionPixelSize(R.dimen.margin);
		hlvRecentMusic.setDividerWidth(padding / 2);
		vpPopularMusic.setPadding(padding, 0, padding, 0);
		vpPopularMusic.setClipToPadding(false);
		vpPopularMusic.setPageMargin(padding / 2);
	}
	
	@Override
	protected void setupViews(Bundle savedInstanceState) {
		loadTodayCollaboArtist();
		
		// load genre songs
		
		loadRecentMusic();
		
		loadPopularMusic();
		
		tvCollaboArtistMore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, getString(R.string.fragment_artist_list_title));
				Intent intent = new Intent(getActivity(), RootActivity.class);
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, ArtistListFragment.class.getName());
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
				startFragment(intent);
			}
		});
		
		tvRecentMusicMore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String segment = "musics/";
				String title = getString(R.string.recent);
				Bundle bundle = new Bundle();
				Bundle params = new Bundle();
				params.putString("order", "created_at");
				bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, title);
				bundle.putString(ListFragment.EXTRA_URL_SEGMENT, segment);
				bundle.putBundle(ListFragment.EXTRA_QUERY_PARAMS, params);
				Intent intent = new Intent(getActivity(), RootActivity.class);
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, MusicListFragment.class.getName());
				startFragment(intent);
			}
		});
	}
	
	private void loadTodayCollaboArtist() {
		if (todayArtistAdapter == null) {
			todayArtistAdapter = new ArtistAdapter();
			final UrlBuilder urlBuilder = new UrlBuilder().s("artists").take(1);
			final GradualLoader loader = new GradualLoader(getActivity());
			loader.setUrlBuilder(urlBuilder);
			loader.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				
				@Override
				public void onComplete(JSONArray response) {
					todayArtistAdapter.addAll(response);
					addArtistView();
				}
			});
			loader.load(); 
		} else {
			addArtistView();
		}
	}
	
	private void addArtistView() {
		if (todayArtistAdapter != null && todayArtistAdapter.getCount() > 0) {
			View child = todayArtistAdapter.getView(0, null, vgTodayCollaboArtistContainer);
			vgTodayCollaboArtistContainer.addView(child);
		}
	}
	
	private void loadPopularMusic() {
		if (popularMusicAdapter == null) {
			final MusicAdapter adapter = new MusicAdapter(LayoutType.POPULAR_HOME);
			popularMusicAdapter = new PagerWrappingAdapter(adapter);
			final UrlBuilder urlBuilder = new UrlBuilder().s("musics").p("order", "sing_num_this_week").p("req", "songs").take(5);
			final GradualLoader loader = new GradualLoader(getActivity());
			loader.setUrlBuilder(urlBuilder);
			loader.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				
				@Override
				public void onComplete(JSONArray response) {
					adapter.addAll(response);
					vpPopularMusic.setAdapter(popularMusicAdapter);
				}
			});
			loader.load();
		} else {
			vpPopularMusic.setAdapter(popularMusicAdapter);
		}
	}
	
	private void loadRecentMusic() {
		if (recentMusicAdapter == null) {
			final MusicAdapter adapter = new MusicAdapter(LayoutType.RECENT); 
			hlvRecentMusic.setAdapter(adapter);
			final UrlBuilder urlBuilder = new UrlBuilder().s("musics").take(10);
			GradualLoader loader = new GradualLoader(getActivity());
			loader.setUrlBuilder(urlBuilder);
			loader.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				
				@Override
				public void onComplete(JSONArray response) {
					adapter.addAll(response);
					hlvRecentMusic.setAdapter(recentMusicAdapter);
					hlvRecentMusic.setOnItemClickListener(musicItemClickListener);
				}
			});
			loader.load();
		} else {
			hlvRecentMusic.setAdapter(recentMusicAdapter);
			hlvRecentMusic.setOnItemClickListener(musicItemClickListener);
		}
	}
	
	private OnItemClickListener musicItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			final Music music = (Music) parent.getItemAtPosition(position);
			Gson gson = Utility.getGsonInstance();
			Bundle bundle = new Bundle();
			bundle.putString(SelectRecordModeDialog.EXTRA_MUSIC, gson.toJson(music));
			BaseDialog dialog = new SelectRecordModeDialog();
			dialog.setArguments(bundle);
			dialog.show(getChildFragmentManager(), "");
		}
	};

	@Override
	protected void onDataChanged() {}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (Authenticator.isLoggedIn()) {
			inflater.inflate(R.menu.home, menu);
			
			View notificationView = getActionViewCompat(menu, R.id.action_notification);
			if (notificationView != null) {
				notificationView.setOnClickListener(notificationClickListener);
				tvNotificationCount = (TextView) notificationView.findViewById(R.id.tv_action_notification_count);
				updateNotificationCount();
				registerNotificationCountListener();
			}
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

	@Override
	public void onResume() {
		super.onResume();
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setLogo(R.drawable.logo_actionbar);
	}

}
