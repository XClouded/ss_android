package com.myandb.singsong.fragment;

import com.facebook.Session;
import com.myandb.singsong.R;
import com.myandb.singsong.secure.Authenticator;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

public class HomeFragment extends BaseFragment {
	
	@Override
	protected int getResourceId() {
		return R.layout.fragment_home;
	}

	@Override
	protected void onViewInflated(View view, LayoutInflater inflater) {
		view.findViewById(R.id.btn_logout).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Authenticator().logout();
				Session session = Session.getActiveSession();
				if (session != null) {
					session.closeAndClearTokenInformation();
				}
				getActivity().finish();
			}
		});
	}

	@Override
	protected void initialize(Activity activity) {
		
	}

	@Override
	protected void setupViews() {
		
	}

	@Override
	protected void onDataChanged() {
		
	}

}
