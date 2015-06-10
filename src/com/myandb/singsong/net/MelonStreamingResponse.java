package com.myandb.singsong.net;

public class MelonStreamingResponse {
	
	public enum FailCode {
		
		NOPRODUCT("ȸ������ ���� �̸���⸸ �����մϴ�. ����� ��Ʈ������ ������ �̿�� ���� �� ������ּ���."),
		
		ADULTONLY("�� ���������� û�ҳ����ظ�ü���μ� -������Ÿ� �̿����� �� ������ȣ � ���� ����- �� -û�ҳ� ��ȣ��-�� ���� 19�� �̸��� û�ҳ��� �̿��� �� �����ϴ�."),
		
		NOREALNAME("�� ���������� û�ҳ����ظ�ü���μ� -������Ÿ� �̿����� �� ������ȣ � ���� ����- �� -û�ҳ� ��ȣ��-�� ���� 19�� �̸��� û�ҳ��� �̿��� �� �����ϴ�. �������� �� �̿����ּ���."),
		
		NORIGHT("��ǰ�� ������ ���� ��ȯ���谡 �����ϴ�."),
		
		NOSVC("���� �Ұ��� �������Դϴ�."),
		
		EXISTCHANNELBANNEDPROD("ä�����࿡ �ɷ� �ִ� ��ǰ�Դϴ�."),
		
		NOAVAILST("�������� ��Ʈ���� �Ұ� �����Դϴ�."),
		
		NOFREE("ȸ������ ���� �̸���⸸ �����մϴ�. ����� ��Ʈ������ ������ �̿�� ���� �� ������ּ���."),
		
		WRONGPARAMS("ȣ�� �Ķ���� ����"),
		
		VALERROR("VAL �� ���� ����"),
		
		ETC("�ý��� ����"),
		
		LOGINTOKEN("�α�����ū ����");
		
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