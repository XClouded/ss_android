package com.myandb.singsong.file;

import android.content.Context;
import android.content.SharedPreferences;

import com.myandb.singsong.model.Notice;
import com.myandb.singsong.util.TimeHelper;
import com.myandb.singsong.util.Utility;

public class Storage {
	
	private static final String FILE_NAME = "_SSaS_";
	private static final String KEY_NOTICE = "_notice_";
	private static final String KEY_NOTICE_UNREAD = "_notice_unread_";
	private static final String KEY_UPDATE = "_update_";
	private static final String KEY_ALLOW_PUSH = "_push_";
	private static final String KEY_UNREAD_PUSH_NUM = "_push_num_";
	private static final String KEY_PLAYER_LOOPING = "_player_loop_";
	private static final String KEY_PLAYER_AUTOPLAY = "_player_autoplay_";
	private static final String INVALID_KEY = "";
	
	private static SharedPreferences preferences;
	
	public static void initialize(Context context) {
		preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
	}
	
	public static SharedPreferences getInstance() {
		return preferences;
	}
	
	public boolean hasCheckedUpdate() {
		try {
			int checkedDate = preferences.getInt(KEY_UPDATE, -1);
			
			if (TimeHelper.getTodayInNumber() > checkedDate) {
				return false;
			}
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	public void checkUpdate() {
		putInt(KEY_UPDATE, TimeHelper.getTodayInNumber());
	}
	
	public boolean isAllowPush() {
		try {
			return preferences.getBoolean(KEY_ALLOW_PUSH, true);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	public void setAllowPush(boolean isAllow) {
		preferences.edit()
				   .putBoolean(KEY_ALLOW_PUSH, isAllow)
				   .commit();
	}
	
	public void arriveNotice(Notice notice) {
		if (notice != null) {
			try {
				String savedNoticeInJson = preferences.getString(KEY_NOTICE, INVALID_KEY);
				
				if (savedNoticeInJson.equals(INVALID_KEY)) {
					saveNotice(notice);
				} else {
					Notice savedNotice = Utility.getGsonInstance().fromJson(savedNoticeInJson, Notice.class);
					
					if (savedNotice.getId() != notice.getId()) {
						saveNotice(notice);
					}
				}
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void saveNotice(Notice notice) {
		preferences.edit()
				   .putString(KEY_NOTICE, Utility.getGsonInstance().toJson(notice, Notice.class))
				   .putBoolean(KEY_NOTICE_UNREAD, true)
				   .commit();
	}
	
	public Notice getUnreadNotice() {
		try {
			if (preferences.getBoolean(KEY_NOTICE_UNREAD, false)) {
				String savedNoticeInJson = preferences.getString(KEY_NOTICE, INVALID_KEY);
				
				if (!savedNoticeInJson.equals(INVALID_KEY)) {
					return Utility.getGsonInstance().fromJson(savedNoticeInJson, Notice.class);
				}
			}
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void readNotice() {
		preferences.edit()
				   .putBoolean(KEY_NOTICE_UNREAD, false)
				   .commit();
	}
	
	public int getUnreadPushNum() {
		try {
			return preferences.getInt(KEY_UNREAD_PUSH_NUM, 0);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public void arriveNewPush() {
		int unreadPushNum = getUnreadPushNum();
		unreadPushNum++;
		
		putInt(KEY_UNREAD_PUSH_NUM, unreadPushNum);
	}
	
	public void readAllNoti() {
		putInt(KEY_UNREAD_PUSH_NUM, 0);
	}
	
	public void setPlayerLooping(boolean enable) {
		preferences.edit()
				   .putBoolean(KEY_PLAYER_LOOPING, enable)
				   .commit();
	}
	
	public boolean isPlayerLooping() {
		try {
			return preferences.getBoolean(KEY_PLAYER_LOOPING, false);
		} catch (ClassCastException e) {
			return false;
		}
	}
	
	public void setPlayerAutoplay(boolean enable) {
		preferences.edit()
				   .putBoolean(KEY_PLAYER_AUTOPLAY, enable)
				   .commit();
	}
	
	public boolean isPlayerAutoplay() {
		try {
			return preferences.getBoolean(KEY_PLAYER_AUTOPLAY, true);
		} catch (ClassCastException e) {
			return true;
		}
	}
	
	public void putInt(String key, int value) {
		preferences.edit()
				   .putInt(key, value)
				   .commit();
	}
	
	public void putString(String key, String value) {
		preferences.edit()
				   .putString(key, value)
				   .commit();
	}
	
	public String getString(String key) {
		try {
			return preferences.getString(key, null);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
