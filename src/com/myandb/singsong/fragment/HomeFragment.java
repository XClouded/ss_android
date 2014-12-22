package com.myandb.singsong.fragment;

import com.facebook.Session;
import com.myandb.singsong.R;
import com.myandb.singsong.activity.BaseActivity;
import com.myandb.singsong.activity.UpActivity;
import com.myandb.singsong.adapter.NotificationAdapter;
import com.myandb.singsong.secure.Authenticator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class HomeFragment extends BaseFragment {
	
	private TextView tvNotificationCount;
	
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
		setHasOptionsMenu(true);
	}

	@Override
	protected void setupViews(Bundle savedInstanceState) {
		
	}

	@Override
	protected void onDataChanged() {
		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.home, menu);
		
		View notificationView = getActionViewCompat(menu, R.id.action_notification);
		if (notificationView != null) {
			notificationView.setOnClickListener(notificationClickListener);
			tvNotificationCount = (TextView) notificationView.findViewById(R.id.tv_action_notification_count);
			updateNotificationCount();
			registerNotificationCountListener();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_sing:
			return true;
		
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private OnClickListener notificationClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Bundle params = new Bundle();
			params.putString("order", "updated_at");
			String userId = String.valueOf(Authenticator.getUser().getId());
			Bundle bundle = new Bundle();
			bundle.putString(BaseFragment.EXTRA_FRAGMENT_TITLE, "새로운 소식");
			bundle.putString(ListFragment.EXTRA_URL_SEGMENT, "users/" + userId + "/notifications");
			bundle.putBundle(ListFragment.EXTRA_QUERY_PARAMS, params);
			bundle.putString(ListFragment.EXTRA_ADAPTER_NAME, NotificationAdapter.class.getName());
			Intent intent = new Intent(getActivity(), UpActivity.class);
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_NAME, ListFragment.class.getName());
			intent.putExtra(BaseActivity.EXTRA_FRAGMENT_BUNDLE, bundle);
			startFragment(intent);
		}
	};
	
	private View getActionViewCompat(Menu menu, int id) {
		return MenuItemCompat.getActionView(menu.findItem(id));
	}
	
	private void updateNotificationCount() {
		int count = getCurrentNotificationCount();
		setNotificationNum(count, tvNotificationCount);
	}
	
	private int getCurrentNotificationCount() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String key = getString(R.string.key_notification_count);
		return preferences.getInt(key, 0);
	}
	
	private void setNotificationNum(int count, TextView textView) {
		if (textView != null) {
			if (count > 0) {
				count = Math.min(count, 99);
				textView.setVisibility(View.VISIBLE);
				textView.setText(String.valueOf(count));
			} else {
				textView.setVisibility(View.GONE);
			}
		}
	}
	
	private void registerNotificationCountListener() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		preferences.registerOnSharedPreferenceChangeListener(notificationCountChangeListener);
	}
	
	private OnSharedPreferenceChangeListener notificationCountChangeListener = new OnSharedPreferenceChangeListener() {
		
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			if (isAdded()) {
				String countkey = getString(R.string.key_notification_count);
				if (key.equals(countkey)) {
					updateNotificationCount();
				}
			}
		}
	};

}
