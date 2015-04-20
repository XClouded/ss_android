package com.myandb.singsong.fragment;

import com.myandb.singsong.R;
import com.myandb.singsong.model.Category;
import com.myandb.singsong.model.Gender;
import com.myandb.singsong.pager.CreateTeamPagerAdapter;
import com.myandb.singsong.util.Logger;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class CreateTeamFragment extends BaseFragment {
	
	private ViewPager viewPager;
	private TeamInformation information;
	private CreateTeamPagerAdapter adapter;
	private Menu optionsMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	protected int getResourceId() {
		return R.layout.fragment_create_team;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		viewPager = (ViewPager) view.findViewById(R.id.viewpager);
	}

	@Override
	protected void initialize(Activity activity) {
		information = new TeamInformation();
		adapter = new CreateTeamPagerAdapter(getChildFragmentManager());
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(pageChangeListener);
	}
	
	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int page) {
			resetOptionsMenu();
			if (page > 0) {
				addPreviousOptionMenu();
			}
			
			if (isLastPage(page, viewPager)) {
				addCreateOptionMenu();
			} else {
				addNextOptionMenu();
			}
		}
		
		private boolean isLastPage(int page, ViewPager viewPager) {
			return page == viewPager.getAdapter().getCount() - 1;
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {}
		
		@Override
		public void onPageScrollStateChanged(int arg0) {}
	};
	
	private void resetOptionsMenu() {
		setOptionMenuVisible(R.id.action_previous, false);
		setOptionMenuVisible(R.id.action_next, false);
		setOptionMenuVisible(R.id.action_create, false);
	}
	
	private void setOptionMenuVisible(int id, boolean visible) {
		if (optionsMenu != null) {
			MenuItem item = optionsMenu.findItem(id);
			if (item != null) {
				item.setVisible(visible);
			}
		}
	}
	
	private void addPreviousOptionMenu() {
		setOptionMenuVisible(R.id.action_previous, true);
	}
	
	private void addNextOptionMenu() {
		setOptionMenuVisible(R.id.action_next, true);
	}
	
	private void addCreateOptionMenu() {
		setOptionMenuVisible(R.id.action_create, true);
	}

	@Override
	protected void onDataChanged() {}
	
	@Override
	public void onResume() {
		super.onResume();
		setActionBarTitle("ÆÀ ¸¸µé±â");
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.create_team, menu);
		optionsMenu = menu;
		pageChangeListener.onPageSelected(0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int currentItem = viewPager.getCurrentItem();
		int targetItem = currentItem;
		
		switch (item.getItemId()) {
		case R.id.action_previous:
			targetItem -= 1;
			break;
			
		case R.id.action_next:
		case R.id.action_create:
			targetItem += 1;
			
			OnTeamInformationUpdated updating = (OnTeamInformationUpdated) adapter.getCurrentItem(currentItem);
			if (updating != null) {
				updating.onUpdated(information);
			}
			break;
			
		default:
			return false;
		}
		
		if (targetItem > 0 || targetItem < viewPager.getAdapter().getCount()) {
			viewPager.setCurrentItem(targetItem, true);
		}
		return true;
	}

	public interface OnTeamInformationUpdated {
		
		public boolean onUpdated(TeamInformation information);
		
	}
	
	public static class TeamInformation {
		
		private static final int DEFAULT_MEMBER_MAX_NUM = 3;
		private static final Category DEFAULT_CATEGORY = new Category(1);
		private static final Gender DEFAULT_GENDER = Gender.BOYS;
		
		private String title = "";
		private int memberMaxNum = DEFAULT_MEMBER_MAX_NUM;
		private Category category = DEFAULT_CATEGORY; 
		private Gender gender = DEFAULT_GENDER;
		
		public void setTitle(String title) {
			if (isProperTitle(title)) {
				this.title = title;
			}
		}
		
		public String getTitle() {
			return title;
		}
		
		public void setMemberMaxNum(int num) {
			if (isProperMemberMaxNum(num)) {
				this.memberMaxNum = num;
			}
		}
		
		public int getMemberMaxNum() {
			return memberMaxNum;
		}
		
		public void setCategory(Category category) {
			if (isProperCategory(category)) {
				this.category = category;
			}
		}
		
		public Category getCategory() {
			return category;
		}
		
		public void setGender(Gender gender) {
			if (isProperGender(gender)) {
				this.gender = gender;
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
		
	}
	
}
