package com.myandb.singsong.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListAdapter;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.RootActivity;
import com.myandb.singsong.adapter.CommentAdapter;
import com.myandb.singsong.fragment.SearchFragment.SearchType;
import com.myandb.singsong.net.UrlBuilder;

public class TeamCollaboListFragment extends ListFragment {
	
	private Button btnStartTeamCollabo;
	
	@Override
	protected int getFixedHeaderViewResId() {
		return R.layout.fragment_team_collabo_list_fixed_header;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		super.onViewInflated(view, inflater);
		
		view = getFixedHeaderView();
		btnStartTeamCollabo = (Button) view.findViewById(R.id.btn_start_team_collabo);
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		super.setupViews(savedInstanceState);
		btnStartTeamCollabo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putSerializable(SearchFragment.EXTRA_SEARCH_TYPE, SearchType.TEAM_COLLABO);
				bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, getString(R.string.fragment_search_music_action_title));
				Intent intent = new Intent(getActivity(), RootActivity.class);
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, SearchFragment.class.getName());
				intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
				startFragment(intent);
			}
		});
	}

	@Override
	protected ListAdapter instantiateAdapter(Activity activity) {
		return new CommentAdapter();
	}

	@Override
	protected UrlBuilder instantiateUrlBuilder(Activity activity) {
		return new UrlBuilder().s("songs").s("13048").s("comments");
	}

}
