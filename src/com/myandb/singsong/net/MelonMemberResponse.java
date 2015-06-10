package com.myandb.singsong.net;

public class MelonMemberResponse {
	
	public static final String KEY_RESULT_CODE = "resultCode";
	public static final String KEY_OPTION = "OPTION";
	public static final String RESULT_SUCCESS = "000000";
	public static final String RESULT_FAIL = "-1";
	
	public static final class AlertInfo {
		
		public String ACTION;
		public String MESSAGE;
		public String POPUPTYPE;
		public String PAGEURL;
		public String LINKTYPE;
		public String LINKTITLE;
		public String OKTITLE;
		
		public AlertInfo() {
			this.PAGEURL = "";
			this.OKTITLE = "»Æ¿Œ";
		}
		
	}

}
