package httptest;

public class Token {
    public static final String BUSI_TYPE_TCP = "TCP";
    public static final String BUSI_TYPE_HTTP = "HTTP";
    private String busiCode;    //ҵ����
    private String busiType;    //ҵ�����ͣ�TCP or HTTP
    private String busiMessage; //ҵ���ģ�TCP����ʱΪTCP�������ģ�HTTP_POSTʱΪ�����岿�֣�HTTP_GETʱΪ����ͷ��һ�в�������
    private String busiCharset; //�����ַ���
    private String fullMessage; //ԭʼ�������ģ���������־�д�ӡ������յ���ԭʼ�������ģ�
    /*-- ������Ե�setter��getter�ԣ������������� --*/
	public String getBusiCode() {
		return busiCode;
	}
	public void setBusiCode(String busiCode) {
		this.busiCode = busiCode;
	}
	public String getBusiType() {
		return busiType;
	}
	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}
	public String getBusiMessage() {
		return busiMessage;
	}
	public void setBusiMessage(String busiMessage) {
		this.busiMessage = busiMessage;
	}
	public String getBusiCharset() {
		return busiCharset;
	}
	public void setBusiCharset(String busiCharset) {
		this.busiCharset = busiCharset;
	}
	public String getFullMessage() {
		return fullMessage;
	}
	public void setFullMessage(String fullMessage) {
		this.fullMessage = fullMessage;
	}

}
