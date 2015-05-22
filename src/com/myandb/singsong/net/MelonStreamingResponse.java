package com.myandb.singsong.net;

public class MelonStreamingResponse {
	
	public enum FailCodeList {
		
		NOPRODUCT,
		
		ADULTONLY,
		
		NOREALNAME,
		
		NORIGHT,
		
		NOSVC,
		
		EXISTCHANNELBANNEDPROD,
		
		NOAVAILST,
		
		NOFREE,
		
		WRONGPARAMS,
		
		VALERROR,
		
		ETC,
		
		LOGINTOKEN
		
	}
	
	public static final String KEY_RESULTYN = "RESULTYN";
	public static final String KEY_FAILCODELIST = "FAILCODELIST";
	
}