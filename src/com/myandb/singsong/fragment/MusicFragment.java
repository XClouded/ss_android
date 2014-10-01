package com.myandb.singsong.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import com.myandb.singsong.App;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.SearchActivity;
import com.myandb.singsong.activity.SearchActivity.SearchType;
import com.myandb.singsong.adapter.MusicSquareAdapter;
import com.myandb.singsong.event.OnVolleyWeakError;
import com.myandb.singsong.event.OnVolleyWeakResponse;
import com.myandb.singsong.event.WeakRunnable;
import com.myandb.singsong.model.Music;
import com.myandb.singsong.net.OAuthJsonArrayRequest;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.pager.InfinitePagerAdapter;
import com.myandb.singsong.pager.PopularMusicAdapter;
import com.myandb.singsong.util.TimeHelper;
import com.myandb.singsong.util.Utility;

public class MusicFragment extends Fragment {
	
	private ViewPager vpPopularMusic;
	private ListView lvUpdateMusic;
	private Handler handler;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.common_listview, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		final View view = getView();
		final View header = View.inflate(getActivity(), R.layout.fragment_music_header, null);
		
		lvUpdateMusic = (ListView) view.findViewById(R.id.lv_full_width);
		lvUpdateMusic.addHeaderView(header);
		
		final View vSearch = view.findViewById(R.id.rl_search);
		vpPopularMusic = (ViewPager) view.findViewById(R.id.vp_popular_music);
		
		vSearch.setOnClickListener(searchClickListener);
		
		loadPopularMusic();
		
		loadUpdateMusic();
	}
	
	private OnClickListener searchClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(getActivity(), SearchActivity.class);
			intent.putExtra(SearchActivity.INTENT_SEARCH_TYPE, SearchType.MUSIC);
			startActivity(intent);
		}
	};
	
	private void loadPopularMusic() {
		final String url = UrlBuilder.getInstance().l("musics").q("order", "sing_num_this_week").take(5).build();
		
		final OAuthJsonArrayRequest request = new OAuthJsonArrayRequest(
				url,
				new OnVolleyWeakResponse<MusicFragment, JSONArray>(this, "onLoadPopularMusic"),
				new OnVolleyWeakError<MusicFragment>(this, "onLoadPopularMusicError")
		);
		
		RequestQueue queue = ((App) getActivity().getApplicationContext()).getQueueInstance();
		queue.add(request);
	}
	
	public void onLoadPopularMusic(JSONArray response) {
		Gson gson = Utility.getGsonInstance();
		List<Music> musics = new ArrayList<Music>();
		JSONObject iMusic;
		
		try {
			for (int i = 0, l = response.length(); i < l; i++) {
				iMusic = response.getJSONObject(i);
				musics.add(gson.fromJson(iMusic.toString(), Music.class));
			}
			
			PagerAdapter adapter = new PopularMusicAdapter(getActivity(), musics);
			InfinitePagerAdapter infiniteAdapter = new InfinitePagerAdapter(adapter);
			
			vpPopularMusic.setAdapter(infiniteAdapter);
			
			handler = new Handler();
			handler.postDelayed(new WeakRunnable<MusicFragment>(this, "onViewPagerScrolled"), 4000);
		} catch (JSONException e) {
			onLoadPopularMusicError();
		}
	}
	
	public void onLoadPopularMusicError() {
		
	}
	
	public void onViewPagerScrolled() {
		vpPopularMusic.setCurrentItem(vpPopularMusic.getCurrentItem() + 1, true);
		
		handler.postDelayed(new WeakRunnable<MusicFragment>(this, "onViewPagerScrolled"), 5000);
	}
	
	private void loadUpdateMusic() {
		final String startDate = TimeHelper.getDateString(Calendar.DATE, -7);
		UrlBuilder urlBuilder = UrlBuilder.create();
		urlBuilder.l("musics").start(startDate).q("order", "created_at");
		
		MusicSquareAdapter adapter = new MusicSquareAdapter(getActivity(), urlBuilder);
		lvUpdateMusic.setAdapter(adapter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
			handler = null;
		}
	}

}
