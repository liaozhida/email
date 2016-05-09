## email

邮件发送的JAVA实现
封装了邮件发送，更方便快捷的使用

```
    EmailSender emailSender = new EmailSender();
    try {
			emailSender.loadProperties(pro);
		} catch (Exception e) {
			e.printStackTrace();
		}
    emailSender.singleEmail(toAddress1, subject, content);
```
