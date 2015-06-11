package com.myandb.singsong;

import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebookConfiguration;

public class FacebookConfig {
	
	public static final String NAMESPACE = "collabokaraoke_";
	
	public static final String APP_ID = "186972151505046";
	
	public static final SimpleFacebookConfiguration getConfig() {
		Permission[] permissions = new Permission[] {
				Permission.PUBLIC_PROFILE,
				Permission.EMAIL,
				Permission.USER_FRIENDS,
				Permission.PUBLISH_ACTION
		};
		
		SimpleFacebookConfiguration config = new SimpleFacebookConfiguration.Builder()
			.setNamespace(NAMESPACE)
			.setAppId(APP_ID)
			.setPermissions(permissions)
			.build();
		
		return config;
	}

}
