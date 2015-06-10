package com.myandb.singsong.net;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.myandb.singsong.dialog.MelonAlertDialog;
import com.myandb.singsong.net.MelonMemberResponse.AlertInfo;
import com.myandb.singsong.net.MelonStreamingResponse.FailCode;
import com.myandb.singsong.secure.Authenticator;
import com.myandb.singsong.util.Utility;

import android.content.Context;
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
					AlertInfo info = new AlertInfo(); 
					JSONArray failCodeArray = response.getJSONArray(MelonStreamingResponse.KEY_FAILCODELIST);
					List<FailCode> failCodeList = new ArrayList<FailCode>();
					for (int i = 0, l = failCodeArray.length(); i < l; i++) {
						try {
							String failCodeString = failCodeArray.getString(i);
							FailCode failCode = FailCode.valueOf(failCodeString);
							failCodeList.add(failCode);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					if (failCodeList.contains(FailCode.NOREALNAME)) {
						throw new MelonAuthorizationException();
					} else if (failCodeList.contains(FailCode.ADULTONLY)) {
						info.MESSAGE = FailCode.ADULTONLY.getMessage();
					} else if (failCodeList.contains(FailCode.NOPRODUCT)) {
						info.MESSAGE = FailCode.NOPRODUCT.getMessage();
						info.PAGEURL = "";
						info.OKTITLE = "이용권 구매";
					} else {
						if (failCodeList.size() > 0) {
							info.MESSAGE = failCodeList.get(0).getMessage();
						}
					}
					
					MelonAlertDialog.show(fm, info);
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
						} else if (response.has("isJuvenileProtection")) {
							String isJuvenileProtection = response.getString("isJuvenileProtection");
							if (isJuvenileProtection.equals("Y")) {
								return;
							} else {
								String viewType = response.getString("viewType");
								String memberKey = Authenticator.getUser().getMelonId();
								String url = "https://m.melon.com:4554/muid/common/android2/realnameauthentication_inform.htm";
								url += "?memberKey=" + memberKey;
								url += "&viewType=" + viewType;
								url += "&returnUrl=" + "singsong://root";
								
								info.PAGEURL = url;
								info.POPUPTYPE = "confirm";
								info.MESSAGE = response.getString("message");
							}
						}
						
						MelonAlertDialog.show(fm, info);
					} else {
						Toast.makeText(context, errorMesg, Toast.LENGTH_LONG).show();
					}
					
					throw new MelonResponseException();
				}
			} else if (response.has(MelonMemberResponse.KEY_OPTION)) {
				if (fm != null) {
					Gson gson = Utility.getGsonInstance();
					JSONObject option = response.getJSONObject(MelonMemberResponse.KEY_OPTION);
					AlertInfo info = gson.fromJson(option.toString(), AlertInfo.class);
					MelonAlertDialog.show(fm, info);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new MelonResponseException();
		}
	}
	
	public static class MelonResponseException extends Exception {

		private static final long serialVersionUID = -3027793848233491224L;
		
	}
	
	public static class MelonAuthorizationException extends MelonResponseException {

		private static final long serialVersionUID = 4966940382118253832L;
		
		
	}
	
}
