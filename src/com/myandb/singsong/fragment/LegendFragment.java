package com.myandb.singsong.fragment;

import java.util.Calendar;

import com.myandb.singsong.R;
import com.myandb.singsong.adapter.CollaboratedAdapter;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.util.StringFormatter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class LegendFragment extends Fragment {
	
	private ImageView ivPreviousWeek;
	private ImageView ivNextWeek;
	private TextView tvCurrentWeek;
	private ListView listview;
	private CollaboratedAdapter adapter;
	private int currentIndex;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_legend, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		View view = getView();
		
		ivPreviousWeek = (ImageView) view.findViewById(R.id.iv_previous_week);
		ivNextWeek = (ImageView) view.findViewById(R.id.iv_next_week);
		tvCurrentWeek = (TextView) view.findViewById(R.id.tv_current_week);
		listview = (ListView) view.findViewById(R.id.lv_full_width);
		
//		adapter = new CollaboratedAdapter(getActivity());
		listview.setAdapter(adapter);
		
		fetchLegend(currentIndex);
		
		ivPreviousWeek.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				currentIndex--;
				fetchLegend(currentIndex);
			}
		});
		ivNextWeek.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				currentIndex++;
				fetchLegend(currentIndex);
			}
		});
	}
	
	private String getStartDate(int index) {
		return StringFormatter.getDateString(Calendar.WEEK_OF_YEAR, (index - 1), Calendar.SUNDAY);
	}
	
	private String getEndDate(int index) {
		return StringFormatter.getDateString(Calendar.WEEK_OF_YEAR, index, Calendar.SUNDAY);
	}
	
	private void fetchLegend(int index) {
		String start = getStartDate(index);
		String end = getEndDate(index);
		
		tvCurrentWeek.setText(start);
		tvCurrentWeek.append(" ~ ");
		tvCurrentWeek.append(end);
		
//		adapter.resetRequest(
//			new UrlBuilder().s("songs").s("leaf").start(start).end(end).p("order", "liking_num").take(10)
//		);
	}

}
