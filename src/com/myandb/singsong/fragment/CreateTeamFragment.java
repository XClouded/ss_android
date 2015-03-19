package com.myandb.singsong.fragment;

import com.myandb.singsong.R;
import com.myandb.singsong.model.Category;
import com.myandb.singsong.model.Gender;
import com.myandb.singsong.pager.CreateTeamPagerAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CreateTeamFragment extends BaseFragment {
	
	private ViewPager viewPager;
	private Button btnPrevious;
	private Button btnNext;
	private TextView tvTeamDescription;
	private TeamInformation information;
	private CreateTeamPagerAdapter adapter;

	@Override
	protected int getResourceId() {
		return R.layout.fragment_create_team;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		viewPager = (ViewPager) view.findViewById(R.id.viewpager);
		btnPrevious = (Button) view.findViewById(R.id.btn_previous);
		btnNext = (Button) view.findViewById(R.id.btn_next);
		tvTeamDescription = (TextView) view.findViewById(R.id.tv_team_description);
	}

	@Override
	protected void initialize(Activity activity) {
		information = new TeamInformation();
		information.setOnValueChangedListener(new OnValueChangedListener() {
			
			@Override
			public void OnChanged() {
				updateDescription();
			}
		});
		adapter = new CreateTeamPagerAdapter(getChildFragmentManager());
		adapter.setTeamInformation(information);
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(pageChangeListener);
		
		btnPrevious.setOnClickListener(previousClickListener);
		btnNext.setOnClickListener(nextClickListener);
	}
	
	private OnClickListener previousClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int currentItem = viewPager.getCurrentItem();
			if (currentItem == 0) {
				Toast.makeText(getActivity(), "page 0", Toast.LENGTH_SHORT).show();
				// finish
			} else {
				viewPager.setCurrentItem(currentItem - 1, true);
			}
		}
		
	};
	
	private OnClickListener nextClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int currentItem = viewPager.getCurrentItem();
			if (currentItem == viewPager.getAdapter().getCount() - 1) {
				Toast.makeText(getActivity(), "page last", Toast.LENGTH_SHORT).show();
				// make
			} else {
				if (adapter.isValidated(currentItem)) {
					viewPager.setCurrentItem(currentItem + 1, true);
				}
			}
		}
	};
	
	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int page) {
			updateDescription(page);
		}
		
		@Override
		public void onPageScrolled(int page, float arg1, int arg2) {}
		
		@Override
		public void onPageScrollStateChanged(int page) {}
	};
	
	private void updateDescription() {
		int currentItem = viewPager.getCurrentItem();
		updateDescription(currentItem);
	}
	
	private void updateDescription(int page) {
		if (page == 0) {
			// update action bar
		}
		
		tvTeamDescription.setText(getDescription(page, ""));
	}
	
	private String getDescription(int page, String root) {
		String added = "";
		
		switch (page) {
		case 1:
			added = String.valueOf(information.getMemberMaxNum()) + "인조";
			break;
			
		case 2:
			added += " ";
			added += information.getGender().getTitle();
			break;
			
		case 3:
			added += " ";
			added += information.getCategory().getTitle();
			added += "그룹";
			break;
			
		default:
			return root;
		}
		
		return getDescription(page - 1, added + root);
	}

	@Override
	protected void onDataChanged() {}
	
	public static class TeamInformation {
		
		private static final int DEFAULT_MEMBER_MAX_NUM = 3;
		private static final Category DEFAULT_CATEGORY = new Category(1);
		private static final Gender DEFAULT_GENDER = Gender.BOYS;
		
		private String title = "";
		private int memberMaxNum = DEFAULT_MEMBER_MAX_NUM;
		private Category category = DEFAULT_CATEGORY; 
		private Gender gender = DEFAULT_GENDER;
		private OnValueChangedListener listener;
		
		public void setTitle(String title) {
			if (isProperTitle(title)) {
				this.title = title;
				dispatchListener();
			}
		}
		
		public String getTitle() {
			return title;
		}
		
		public void setMemberMaxNum(int num) {
			if (isProperMemberMaxNum(num)) {
				this.memberMaxNum = num;
				dispatchListener();
			}
		}
		
		public int getMemberMaxNum() {
			return memberMaxNum;
		}
		
		public void setCategory(Category category) {
			if (isProperCategory(category)) {
				this.category = category;
				dispatchListener();
			}
		}
		
		public Category getCategory() {
			return category;
		}
		
		public void setGender(Gender gender) {
			if (isProperGender(gender)) {
				this.gender = gender;
				dispatchListener();
			}
		}
		
		public Gender getGender() {
			return gender;
		}
		
		private boolean isProperTitle(String title) {
			return !"".equals(title);
		}
		
		private boolean isProperMemberMaxNum(int num) {
			return num > 0;
		}
		
		private boolean isProperCategory(Category category) {
			return category != null && category.canRepresentTeam();
		}
		
		private boolean isProperGender(Gender gender) {
			return gender != null && !gender.equals(Gender.NULL);
		}
		
		public void setOnValueChangedListener(OnValueChangedListener listener) {
			this.listener = listener;
		}
		
		private void dispatchListener() {
			if (listener != null) {
				listener.OnChanged();
			}
		}
		
	}
	
	public interface OnValueChangedListener {
		
		public void OnChanged();
		
	}
	
}
