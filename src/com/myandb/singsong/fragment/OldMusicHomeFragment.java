package com.myandb.singsong.fragment;

import java.util.ArrayList;

import org.json.JSONArray;

import com.mhdjang.infiniteviewpager.InfinitePagerAdapter;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.activity.UpActivity;
import com.myandb.singsong.adapter.MusicCategoryAdapter;
import com.myandb.singsong.adapter.MusicAdapter;
import com.myandb.singsong.adapter.MusicAdapter.LayoutType;
import com.myandb.singsong.fragment.SearchFragment.SearchType;
import com.myandb.singsong.model.Category;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.net.GradualLoader;
import com.myandb.singsong.net.GradualLoader.OnLoadCompleteListener;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.pager.PagerWrappingAdapter;
import com.myandb.singsong.widget.FloatableLayout;
import com.myandb.singsong.widget.HorizontalListView;
import com.viewpagerindicator.CirclePageIndicator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class OldMusicHomeFragment extends BaseFragment {
	
	private TextView tvRecentMusicMore;
	private TextView tvPopularMusicMore;
	private ViewPager vpPopularMusic;
	private HorizontalListView hlvRecentMusic;
	private CirclePageIndicator cpiPopularMusic;
	private View content;
	private View progressBar;
	private PagerAdapter popularMusicAdapter;
	private MusicAdapter recentMusicAdapter;
	private FloatableLayout fltCategoryMusic;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	protected int getResourceId() {
		return R.layout.fragment_old_music_home;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		tvRecentMusicMore = (TextView) view.findViewById(R.id.tv_recent_music_more);
		tvPopularMusicMore = (TextView) view.findViewById(R.id.tv_popular_music_more);
		vpPopularMusic = (ViewPager) view.findViewById(R.id.vp_popular_music);
		hlvRecentMusic = (HorizontalListView) view.findViewById(R.id.hlv_recent_music);
		fltCategoryMusic = (FloatableLayout) view.findViewById(R.id.flt_category_music);
		cpiPopularMusic = (CirclePageIndicator) view.findViewById(R.id.cpi_popular_music);
		content = view.findViewById(R.id.loadable_content);
		progressBar = view.findViewById(R.id.progress_bar);
	}

	@Override
	protected void initialize(Activity activity) {
		int padding = getResources().getDimensionPixelSize(R.dimen.margin);
		hlvRecentMusic.setDividerWidth(padding / 2);
		
		fltCategoryMusic.setHorizontalSpacing(R.dimen.margin_small);
		fltCategoryMusic.setVerticalSpacing(R.dimen.margin_small);
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		setContentShown(false);
		
		loadRecentMusic();
		
		loadPopularMusic();
		
		stylePageIndicator(cpiPopularMusic);
		
		ArrayList<Category> cgs = new ArrayList<Category>();
		for (int i = 1; i < 10; i++) {
			cgs.add(new Category(i));
		}
		
		MusicCategoryAdapter adapter = new MusicCategoryAdapter();
		adapter.addAll(cgs);
		fltCategoryMusic.setAdapter(adapter);
		
		tvRecentMusicMore.setOnClickListener(musicMoreClickListener);
		tvPopularMusicMore.setOnClickListener(musicMoreClickListener);
	}
	
	public void setContentShown(boolean shown) {
		if (content.isShown() == shown) {
			return;
		}
		
		if (shown) {
			if (vpPopularMusic.getAdapter() != null && hlvRecentMusic.getAdapter() != null) {
				content.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.INVISIBLE);
			}
		} else {
			content.setVisibility(View.INVISIBLE);
			progressBar.setVisibility(View.VISIBLE);
		}
	}
	
	private void stylePageIndicator(CirclePageIndicator indicator) {
		indicator.setStrokeWidth(0f);
		indicator.setPageColor(getResources().getColor(R.color.grey_light));
		indicator.setFillColor(getResources().getColor(R.color.sub));
	}

	@Override
	protected void onDataChanged() {}
	
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

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.search, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_search:
			Bundle bundle = new Bundle();
			bundle.putSerializable(SearchFragment.EXTRA_SEARCH_TYPE, SearchType.MUSIC);
			bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, getString(R.string.fragment_search_music_action_title));
			Intent intent = new Intent(getActivity(), RootActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, SearchFragment.class.getName());
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			startFragment(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
