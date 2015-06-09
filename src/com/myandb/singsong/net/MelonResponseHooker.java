package com.myandb.singsong.net;

import org.json.JSONArray;
import org.json.JSONObject;

import com.myandb.singsong.dialog.BaseDialog;
import com.myandb.singsong.dialog.MelonAlertDialog;
import com.myandb.singsong.net.MelonMemberResponse.AlertInfo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

public class MelonResponseHooker {

	private MelonResponseHooker() {}
	
	public static void hook(Context context, JSONObject response) throws MelonResponseException {
		hook(context, null, response);
	}
	
	public static void hook(Context context, FragmentManager fm, JSONObject response) throws MelonResponseException {
		try {
			if (response.has(MelonStreamingResponse.KEY_RESULTYN)) {
				String yesOrNo = response.getString(MelonStreamingResponse.KEY_RESULTYN);
				if (yesOrNo.equals("N")) {
					JSONArray failCodeList = response.getJSONArray(MelonStreamingResponse.KEY_FAILCODELIST);
					String result = "";
					for (int i = 0, l = failCodeList.length(); i < l; i++) {
						result += failCodeList.getString(i);
					}
					Toast.makeText(context, result, Toast.LENGTH_LONG).show();
					
					throw new MelonResponseException(); 
				}
			} else if (response.has(MelonMemberResponse.KEY_RESULT_CODE)) {
				String resultCode = response.getString(MelonMemberResponse.KEY_RESULT_CODE);
				if (resultCode.equals("-1")) {
					String errorCode = response.getString("errorCode");
					String errorMesg = response.getString("errorMesg");
					
					if (fm != null) {
						AlertInfo info = new AlertInfo();
						info.MESSAGE = errorMesg;
						
						if (errorCode.equals("ERL003")) {
							info.PAGEURL = "https://m.melon.com:4554/muid/search/android2/passwordsearch_inform.htm";
							info.OKTITLE = "비밀번호찾기";
						} else {
							info.PAGEURL = "";
							info.OKTITLE = "확인";
						}
						
						MelonAlertDialog.show(fm, info);
					} else {
						Toast.makeText(context, errorMesg, Toast.LENGTH_LONG).show();
					}
					
					throw new MelonResponseException();
				}
			} else if (response.has(MelonMemberResponse.KEY_OPTION)) {
				if (fm != null) {
					Bundle bundle = new Bundle();
					bundle.putString(AlertInfo.class.getName(), response.getJSONObject(MelonMemberResponse.KEY_OPTION).toString());
					
					BaseDialog dialog = new MelonAlertDialog();
					dialog.setArguments(bundle);
					dialog.show(fm, "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new MelonResponseException();
		}
	}
	
	public static final class MelonResponseException extends Exception {

		private static final long serialVersionUID = 1L;
		
	}
	
}
