package com.myandb.singsong.net;

import org.json.JSONException;
import org.json.JSONObject;

import com.myandb.singsong.dialog.BaseDialog;
import com.myandb.singsong.dialog.MelonAlertDialog;
import com.myandb.singsong.net.MelonResponseHooker.MelonResponse.MelonAlertInfo;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

public class MelonResponseHooker {

	private MelonResponseHooker() {}
	
	public static void hook(FragmentManager fm, JSONObject response) {
		try {
			if (response.has(MelonResponse.KEY_OPTION)) {
				Bundle bundle = new Bundle();
				bundle.putString(MelonAlertInfo.class.getName(), response.getJSONObject(MelonResponse.KEY_OPTION).toString());
				
				BaseDialog dialog = new MelonAlertDialog();
				dialog.setArguments(bundle);
				dialog.show(fm, "");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static final class MelonResponse {
		
		public static final String KEY_RESULT_CODE = "resultCode";
		public static final String KEY_OPTION = "OPTION";
		public static final String RESULT_SUCCESS = "000000";
		public static final String RESULT_FAIL = "-1";
		
		public static final class MelonAlertInfo {
			
			public String ACTION;
			public String MESSAGE;
			public String POPUPTYPE;
			public String PAGEURL;
			public String LINKTYPE;
			public String LINKTITLE;
			public String OKTITLE;
			
		}
	}
	
	
}
