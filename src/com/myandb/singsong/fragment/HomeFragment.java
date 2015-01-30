package com.myandb.singsong.fragment;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.Gson;
import com.mhdjang.infiniteviewpager.InfinitePagerAdapter;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.activity.UpActivity;
import com.myandb.singsong.adapter.ArtistAdapter;
import com.myandb.singsong.adapter.MusicAdapter;
import com.myandb.singsong.adapter.SimpleChildrenSongAdapter;
import com.myandb.singsong.adapter.SimpleSongNumAdapter;
import com.myandb.singsong.adapter.MusicAdapter.LayoutType;
import com.myandb.singsong.event.MemberOnlyClickListener;
import com.myandb.singsong.image.ImageHelper;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.model.Song;
import com.myandb.singsong.model.User;
import com.myandb.singsong.net.GradualLoader;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.net.GradualLoader.OnLoadCompleteListener;
import com.myandb.singsong.pager.ListenPagerAdapter.SongType;
import com.myandb.singsong.pager.PagerWrappingAdapter;
import com.myandb.singsong.service.PlayerService;
import com.myandb.singsong.util.StringFormatter;
import com.myandb.singsong.util.Utility;
import com.myandb.singsong.widget.HorizontalListView;
import com.viewpagerindicator.CirclePageIndicator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class HomeFragment extends BaseFragment {
	
	private TextView tvNotificationCount;
	private TextView tvTop10FirstLikeNum;
	private TextView tvTop10FirstMusicInfo;
	private TextView tvTop10FirstUserInfo;
	private TextView tvSongCollaboratedMore;
	private TextView tvRecentMusicMore;
	private TextView tvPopularMusicMore;
	private TextView tvCollaboArtistMore;
	private TextView tvSongWaitingMore;
	private ImageView ivTop10FirstAlbumPhoto;
	private ViewPager vpCollaboTop10;
	private ViewPager vpPopularMusic;
	private HorizontalListView hlvRecentMusic;
	private HorizontalListView hlvWaitingSong;
	private CirclePageIndicator cpiCollaboTop10;
	private CirclePageIndicator cpiPopularMusic;
	private View content;
	private View progressBar;
	private View vtop10First;
	private ViewGroup vgCollaboArtistContainer;
	private PagerAdapter collaboTop10Adapter;
	private PagerAdapter popularMusicAdapter;
	private MusicAdapter recentMusicAdapter;
	private SimpleChildrenSongAdapter waitingSongAdapter;
	private ArtistAdapter collaboArtistAdapter;
	private Song top10First;
	
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
		tvTop10FirstLikeNum = (TextView) view.findViewById(R.id.tv_top10_first_like_num);
		tvTop10FirstMusicInfo = (TextView) view.findViewById(R.id.tv_top10_first_music_info);
		tvTop10FirstUserInfo = (TextView) view.findViewById(R.id.tv_top10_first_user_info);
		tvSongCollaboratedMore = (TextView) view.findViewById(R.id.tv_song_collaborated_more);
		tvRecentMusicMore = (TextView) view.findViewById(R.id.tv_recent_music_more);
		tvPopularMusicMore = (TextView) view.findViewById(R.id.tv_popular_music_more);
		tvCollaboArtistMore = (TextView) view.findViewById(R.id.tv_collabo_artist_more);
		tvSongWaitingMore = (TextView) view.findViewById(R.id.tv_song_waiting_more);
		
		vpCollaboTop10 = (ViewPager) view.findViewById(R.id.vp_collabo_top10);
		vpPopularMusic = (ViewPager) view.findViewById(R.id.vp_popular_music);
		hlvRecentMusic = (HorizontalListView) view.findViewById(R.id.hlv_recent_music);
		hlvWaitingSong = (HorizontalListView) view.findViewById(R.id.hlv_waiting_song);
		cpiCollaboTop10 = (CirclePageIndicator) view.findViewById(R.id.cpi_collabo_top10);
		cpiPopularMusic = (CirclePageIndicator) view.findViewById(R.id.cpi_popular_music);
		
		ivTop10FirstAlbumPhoto = (ImageView) view.findViewById(R.id.iv_top10_first_album_photo);
		content = view.findViewById(R.id.loadable_content);
		progressBar = view.findViewById(R.id.progress_bar);
		vtop10First = view.findViewById(R.id.fl_top10_first);
		vgCollaboArtistContainer = (ViewGroup) view.findViewById(R.id.fl_collabo_artist_container);
	}

	@Override
	protected void initialize(Activity activity) {
		int padding = getResources().getDimensionPixelSize(R.dimen.margin);
		hlvRecentMusic.setDividerWidth(padding / 2);
		hlvWaitingSong.setDividerWidth(padding / 2);
	}
	
	@Override
	protected void setupViews(Bundle savedInstanceState) {
		setContentShown(false);
		
		loadCollaboTop10();
		
		loadRecentMusic();
		
		loadPopularMusic();
		
		loadCollaboArtist();
		
		loadWaitingTop10();
		
		stylePageIndicator(cpiCollaboTop10);
		stylePageIndicator(cpiPopularMusic);
		
		tvCollaboArtistMore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, getString(R.string.fragment_artist_list_action_title));
				bundle.putBoolean(ListFragment.EXTRA_HORIZONTAL_PADDING, true);
				bundle.putBoolean(ListFragment.EXTRA_VERTICAL_PADDING, true);
				Intent intent = new Intent(getActivity(), RootActivity.class);
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, ArtistListFragment.class.getName());
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
				startFragment(intent);
			}
		});
		
		tvSongCollaboratedMore.setOnClickListener(songMoreClickListener);
		tvSongWaitingMore.setOnClickListener(songMoreClickListener);
		tvRecentMusicMore.setOnClickListener(musicMoreClickListener);
		tvPopularMusicMore.setOnClickListener(musicMoreClickListener); 
	}
	
	private void loadCollaboTop10() {
		if (collaboTop10Adapter == null) {
			final String yesterday = StringFormatter.getDateString(Calendar.DAY_OF_YEAR, -1);
			final UrlBuilder urlBuilder = new UrlBuilder().s("songs").s("leaf").start(yesterday).p("order", "liking_num").take(10);
			final GradualLoader loader = new GradualLoader(getActivity());  
			loader.setUrlBuilder(urlBuilder);
			loader.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				
				@Override
				public void onComplete(JSONArray response) {
					try {
						top10First = getTop10First(response);
						collaboTop10Adapter = getCollaboTop10Adapter(response);
						onLoadCollaboTop10();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				private Song getTop10First(JSONArray response) {
					try {
						Gson gson = Utility.getGsonInstance();
						return gson.fromJson(response.getJSONObject(0).toString(), Song.class);
					} catch (Exception e) {
						return null;
					}
				}
				
				private PagerAdapter getCollaboTop10Adapter(JSONArray response) {
					SimpleSongNumAdapter adapter = new SimpleSongNumAdapter(1);
					try {
						adapter.addAll(removeElement(response, 0));
						PagerWrappingAdapter wrappingAdapter = new PagerWrappingAdapter(adapter, 3);
						return new InfinitePagerAdapter(wrappingAdapter);
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				}
			});
			loader.load();
		} else {
			onLoadCollaboTop10();
		}
	}
	
	private void onLoadCollaboTop10() {
		bindTop10First(top10First);
		if (collaboTop10Adapter != null) {
			vpCollaboTop10.setAdapter(collaboTop10Adapter);
			cpiCollaboTop10.setViewPager(vpCollaboTop10);
		}
		setContentShown(true);
	}
	
	private JSONArray removeElement(JSONArray array, int index) throws JSONException {
		if (index < 0 || index >= array.length()) {
			return array;
		} else {
			JSONArray newArray = new JSONArray();
			for (int i = 0, l = array.length(); i < l; i++) {
				if (i != index) {
					newArray.put(array.getJSONObject(i));
				}
			}
			return newArray;
		}
	}
	
	private void bindTop10First(Song song) {
		if (song == null) {
			return;
		}
		
		final Music music = song.getMusic();
		final Song parentSong = song.getParentSong();
		tvTop10FirstLikeNum.setText(song.getWorkedLikeNum());
		tvTop10FirstMusicInfo.setText(music.getTitle() + " - " + music.getSingerName());
		tvTop10FirstUserInfo.setText(parentSong.getCreator().getNickname() + " X " + song.getCreator().getNickname());
		vtop10First.setOnClickListener(song.getPlayClickListener());
		ImageHelper.displayPhoto(music.getAlbumPhotoUrl(), ivTop10FirstAlbumPhoto);
	}
	
	private void loadPopularMusic() {
		if (popularMusicAdapter == null) {
			final UrlBuilder urlBuilder = new UrlBuilder().s("musics").p("order", "sing_num_this_week").take(12);
			final GradualLoader loader = new GradualLoader(getActivity());  
			loader.setUrlBuilder(urlBuilder);
			loader.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				
				@Override
				public void onComplete(JSONArray response) {
					popularMusicAdapter = getPopularMusicAdapter(response);
					onLoadPopularMusic();
				}
				
				private PagerAdapter getPopularMusicAdapter(JSONArray response) {
					try {
						MusicAdapter adapter = new MusicAdapter(LayoutType.NORMAL_POPULAR);
						adapter.addAll(response);
						PagerWrappingAdapter wrappingAdapter = new PagerWrappingAdapter(adapter, 3);
						return new InfinitePagerAdapter(wrappingAdapter);
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				}
			});
			loader.load();
		} else {
			onLoadPopularMusic();
		}
	}
	
	private void onLoadPopularMusic() {
		if (popularMusicAdapter != null) {
			vpPopularMusic.setAdapter(popularMusicAdapter);
			cpiPopularMusic.setViewPager(vpPopularMusic);
		}
		setContentShown(true);
	}
	
	private void loadRecentMusic() {
		if (recentMusicAdapter == null) {
			final UrlBuilder urlBuilder = new UrlBuilder().s("musics").take(10);
			GradualLoader loader = new GradualLoader(getActivity());
			loader.setUrlBuilder(urlBuilder);
			loader.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				
				@Override
				public void onComplete(JSONArray response) {
					recentMusicAdapter = new MusicAdapter(LayoutType.RECENT);
					recentMusicAdapter.addAll(response);
					hlvRecentMusic.setAdapter(recentMusicAdapter);
					hlvRecentMusic.setOnItemClickListener(musicItemClickListener);
					setContentShown(true);
				}
			});
			loader.load();
		} else {
			hlvRecentMusic.setAdapter(recentMusicAdapter);
			hlvRecentMusic.setOnItemClickListener(musicItemClickListener);
			setContentShown(true);
		}
	}
	
	private OnItemClickListener musicItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (!isAdded() || getActivity() == null) {
				return;
			}
			
			final Music music = (Music) parent.getItemAtPosition(position);
			Bundle bundle = new Bundle();
			bundle.putString(SelectRecordModeFragment.EXTRA_MUSIC, music.toString());
			Intent intent = new Intent(getActivity(), UpActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, SelectRecordModeFragment.class.getName());
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			intent.putExtra(UpActivity.EXTRA_FULL_SCREEN, true);
			startFragment(intent);
		}
	};
	
	private void loadCollaboArtist() {
		if (collaboArtistAdapter == null) {
			collaboArtistAdapter = new ArtistAdapter();
			final UrlBuilder urlBuilder = new UrlBuilder().s("artists").take(1);
			final GradualLoader loader = new GradualLoader(getActivity());
			loader.setUrlBuilder(urlBuilder);
			loader.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				
				@Override
				public void onComplete(JSONArray response) {
					collaboArtistAdapter.addAll(response);
					addArtistView();
				}
			});
			loader.load();
		} else {
			addArtistView();
		}
	}
	
	private void addArtistView() {
		if (collaboArtistAdapter != null && collaboArtistAdapter.getCount() > 0) {
			View child = collaboArtistAdapter.getView(0, null, vgCollaboArtistContainer);
			child.setBackgroundResource(0);
			vgCollaboArtistContainer.addView(child);
		}
	}
	
	private void loadWaitingTop10() {
		if (waitingSongAdapter == null) {
			final UrlBuilder urlBuilder = new UrlBuilder().s("songs").s("root").p("order", "liking_num").take(10);
			GradualLoader loader = new GradualLoader(getActivity());
			loader.setUrlBuilder(urlBuilder);
			loader.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				
				@Override
				public void onComplete(JSONArray response) {
					waitingSongAdapter = new SimpleChildrenSongAdapter();
					waitingSongAdapter.addAll(response);
					hlvWaitingSong.setAdapter(waitingSongAdapter);
					hlvWaitingSong.setOnItemClickListener(songItemClickListener);
					setContentShown(true);
				}
			});
			loader.load();
		} else {
			hlvWaitingSong.setAdapter(waitingSongAdapter);
			hlvWaitingSong.setOnItemClickListener(songItemClickListener);
			setContentShown(true);
		}
	}
	
	private OnItemClickListener songItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (!isAdded() || getActivity() == null) {
				return;
			}
			
			final Song song = (Song) parent.getItemAtPosition(position);
			BaseActivity activity = (BaseActivity) getActivity();
			PlayerService service = activity.getPlayerService();
			service.startPlaying(song);
		}
	};
	
	private OnClickListener songMoreClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Bundle bundle = new Bundle();
			bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, getString(R.string.fragment_listen_action_title));
			if (v.getId() == R.id.tv_song_waiting_more) {
				bundle.putSerializable(ListenHomeFragment.EXTRA_SONG_TYPE, SongType.WAITING);
			}
			Intent intent = new Intent(getActivity(), RootActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, ListenHomeFragment.class.getName());
			startFragment(intent);
		}
	};
	
	private OnClickListener musicMoreClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String segment = "musics/";
			String title = "";
			Bundle bundle = new Bundle();
			Bundle params = new Bundle();
			
			switch (v.getId()) {
			case R.id.tv_recent_music_more:
				title = getString(R.string.sort_by_time);
				params.putString("order", "created_at");
				break;
				
			case R.id.tv_popular_music_more:
				title = getString(R.string.sort_by_popularity);
				params.putString("order", "sing_num_this_week");
				bundle.putBoolean(MusicListFragment.EXTRA_SHOW_NUM, true);
				break;

			default:
				return;
			}
			
			bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, title);
			bundle.putString(ListFragment.EXTRA_URL_SEGMENT, segment);
			bundle.putBundle(ListFragment.EXTRA_QUERY_PARAMS, params);
			Intent intent = new Intent(getActivity(), RootActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, MusicListFragment.class.getName());
			startFragment(intent);
		}
	};
	
	private void stylePageIndicator(CirclePageIndicator indicator) {
		indicator.setStrokeWidth(0f);
		indicator.setPageColor(getResources().getColor(R.color.grey_light));
		indicator.setFillColor(getResources().getColor(R.color.sub));
	}

	@Override
	protected void onDataChanged() {}
	
	public void setContentShown(boolean shown) {
		if (content.isShown() == shown) {
			return;
		}
		
		if (shown) {
			if (vpCollaboTop10.getAdapter() != null &&
					vpPopularMusic.getAdapter() != null &&
					hlvRecentMusic.getAdapter() != null) {
				content.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
			}
		} else {
			content.setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);
		}
	}
	
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
			Bundle bundle = new Bundle();
			bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, getString(R.string.fragment_sing_action_title));
			Intent intent = new Intent(getActivity(), RootActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, MusicHomeFragment.class.getName());
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			startFragment(intent);
			return true;
		
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private OnClickListener notificationClickListener = new MemberOnlyClickListener() {
		
		@Override
		public void onLoggedIn(View v, User user) {
			Intent intent = new Intent(getActivity(), UpActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, NotificationFragment.class.getName());
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
	public boolean isActionBarLogoEnabled() {
		return true;
	}

}
