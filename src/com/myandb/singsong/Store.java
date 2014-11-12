package com.myandb.singsong;

import android.net.Uri;

public interface Store {
	
	public Uri getDetailViewUri(String packageName);
	
	public String getPackageName();

}
