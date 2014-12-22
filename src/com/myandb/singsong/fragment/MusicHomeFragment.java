package com.myandb.singsong.fragment;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;

import com.google.gson.Gson;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.adapter.CategoryAdapter;
import com.myandb.singsong.adapter.MusicAdapter;
import com.myandb.singsong.adapter.MusicAdapter.LayoutType;
import com.myandb.singsong.dialog.BaseDialog;
import com.myandb.singsong.dialog.SelectRecordModeDialog;
import com.myandb.singsong.model.Category;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.net.GradualLoader;
import com.myandb.singsong.net.GradualLoader.OnLoadCompleteListener;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.StringFormatter;
import com.myandb.singsong.util.Utility;
import com.myandb.singsong.widget.FloatableLayout;
import com.myandb.singsong.widget.HorizontalListView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class MusicHomeFragment extends BaseFragment {
	
	private HorizontalListView hlvPopularMusic;
	private HorizontalListView hlvRecentMusic;
	private FloatableLayout fltCategoryMusic;
	private TextView tvPopularMore;
	private TextView tvRecentMore;
	
	@Override
	protected int getResourceId() {
		return R.layout.fragment_music_home;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		hlvPopularMusic = (HorizontalListView) view.findViewById(R.id.hlv_popular_music);
		hlvRecentMusic = (HorizontalListView) view.findViewById(R.id.hlv_recent_music);
		fltCategoryMusic = (FloatableLayout) view.findViewById(R.id.flt_category_music);
		tvPopularMore = (TextView) view.findViewById(R.id.tv_popular_more);
		tvRecentMore = (TextView) view.findViewById(R.id.tv_recent_more);
	}

	@Override
	protected void initialize(Activity activity) {
		setHasOptionsMenu(true);
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		hlvPopularMusic.setDividerWidth(getResources().getDimensionPixelSize(R.dimen.margin));
		hlvRecentMusic.setDividerWidth(getResources().getDimensionPixelSize(R.dimen.margin));
		fltCategoryMusic.setHorizontalSpacing(R.dimen.margin_small);
		fltCategoryMusic.setVerticalSpacing(R.dimen.margin_small);
		
		tvPopularMore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String segment = "musics/";
				String adapterName = MusicAdapter.class.getName();
				String title = "title";
				Bundle bundle = new Bundle();
				bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, title);
				bundle.putString(ListFragment.EXTRA_URL_SEGMENT, segment);
				bundle.putString(ListFragment.EXTRA_ADAPTER_NAME, adapterName);
				Intent intent = new Intent(getActivity(), RootActivity.class);
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, ListFragment.class.getName());
				startFragment(intent);
			}
		});
	}

	@Override
	protected void onDataChanged() {
		loadPopularMusic();
		loadRecentMusic();
		
		ArrayList<Category> cgs = new ArrayList<Category>();
		cgs.add(new Category("ÈüÇÕ"));
		cgs.add(new Category("¹ß¶óµå"));
		cgs.add(new Category("´í½º"));
		cgs.add(new Category("·¦/µà¿§"));
		cgs.add(new Category("¼º´ë¸ð»ç"));
		cgs.add(new Category("¶óµð¿À"));
		
		CategoryAdapter adapter = new CategoryAdapter();
		adapter.addAll(cgs);
		
		fltCategoryMusic.setAdapter(adapter);
	}
	
	private void loadPopularMusic() {
		final MusicAdapter adapter = new MusicAdapter(LayoutType.POPULAR); 
		hlvPopularMusic.setAdapter(adapter);
		hlvPopularMusic.setOnItemClickListener(musicItemClickListener);
		final UrlBuilder urlBuilder = new UrlBuilder().s("musics").p("order", "sing_num_this_week").take(5);
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
		hlvRecentMusic.setOnItemClickListener(musicItemClickListener);
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

}
