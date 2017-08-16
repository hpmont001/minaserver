package httptest;

public class MinaBean {
	private String content;
	private boolean isWebAccept=false;
	private boolean isWebClose=false;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}


	public boolean isWebAccept() {
		return isWebAccept;
	}
	public boolean isWebClose() {
		return isWebClose;
	}

	public void setWebAccept(boolean isWebAccept) {
		this.isWebAccept = isWebAccept;
	}
	public void setisWebClose(boolean isWebClose) {
		this.isWebClose = isWebClose;
	}

	@Override
	public String toString() {
		return "MinaBean [content=" + content + "]";
	}

}
