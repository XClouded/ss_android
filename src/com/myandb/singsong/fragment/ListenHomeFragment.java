package com.myandb.singsong.fragment;

import java.util.Calendar;

import android.app.Activity;
import android.widget.ListAdapter;

import com.myandb.singsong.adapter.CollaboratedAdapter;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.StringFormatter;

public class ListenHomeFragment extends ListFragment {

	@Override
	protected ListAdapter instantiateAdapter(Activity activity) {
		return new CollaboratedAdapter();
	}

	@Override
	protected UrlBuilder instantiateUrlBuilder(Activity activity) {
		final String startDate = StringFormatter.getDateString(Calendar.DATE, -1);
		return new UrlBuilder().s("songs").s("leaf").p("order", "liking_num").start(startDate);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		/*
		setActionBarTitle("");
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		RelativeLayout relative=new RelativeLayout(getApplicationContext());
		TextView tv = new TextView(getApplicationContext());
		tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Logger.log("asdfasdf");
			}
		});
		tv.setText("Asdfasdf");
		relative.addView(tv);
		getSupportActionBar().setCustomView(relative);
		*/
	}

}
