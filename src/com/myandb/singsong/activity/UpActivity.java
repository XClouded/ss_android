package com.myandb.singsong.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.myandb.singsong.R;
import com.myandb.singsong.service.PlayerService;

public class UpActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// getIntent()
		// request no title
		
		setContentView(R.layout.activity_up);
	}

	@Override
	public void onPlayerServiceConnected(PlayerService service) {
		
	}

	@Override
	public void onPageChanged(Intent intent) {
		if (isComponentOf(intent, RootActivity.class)) {
			// call root
		} else if (isComponentOf(intent, UpActivity.class)) {
			startActivity(intent);
		} else {
			try {
				Fragment fragment = instantiateFragmentFromIntent(intent);
				replaceContentFragment(fragment);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
