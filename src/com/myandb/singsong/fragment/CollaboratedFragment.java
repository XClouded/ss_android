package com.myandb.singsong.fragment;

import java.util.Calendar;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.ArtistActivity;
import com.myandb.singsong.activity.SearchActivity;
import com.myandb.singsong.activity.SearchActivity.SearchType;
import com.myandb.singsong.adapter.CollaboratedAdapter;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.TimeHelper;
import com.myandb.singsong.widget.ResizableImageView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CollaboratedFragment extends Fragment {
	
	private ListAdapter popularAdapter;
	private ListAdapter recentAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_collaborated, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		final View view = getView();
		
		final TextView tvSortPopular = (TextView) view.findViewById(R.id.tv_sort_popular);
		final TextView tvSortRecent = (TextView) view.findViewById(R.id.tv_sort_recent);
		final ImageView ivSearch = (ImageView) view.findViewById(R.id.iv_search);
		final ListView listView = (ListView) view.findViewById(R.id.lv_full_width);
		
		final String popularString = getResources().getString(R.string.popular);
		final String recentString = getResources().getString(R.string.recent);
		final String checkString = getResources().getString(R.string._check);
		
		final ImageView ivBanner = new ResizableImageView(getActivity());
		ivBanner.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		ivBanner.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), ArtistActivity.class);
				startActivity(intent);
			}
		});
		listView.addHeaderView(ivBanner);
		
		tvSortPopular.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tvSortPopular.setTypeface(null, Typeface.BOLD);
				tvSortPopular.setText(checkString);
				tvSortPopular.append(popularString);
				
				tvSortRecent.setTypeface(null, Typeface.NORMAL);
				tvSortRecent.setText(recentString);
				
				if (popularAdapter == null) {
					final String startDate = TimeHelper.getDateString(Calendar.DATE, -1);
					popularAdapter = new CollaboratedAdapter(
							getActivity(),
							UrlBuilder.create().l("songs").l("leaf").q("order", "liking_num").start(startDate)
					);
				}
				
				listView.setAdapter(popularAdapter);
				ivBanner.setImageResource(R.drawable.banner_collabo_artist);
			}
		});
		
		tvSortRecent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tvSortRecent.setTypeface(null, Typeface.BOLD);
				tvSortRecent.setText(checkString);
				tvSortRecent.append(recentString);
				
				tvSortPopular.setTypeface(null, Typeface.NORMAL);
				tvSortPopular.setText(popularString);
				
				if (recentAdapter == null) {
					recentAdapter = new CollaboratedAdapter(
							getActivity(),
							UrlBuilder.create().l("songs").l("leaf").q("order", "created_at")
					);
				}

				listView.setAdapter(recentAdapter);
				ivBanner.setImageDrawable(null);
			}
		});
		
		ivSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), SearchActivity.class);
				intent.putExtra(SearchActivity.INTENT_SEARCH_TYPE, SearchType.SONG_LEAF);
				startActivity(intent);
			}
		});
		
		tvSortPopular.performClick();
	}

}
