package com.myandb.singsong.fragment;

import java.util.Calendar;

import org.json.JSONArray;

import com.myandb.singsong.adapter.CollaboratedAdapter;
import com.myandb.singsong.net.GradualLoader.OnLoadCompleteListener;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.Logger;
import com.myandb.singsong.util.StringFormatter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class ArtistListFragment extends ListFragment {

	@Override
	protected View inflateEmptyView(LayoutInflater inflater) {
		return null;
	}

	@Override
	protected View inflateListHeaderView(LayoutInflater inflater) {
		return null;
	}

	@Override
	protected View inflateFixedHeaderView(LayoutInflater inflater) {
		return null;
	}

	@Override
	protected void onArgumentsReceived(Bundle bundle) {
		
	}

	@Override
	protected void onDataChanged() {
		final String startDate = StringFormatter.getDateString(Calendar.DATE, -1);
		UrlBuilder urlBuilder = new UrlBuilder().s("songs").s("leaf").p("order", "liking_num").start(startDate);
		getGradualLoader().setUrlBuilder(urlBuilder);
		final CollaboratedAdapter adapter = new CollaboratedAdapter();
		setAdapter(adapter);
		getGradualLoader().setOnLoadCompleteListener(new OnLoadCompleteListener() {
			
			@Override
			public void onComplete(JSONArray response) {
				setListShown(true);
				adapter.addAll(response);
			}
		});
		getGradualLoader().load();
	}

}
