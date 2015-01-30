package com.myandb.singsong.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ListAdapter;

import com.myandb.singsong.R;
import com.myandb.singsong.adapter.NotificationAdapter;
import com.myandb.singsong.net.UrlBuilder;
import com.myandb.singsong.secure.Authenticator;

public class NotificationFragment extends ListFragment {

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		super.setupViews(savedInstanceState);
		setCurrentNotificationCount(0);
	}

	@Override
	protected ListAdapter instantiateAdapter(Activity activity) {
		return new NotificationAdapter();
	}

	@Override
	protected UrlBuilder instantiateUrlBuilder(Activity activity) {
		if (Authenticator.isLoggedIn()) {
			int id = Authenticator.getUser().getId();
			return new UrlBuilder().s("users").s(id).s("notifications").p("order", "updated_at");
		}
		return null;
	}

	@Override
	public void onResume() {
		super.onResume();
		setActionBarTitle(getString(R.string.fragment_notification_action_title));
	}
	
	private void setCurrentNotificationCount(int count) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String key = getString(R.string.key_notification_count);
		preferences.edit().putInt(key, count).commit();
	}

}
