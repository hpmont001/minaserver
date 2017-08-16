package httptest;

public class Token {
    public static final String BUSI_TYPE_TCP = "TCP";
    public static final String BUSI_TYPE_HTTP = "HTTP";
    private String busiCode;    //业务码
    private String busiType;    //业务类型：TCP or HTTP
    private String busiMessage; //业务报文：TCP请求时为TCP完整报文，HTTP_POST时为报文体部分，HTTP_GET时为报文头第一行参数部分
    private String busiCharset; //报文字符集
    private String fullMessage; //原始完整报文（用于在日志中打印最初接收到的原始完整报文）
    /*-- 五个属性的setter和getter略（不包括常量） --*/
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
