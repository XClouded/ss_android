package com.myandb.singsong.net;

public class MelonStreamingResponse {
	
	public enum FailCode {
		
		NOPRODUCT("회원님은 현재 미리듣기만 가능합니다. 모바일 스트리밍이 가능한 이용권 구매 후 사용해주세요."),
		
		ADULTONLY("이 정보내용은 청소년유해매체물로서 -정보통신망 이용촉진 및 정보보호 등에 관한 법률- 및 -청소년 보호법-에 따라 19세 미만의 청소년이 이용할 수 없습니다."),
		
		NOREALNAME("이 정보내용은 청소년유해매체물로서 -정보통신망 이용촉진 및 정보보호 등에 관한 법률- 및 -청소년 보호법-에 따라 19세 미만의 청소년이 이용할 수 없습니다. 성인인증 후 이용해주세요."),
		
		NORIGHT("상품과 콘텐츠 간의 권환관계가 없습니다."),
		
		NOSVC("서비스 불가한 콘텐츠입니다."),
		
		EXISTCHANNELBANNEDPROD("채널제약에 걸려 있는 상품입니다."),
		
		NOAVAILST("콘텐츠가 스트리밍 불가 상태입니다."),
		
		NOFREE("회원님은 현재 미리듣기만 가능합니다. 모바일 스트리밍이 가능한 이용권 구매 후 사용해주세요."),
		
		WRONGPARAMS("호출 파라미터 오류"),
		
		VALERROR("VAL 값 맞지 않음"),
		
		ETC("시스템 에러"),
		
		LOGINTOKEN("로그인토큰 오류");
		
		private String message;
		
		private FailCode(String message) {
			this.message = message;
		}
		
		public String getMessage() {
			return message;
		}
		
	}
	
	public static final String KEY_RESULTYN = "RESULTYN";
	public static final String KEY_FAILCODELIST = "FAILCODELIST";
	
}