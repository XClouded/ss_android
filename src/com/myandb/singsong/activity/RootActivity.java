package com.myandb.singsong.activity;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class RootActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Fragment fragment = getFragmentFromIntent(intent);
		setFragment(fragment);
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		startService(playerIntent);
	}

	@Override
	protected void onPlayerServiceConnected(Service service) {
		// TODO Auto-generated method stub
		
	}

}
