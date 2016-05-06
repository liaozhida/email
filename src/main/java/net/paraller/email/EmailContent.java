package net.paraller.email;

/**
 * @author liaozhida
 * 邮件的信息
 */
public class EmailContent {
	
	private String subject;
	private String content;
	//收件人地址
	private String toAddress;
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getToAddress() {
		return toAddress;
	}
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}
	@Override
	public String toString() {
		return "EmailContent [subject=" + subject + ", content=" + content
				+ ", toAddress=" + toAddress + "]";
	}
	
}



