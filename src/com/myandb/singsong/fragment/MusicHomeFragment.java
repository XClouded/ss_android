package com.myandb.singsong.fragment;

import java.util.ArrayList;

import org.json.JSONArray;

import com.google.gson.Gson;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.adapter.MusicCategoryAdapter;
import com.myandb.singsong.adapter.MusicAdapter;
import com.myandb.singsong.adapter.MusicAdapter.LayoutType;
import com.myandb.singsong.dialog.BaseDialog;
import com.myandb.singsong.dialog.SelectRecordModeDialog;
import com.myandb.singsong.fragment.SearchFragment.SearchType;
import com.myandb.singsong.model.Category;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.net.GradualLoader;
import com.myandb.singsong.net.GradualLoader.OnLoadCompleteListener;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.pager.PagerWrappingAdapter;
import com.myandb.singsong.util.Utility;
import com.myandb.singsong.widget.FloatableLayout;
import com.myandb.singsong.widget.HorizontalListView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

public class MusicHomeFragment extends BaseFragment {
	
	private ViewPager vpPopularMusic;
	private PagerWrappingAdapter popularMusicAdapter;
	private HorizontalListView hlvRecentMusic;
	private FloatableLayout fltCategoryMusic;
	private TextView tvPopularMusicMore;
	private TextView tvRecentMusicMore;
	private MusicAdapter recentMusicAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	protected int getResourceId() {
		return R.layout.fragment_music_home;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		vpPopularMusic = (ViewPager) view.findViewById(R.id.vp_popular_music);
		hlvRecentMusic = (HorizontalListView) view.findViewById(R.id.hlv_recent_music);
		fltCategoryMusic = (FloatableLayout) view.findViewById(R.id.flt_category_music);
		tvPopularMusicMore = (TextView) view.findViewById(R.id.tv_popular_music_more);
		tvRecentMusicMore = (TextView) view.findViewById(R.id.tv_recent_music_more);
	}

	@Override
	protected void initialize(Activity activity) {
		setHasOptionsMenu(true);
		
		int padding = getResources().getDimensionPixelSize(R.dimen.margin);
		hlvRecentMusic.setDividerWidth(padding / 2);
		vpPopularMusic.setPadding(padding, 0, padding, 0);
		vpPopularMusic.setClipToPadding(false);
		vpPopularMusic.setPageMargin(padding / 2);
		
		fltCategoryMusic.setHorizontalSpacing(R.dimen.margin_small);
		fltCategoryMusic.setVerticalSpacing(R.dimen.margin_small);
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		tvPopularMusicMore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String segment = "musics/";
				String title = getString(R.string.popular);
				Bundle bundle = new Bundle();
				Bundle params = new Bundle();
				params.putString("order", "sing_num_this_week");
				bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, title);
				bundle.putString(ListFragment.EXTRA_URL_SEGMENT, segment);
				bundle.putBundle(ListFragment.EXTRA_QUERY_PARAMS, params);
				bundle.putBoolean(MusicListFragment.EXTRA_SHOW_NUM, true);
				Intent intent = new Intent(getActivity(), RootActivity.class);
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, MusicListFragment.class.getName());
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

	@Override
	protected void onDataChanged() {
		loadPopularMusic();
		loadRecentMusic();
		
		ArrayList<Category> cgs = new ArrayList<Category>();
		cgs.add(new Category(1, "발라드"));
		cgs.add(new Category(2, "댄스"));
		cgs.add(new Category(3, "랩/힙합"));
		cgs.add(new Category(4, "R&B/Soul"));
		cgs.add(new Category(5, "록"));
		cgs.add(new Category(6, "OST"));
		cgs.add(new Category(7, "트로트"));
		cgs.add(new Category(8, "포크"));
		cgs.add(new Category(9, "인디음악"));
		
		MusicCategoryAdapter adapter = new MusicCategoryAdapter();
		adapter.addAll(cgs);
		
		fltCategoryMusic.setAdapter(adapter);
	}
	
	private void loadPopularMusic() {
		if (popularMusicAdapter == null) {
			final MusicAdapter adapter = new MusicAdapter(LayoutType.POPULAR);
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
			recentMusicAdapter = new MusicAdapter(LayoutType.RECENT); 
			final UrlBuilder urlBuilder = new UrlBuilder().s("musics").take(10);
			GradualLoader loader = new GradualLoader(getActivity());
			loader.setUrlBuilder(urlBuilder);
			loader.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				
				@Override
				public void onComplete(JSONArray response) {
					recentMusicAdapter.addAll(response);
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
			bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, "노래 검색");
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
