package com.myandb.singsong;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public abstract class StoreBase implements Store {
	
	private String appPackageName;
	
	public StoreBase(String packageName) {
		this.appPackageName = packageName;
	}

	@Override
	public void move(Activity activity) {
		Uri uri = getDetailViewUri();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(uri);
		activity.startActivity(intent);
	}
	
	public String getAppPackageName() {
		return appPackageName;
	}
	
	public abstract Uri getDetailViewUri();

}
