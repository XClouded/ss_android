package com.myandb.singsong.fragment;

import org.json.JSONArray;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.UpActivity;
import com.myandb.singsong.adapter.ArtistAdapter;
import com.myandb.singsong.adapter.ArtistAdapter.LayoutType;
import com.myandb.singsong.net.GradualLoader;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.net.GradualLoader.OnLoadCompleteListener;
import com.myandb.singsong.secure.Authenticator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public class ArtistListFragment extends ListFragment {
	
	private ViewGroup vgTodayCollaboArtistContainer;
	private ArtistAdapter todayArtistAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	protected int getListHeaderViewResId() {
		return R.layout.fragment_artist_list_header;
	}

	@Override
	protected ListAdapter instantiateAdapter(Activity activity) {
		return new ArtistAdapter(LayoutType.SIMPLE);
	}

	@Override
	protected UrlBuilder instantiateUrlBuilder(Activity activity) {
		return new UrlBuilder().s("artists").skip(1);
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		super.onViewInflated(view, inflater);
		
		view = getListHeaderView();
		vgTodayCollaboArtistContainer = (ViewGroup) view.findViewById(R.id.fl_today_collabo_artist_container);
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		super.setupViews(savedInstanceState);
		enableFadingActionBar(false);
		loadTodayCollaboArtist();
		getListView().setDivider(null);
	}
	
	private void loadTodayCollaboArtist() {
		if (todayArtistAdapter == null) {
			todayArtistAdapter = new ArtistAdapter(LayoutType.NORMAL);
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
		if (todayArtistAdapter != null) {
			View child = todayArtistAdapter.getView(0, null, vgTodayCollaboArtistContainer);
			vgTodayCollaboArtistContainer.addView(child);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (Authenticator.isLoggedIn()) {
			inflater.inflate(R.menu.artist, menu);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_candidate_artist:
			String url = new UrlBuilder().s("w").s("apply-candidate").toString();
			Bundle bundle = new Bundle();
			bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, "콜라보 아티스트 신청하기");
			bundle.putString(WebViewFragment.EXTRA_WEBVIEW_URL, url);
			Intent intent = new Intent(getActivity(), UpActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, WebViewFragment.class.getName());
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			startFragment(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
