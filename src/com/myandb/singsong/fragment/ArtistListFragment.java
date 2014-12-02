package com.myandb.singsong.fragment;

import java.util.Calendar;

import com.myandb.singsong.adapter.CollaboratedAdapter;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.StringFormatter;

public class ArtistListFragment extends ListFragment {

	@Override
	protected void onDataChanged() {
		final String startDate = StringFormatter.getDateString(Calendar.DATE, -1);
		UrlBuilder urlBuilder = new UrlBuilder().s("songs").s("leaf").p("order", "liking_num").start(startDate);
		
		setUrlBuilder(urlBuilder);
		setAdapter(new CollaboratedAdapter());
		load();
	}

}
