package com.myandb.singsong.fragment;

import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.UpActivity;
import com.myandb.singsong.adapter.ArtistAdapter;
import com.myandb.singsong.net.UrlBuilder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ArtistListFragment extends ListFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	protected void initialize(Activity activity) {
		super.initialize(activity);
		
		if (getAdapter() == null) {
			UrlBuilder builder = new UrlBuilder();
			builder.s("artists");
			setUrlBuilder(builder);
			setAdapter(new ArtistAdapter());
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.artist, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_candidate_artist:
			String url = new UrlBuilder().s("w").s("candidates").toString();
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
