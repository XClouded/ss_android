package com.myandb.singsong.net;

import java.util.List;

public class MelonResponse {
	
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
	
	public static final class StreamingAuth {
		
		public enum FailCodeList {
			
			NOPRODUCT,
			
			ADULTONLY,
			
			NOREALNAME,
			
			NORIGHT,
			
			NOSVC,
			
			EXISTCHANNELBANNEDPROD,
			
			NOAVAILST,
			
			NOFREE,
			
			WRONGPARAMS
			
		}
		
		public String RESULTYN;
		public List<String> FAILCODELIST;
		
	}

}
