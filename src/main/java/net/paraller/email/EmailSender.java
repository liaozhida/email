package net.paraller.email;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liaozhida
 * 邮件发送类
 */
public class EmailSender {
	
	private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);
	
	private static String nickname;
	private static String username;
	private static String password;
	private static String fromAddress ;
	private static Session session = null;
	private static MimeMessage message = null;
	private static Map<String, String> _templates;
	private static String TEMPLATE_PREFIX = "mail.template.";
	
	/**
	 * 状态码 成功
	 */
	public  static int SUCC_STATE = 0;
	
	/**
	 * 状态码  失败 
	 */
	public  static int FAIL_STATE = -1;
	
	/**
	 * 群发全部失败
	 */
	public  static int BATCH_FAIL_STATE = -2;
	
	/**
	 * 群发部分失败
	 */
	public  static int BATCH_WARN_STATE = -3;
	
	/**
	 * 配置文件出错
	 */
	public static int BAD_CONFIG = -4;
	
	
	/**初始化配置信息
	 * @param properties
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public void loadProperties(Properties properties) throws Exception{
		//判断所需要的字段
		String port = properties.getProperty("mail.smtp.port");
		String host = properties.getProperty("mail.smtp.host");
		String auth = properties.getProperty("mail.smtp.auth");
		username = properties.getProperty("mail.smtp.username");
		password = properties.getProperty("mail.smtp.password");
		nickname = properties.getProperty("mail.smtp.nickname");
		fromAddress = properties.getProperty("mail.smtp.from");
		if(port == null || "".equals(port)){
			throw new Exception("配置文件缺少 port 参数	");
		}
		if(username == null || "".equals(username)){
			throw new Exception("配置文件缺少 username 参数	");
		}
		if(password == null || "".equals(password)){
			throw new Exception("配置文件缺少 password 参数	");
		}
		if(fromAddress == null || "".equals(fromAddress)){
			throw new Exception("配置文件缺少 fromAddress 参数	");
		}
		if(host == null || "".equals(host)){
			throw new Exception("配置文件缺少 host 参数	");
		}
		if(auth == null || "".equals(auth)){
			throw new Exception("配置文件缺少 auth 参数	");
		}
		if(nickname == null || "".equals(nickname)){
			throw new Exception("配置文件缺少 nickname 参数	");
		}
		// 装载模版
		_templates = new HashMap<String, String>();
		Enumeration<String> pName = (Enumeration<String>) properties.propertyNames();
		while (pName.hasMoreElements()) {
			String name = pName.nextElement();
			String value = properties.getProperty(name);
			if (name.startsWith(TEMPLATE_PREFIX)) {
				String t_key = name.substring(TEMPLATE_PREFIX.length());
				logger.debug("装载模版(" + t_key + ")[" + value + "]");
				_templates.put(t_key, value);
			}
		}
		//初始化发送器
		SmtpAuthenticator smtpAuthenticator = new SmtpAuthenticator();
		session = Session.getDefaultInstance(properties,smtpAuthenticator);
		message = new MimeMessage(session);
	}
	
	/** 发送单个邮件
	 * @param toAddress 接收的邮箱地址
	 * @param subject 主题
	 * @param content 内容
	 * @return
	 */
	public int singleEmail(String toAddress,String subject,String  content){
		
		if(message == null){
			return BAD_CONFIG;
		}
		
		try {
			message.setSubject(subject);
			message.setText(content);
			message.setFrom( new InternetAddress(fromAddress,MimeUtility.encodeText(nickname,"gb2312","b"))  );
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
			Transport.send(message);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return FAIL_STATE;
		}
		
		return SUCC_STATE;
	}
	
	/** 发送单个邮件 带模版的
	 * @param template 模版名称
	 * @param parameter 元数据
	 * @param toAddress 接收的邮箱地址
	 * @param subject 主题
	 * @param content 内容
	 * @return
	 */
	public int singleTemplateEmail(String template,	Map<String, String> parameter
			,String toAddress,String subject,String  content){
		
		if(message == null){
			return BAD_CONFIG;
		}
		
		template = _templates.get(template);
		content = PlaceHolderUtils.resolvePlaceholders(template, parameter);
		try {
			message.setSubject(subject);
			message.setFrom( new InternetAddress(fromAddress,MimeUtility.encodeText(nickname,"gb2312","b"))  );
			message.setText(content);
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
			Transport.send(message);
		} catch (Exception e) {

			logger.error(e.getMessage());
			return FAIL_STATE;
		}
		
		return SUCC_STATE;
	}
	
	
	/**简单的邮件群发
	 * @param address 接收的邮箱地址集合
	 * @param subject 主题
	 * @param content 内容
	 * @return
	 */
	public int simpleBatchEmail(List<String> address,String subject,String content){
		
		if(message == null){
			return BAD_CONFIG;
		}
		
		InternetAddress[] internetAddress=new InternetAddress[address.size()]; 
		int count = 0;
		
		try {
			for(String str : address){
				InternetAddress ia = new InternetAddress(str);
				internetAddress[count] = ia;
				count++;
			}
			message.setSubject(subject);
			message.setFrom( new InternetAddress(fromAddress,MimeUtility.encodeText(nickname,"gb2312","b"))  );
			message.setText(content);
			message.setRecipients(Message.RecipientType.TO, internetAddress);
			Transport.send(message);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return FAIL_STATE;
		}
		return SUCC_STATE;
	}
	
	

	/** 群发邮件
	 * @param toAddress
	 * @param subject
	 * @param content
	 * @return
	 */
	public int BatchEmail(List<EmailContent> emailContents){
		
		if(message == null){
			return BAD_CONFIG;
		}
		
		int failCounter = 0;
		for(EmailContent emailContent : emailContents){
			int flag = singleEmail(emailContent.getToAddress(),emailContent.getSubject(),emailContent.getContent());
			//可能的网络不稳定  导致部分失败
			if(flag == FAIL_STATE){
				logger.error("部分发送失败:内容为:"+emailContent.getToAddress()+":"+emailContent.getSubject());
				failCounter++;
			}
		}
		
		if(failCounter == 0){
			logger.error("全部发送成功");
			return SUCC_STATE;
		}else if(failCounter == emailContents.size()){
			logger.error("全部发送失败");
			return BATCH_FAIL_STATE;
		}else{
			logger.error("部分发送失败,"+failCounter+"封邮件");
			return BATCH_WARN_STATE;
		}
	}
	
	
	/**
	 * @author liaozhida
	 * 邮箱验证类
	 */
	class SmtpAuthenticator extends Authenticator {
		
		public SmtpAuthenticator() {
		    super();
		}
		
		@Override
		public PasswordAuthentication getPasswordAuthentication() {
		 String username = EmailSender.username;
		 String password = EmailSender.password;
		    if ((username != null) && (username.length() > 0) && (password != null) 
		    		&& (password.length   () > 0)) {
		        return new PasswordAuthentication(username, password);
		    }
		    return null;
		}
	}
	
	
}
