package com.myandb.singsong.fragment;

import java.util.ArrayList;
import java.util.List;

import com.myandb.singsong.R;
import com.myandb.singsong.pager.TutorialPagerAdapter;
import com.myandb.singsong.secure.Authenticator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class TutorialFragment extends Fragment {
	
	private ViewPager viewPager;
	private TutorialPagerAdapter pagerAdapter;
	private ImageView ivNext;
	private List<ImageView> pagination;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		pagerAdapter = new TutorialPagerAdapter(getActivity());
		pagination = new ArrayList<ImageView>();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_tutorial, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		View view = getView();
		
		ivNext = (ImageView) view.findViewById(R.id.iv_next);
		viewPager = (ViewPager) view.findViewById(R.id.view_pager);
		pagination.add((ImageView) view.findViewById(R.id.iv_pagination_1));
		pagination.add((ImageView) view.findViewById(R.id.iv_pagination_2));
		pagination.add((ImageView) view.findViewById(R.id.iv_pagination_3));
		pagination.add((ImageView) view.findViewById(R.id.iv_pagination_4));
		pagination.add((ImageView) view.findViewById(R.id.iv_pagination_5));
		
		ivNext.setOnClickListener(nextClickListener);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int pageNum) {
				int pageLength = pagination.size();
				if (pageLength == pagerAdapter.getCount()) {
					
					ImageView iPage;
					for (int i = 0; i < pageLength; i++) {
						iPage = pagination.get(i);
						iPage.setImageResource(R.drawable.circle_white);
					}
					
					pagination.get(pageNum).setImageResource(R.drawable.circle_grey);
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) { }
			
			@Override
			public void onPageScrollStateChanged(int arg0) { }
			
		});
	}

	public void onBackPressed() {
		int currentPosition = viewPager.getCurrentItem();
		
		if (currentPosition > 0) {
			viewPager.setCurrentItem(currentPosition - 1);
		} else if (currentPosition == 0) {
			getActivity().finish();
		}
	}
	
	private OnClickListener nextClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int currentPosition = viewPager.getCurrentItem();
			int lastPosition = pagerAdapter.getCount() - 1;
			
			if (currentPosition < lastPosition) {
				viewPager.setCurrentItem(currentPosition + 1);
			} else if (currentPosition == lastPosition) {
				if (Authenticator.isLoggedIn()) {
					getActivity().finish();
				} else {
					try {
//						OldBaseActivity parent = (OldBaseActivity) getActivity();
//						parent.replaceFragment(new LoginFragment());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	};
	
}
