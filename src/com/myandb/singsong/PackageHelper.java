package com.myandb.singsong;

import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class PackageHelper {
	
	private PackageManager packageManager;
	
	public PackageHelper(PackageManager packageManager) {
		this.packageManager = packageManager;
	}
	
	public boolean isAppInstalled(String packageName) {
		try {
			packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
		return true;
	}
	
	public int getVersionCode(String packageName) {
		try {
			return packageManager.getPackageInfo(packageName, 0).versionCode;
		} catch (NameNotFoundException e) {
			return 0;
		}
	}

}
