package com.myandb.singsong.activity;

import com.google.gson.Gson;
import com.myandb.singsong.R;
import com.myandb.singsong.fragment.ProfileRootFragment;
import com.myandb.singsong.model.User;
import com.myandb.singsong.util.Utility;

import android.os.Bundle;

public class ProfileRootActivity extends OldBaseActivity {
	
	public static final String INTENT_USER = "_user_";
	
	private static final int MAX_ACTIVITY_INSTANCE_NUM = 5;
	
	private static int activityCreated = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		activityCreated();
		
		if (activityCreated > MAX_ACTIVITY_INSTANCE_NUM) {
			finish();
		} else {
			Gson gson = Utility.getGsonInstance();
			String userInJson = getIntent().getStringExtra(INTENT_USER);
			User user = gson.fromJson(userInJson, User.class);
			
			if (user != null) {
				ProfileRootFragment fragment = new ProfileRootFragment();
				
				fragment.setUser(user);
				replaceFragment(fragment);
			} else {
				finish();
			}
		}
	}
	
	private void activityCreated() {
		activityCreated++;
	}
	
	private void activityDestroyed() {
		activityCreated--;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		activityDestroyed();
	}

	@Override
	protected int getChildLayoutResourceId() {
		return R.layout.common_fragment_container;
	}

	@Override
	protected boolean isRootActivity() {
		return false;
	}

	@Override
	protected boolean enablePlayingThumb() {
		return true;
	}

}
